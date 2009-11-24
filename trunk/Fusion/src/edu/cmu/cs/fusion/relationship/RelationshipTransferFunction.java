package edu.cmu.cs.fusion.relationship;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.flow.BooleanLabel;
import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.flow.LabeledResult;
import edu.cmu.cs.crystal.flow.LabeledSingleResult;
import edu.cmu.cs.crystal.tac.AbstractTACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.model.MethodCallInstruction;
import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.crystal.util.typehierarchy.CachedTypeHierarchy;
import edu.cmu.cs.fusion.AliasContext;
import edu.cmu.cs.fusion.BooleanConstantWrapper;
import edu.cmu.cs.fusion.BooleanContext;
import edu.cmu.cs.fusion.FusionAnalysis;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.FusionErrorStorage;
import edu.cmu.cs.fusion.FusionException;
import edu.cmu.cs.fusion.MayAliasWrapper;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.Variant;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.ConstraintEnvironment;
import edu.cmu.cs.fusion.constraint.Effect;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.SubPair;
import edu.cmu.cs.fusion.constraint.Substitution;


public class RelationshipTransferFunction extends AbstractTACBranchSensitiveTransferFunction<RelationshipContext> {

	private FusionAnalysis mainAnalysis;
	private ConstraintEnvironment constraints;
	protected TypeHierarchy types;
	private InferenceEnvironment infers;
	private FusionErrorStorage errors;
	
	/**
	 * Do not use, only for testing purposes.
	 * @param relAnalysis
	 * @param variant
	 * @throws FusionException
	 */
	public RelationshipTransferFunction(FusionAnalysis relAnalysis, FusionErrorStorage errors) throws FusionException {
		mainAnalysis = relAnalysis;
		this.errors = errors;
	}
	
	public RelationshipTransferFunction(FusionAnalysis relAnalysis, FusionErrorStorage errors, ConstraintEnvironment constraints, InferenceEnvironment inf, Variant variant, IJavaProject project, IProgressMonitor monitor) throws FusionException {
		mainAnalysis = relAnalysis;
		this.errors = errors;
		this.constraints = constraints;
		this.infers = inf;
		try {
			types = new CachedTypeHierarchy(project, monitor);
			FreeVars.setHierarchy(types);
		} catch (JavaModelException e) {
			throw new FusionException("Could not create type hierarchy", e);
		}
	}

	public RelationshipContext createEntryValue(MethodDeclaration method) {
		return new RelationshipContext(false);
	}

	public ILatticeOperations<RelationshipContext> getLatticeOperations() {
		return new RelationshipLatticeOperations();
	}

	@Override
	public IResult<RelationshipContext> transfer(MethodCallInstruction instr,
			List<ILabel> labels, RelationshipContext value) {
		//some options:
		//pass in two alias contexts: 1 for before/result, 1 for effects?
		//no, I should be able to just use the after values. The receiver should not have been
		//anywhere else in the expression in a TAC system....right? Can I do x = x.foo()? What about x = y.bar(a, a) (though that has no affect...)
		//if not, use the after value.
		//		However, consider how this might affect reference equality predicates. I suppose since result shouldn't be used
		//		in trigger or predicate, this won't be an issue. Doing that would be dirty.
		//if so: 
		//what if we let the freevars decide this? Upon getting free vars, also state whether it is a "before" or "after"  variable.
		//trig and req produce before. effect produces afters for * and result, but before otherwise?
		//op produces before for everything except result?
		//
		//For now, I'm just going to assume that we use the after value, and result isn't allowed in the trigger or requires preds.

		
		
		//run twice: once assuming return is false, and again assuming return is true.
		AliasContext aContext = new MayAliasWrapper(instr, mainAnalysis.getAliasAnalysis());
		if (labels.contains(BooleanLabel.getBooleanLabel(true)) && labels.contains(BooleanLabel.getBooleanLabel(false))) {
			BooleanContext tBContext = new BooleanConstantWrapper(instr, mainAnalysis.getBooleanAnalysis(), mainAnalysis.getAliasAnalysis(), true);
			FusionEnvironment tEnv = new FusionEnvironment(aContext, value, tBContext, types, infers);
			RelationshipContext tNewContext = genericFlowFunction(tEnv, instr);
			
			BooleanContext fBContext = new BooleanConstantWrapper(instr, mainAnalysis.getBooleanAnalysis(), mainAnalysis.getAliasAnalysis(), false);
			FusionEnvironment fEnv = new FusionEnvironment(aContext, value, fBContext, types, infers);
			RelationshipContext fNewContext = genericFlowFunction(fEnv, instr);
			
			LabeledResult<RelationshipContext> result = LabeledResult.createResult(labels, new RelationshipContext(false));
			result.put(BooleanLabel.getBooleanLabel(true), tNewContext);
			result.put(BooleanLabel.getBooleanLabel(false), fNewContext);
			return result;
		}
		else {
			BooleanContext bContext = new BooleanConstantWrapper(instr, mainAnalysis.getBooleanAnalysis(), mainAnalysis.getAliasAnalysis());
			FusionEnvironment env = new FusionEnvironment(aContext, value, bContext, types, infers);
			RelationshipContext newContext = genericFlowFunction(env, instr);
			
			return LabeledSingleResult.createResult(newContext);
		}
	}

	/**
	 * Run the flow function for each constraint. Join the results from each constraint together and adjust the lattice
	 * @param env
	 * @param instr
	 * @return The new relationship lattice.
	 */
	protected RelationshipContext genericFlowFunction(FusionEnvironment env, TACInstruction instr) {
		List<RelationshipDelta> consDeltas = new LinkedList<RelationshipDelta>();
		RelationshipDelta delta;
		
		for (Constraint cons : constraints) {
			delta = checkSingleConstraint(env, cons, instr);
			consDeltas.add(delta);
		}
		
		delta = RelationshipDelta.join(consDeltas);
		return env.getContext().applyChangesFromDelta(delta);
	}

	/**
	 * @param env The alias and relation lattices
	 * @param cons The constraint to check
	 * @param instr The instruction we are checking the constraint on
	 * @return a relationship delta for all possible aliasing configurations, assuming the constraint
	 * triggers.
	 */
	protected RelationshipDelta checkSingleConstraint(FusionEnvironment env,
			Constraint cons, TACInstruction instr) {
		ConsList<Pair<SpecVar, Variable>> boundVars = cons.getOp().matches(types, instr);
		List<RelationshipDelta> deltas = new LinkedList<RelationshipDelta>();
		RelationshipDelta delta;
		
		if (boundVars == null)
			return new RelationshipDelta();
		
		SubPair pairs = env.findLabels(boundVars, cons.getFreeVarsExceptReqs());
		Iterator<Substitution> itr;
		
		itr = pairs.getDefiniteSubstitutions();
		while (itr.hasNext()) {
			delta = checkPartialBound(env, itr.next(), cons, instr);
			deltas.add(delta);
		}

		itr = pairs.getPossibleSubstitutions();
		while (itr.hasNext()) {
			delta = checkPartialBound(env, itr.next(), cons, instr);
			deltas.add(delta.polarize());
		}
		
		return RelationshipDelta.equalityJoin(deltas);
	}

	protected RelationshipDelta checkPartialBound(FusionEnvironment env,
			Substitution boundSubs, Constraint cons, TACInstruction instr) {
		
		List<RelationshipDelta> deltas = new LinkedList<RelationshipDelta>();
		RelationshipDelta delta;
		SubPair pair = env.allValidSubs(boundSubs, cons.getFreeVarsExceptReqs());

		Iterator<Substitution> itr;
		
		itr = pair.getDefiniteSubstitutions();
		while (itr.hasNext()) {
			delta = checkFullyBound(env, itr.next(), cons, instr);
			deltas.add(delta);
		}

		itr = pair.getPossibleSubstitutions();
		while (itr.hasNext()) {
			delta = checkFullyBound(env, itr.next(), cons, instr);
			deltas.add(delta.polarize());
		}
		
		return RelationshipDelta.equalityJoin(deltas);
	}

	/**
	 * Check a constraint with the fully bound substitutions
	 * @param env The stating lattices
	 * @param partialSubs The substitutions for FV(cons) - FV(req)
	 * @param cons The constraint to check
	 * @param instr The instruction to report errors on
	 * @return Any change effects
	 */
	protected RelationshipDelta checkFullyBound(FusionEnvironment env, Substitution partialSubs, Constraint cons, TACInstruction instr) {	
		RelationshipDelta delta;
		int error = 0;
		boolean definitelyPasses = false;
		boolean possiblyPasses = false;
		
		ThreeValue trigger = cons.getTrigger().getTruth(env, partialSubs);


		if (trigger != ThreeValue.FALSE) {
		
			SubPair pair = env.allValidSubs(partialSubs, cons.getFreeVars());
			
			//check all three for definite substitutions
			Iterator<Substitution> itr = pair.getDefiniteSubstitutions();
			definitelyPasses = false;
			possiblyPasses = false;
			while (itr.hasNext() && !definitelyPasses) {
				Substitution fullSub = itr.next();
				ThreeValue req = cons.getRequires().getTruth(env, fullSub);
				if (req == ThreeValue.TRUE) 
					definitelyPasses = possiblyPasses = true; //something was found which passed
				else if (req == ThreeValue.UNKNOWN)
					possiblyPasses = true;					//it's possible that something passed
			}
			
			//if nothing passes, also check possible subs for the complete variant
			itr = pair.getPossibleSubstitutions();
			while (itr.hasNext() && !possiblyPasses) {
				Substitution fullSub = itr.next();
				ThreeValue req = cons.getRequires().getTruth(env, fullSub);
				if (req != ThreeValue.FALSE)
					possiblyPasses = true;
			}		
			
			
			if (trigger == ThreeValue.TRUE) {
				if (!possiblyPasses) //nothing passed! Errors all around
					error = Variant.SOUND | Variant.COMPLETE | Variant.PRAGMATIC;
				else if (!definitelyPasses) //was no definite pass, errors for sound and prag
					error = Variant.SOUND | Variant.PRAGMATIC;
			}
			else if (!definitelyPasses) { //trg = unknown and no req passes with a true
					error = Variant.SOUND;
			}
			
			//report errors
			if (error != 0)
				errors.addError(new FusionErrorReport(new Variant(error), cons, instr));
			
			//now make the effects
			List<RelationshipDelta> eDeltas = new LinkedList<RelationshipDelta>();
			for (Effect effect : cons.getEffects())
				eDeltas.add(effect.makeEffects(env, partialSubs));
			delta = RelationshipDelta.join(eDeltas);
			if (trigger == ThreeValue.UNKNOWN)
				delta = delta.polarize();
		}
		else {
			delta = new RelationshipDelta();
		}
		
		
		return delta;
	}
}
