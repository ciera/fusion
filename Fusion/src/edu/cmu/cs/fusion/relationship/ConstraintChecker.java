package edu.cmu.cs.fusion.relationship;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.Binding;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.Variant;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.ConstraintEnvironment;
import edu.cmu.cs.fusion.constraint.Effect;
import edu.cmu.cs.fusion.constraint.SubPair;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.constraint.predicates.TruePredicate;

public class ConstraintChecker {
	private ConstraintEnvironment constraints;
	protected TypeHierarchy types;
	protected Variant variant;
	
	public ConstraintChecker(ConstraintEnvironment constraints, TypeHierarchy types, Variant var) {
		this.constraints = constraints;
		this.types = types;
		this.variant = var;
	}

	/**
	 * Run the flow function for each constraint. Join the results from each constraint together and adjust the lattice
	 * @param env
	 * @param instr
	 * @return The new relationship lattice.
	 */
	public RelationshipContext runGenericTransfer(FusionEnvironment env, TACInstruction instr) {
		List<RelationshipDelta> consDeltas = new LinkedList<RelationshipDelta>();
		RelationshipDelta delta;
		
		for (Constraint cons : constraints) {
			if (!cons.getEffects().isEmpty()) {
				delta = runSingleConstraint(env, cons, instr);
				consDeltas.add(delta);
			}
		}
		
		delta = RelationshipDelta.join(consDeltas);
		return env.getContext().applyChangesFromDelta(delta);
	}

	public List<FusionErrorReport> checkForErrors(FusionEnvironment env, TACInstruction instr) {
		List<FusionErrorReport> errors = new LinkedList<FusionErrorReport>();
		FusionErrorReport err;
		
		for (Constraint cons : constraints) {
			if (!(cons.getRequires() instanceof TruePredicate)) {
				err = checkSingleConstraint(env, cons, instr);
				if (err != null)
					errors.add(err);
			}
		}
		
		return errors;
	}

	/**
	 * @param env The alias and relation lattices
	 * @param cons The constraint to check
	 * @param instr The instruction we are checking the constraint on
	 * @return a relationship delta for all possible aliasing configurations, assuming the constraint
	 * triggers.
	 */
	protected RelationshipDelta runSingleConstraint(FusionEnvironment env,
			Constraint cons, TACInstruction instr) {
		ConsList<Binding> boundVars = cons.getOp().matches(types, instr);
		List<RelationshipDelta> deltas = new LinkedList<RelationshipDelta>();
		RelationshipDelta delta;
		
		if (boundVars == null)
			return new RelationshipDelta();
		
		SubPair pairs = env.findLabels(boundVars, cons.getFreeVarsExceptReqs());
		Iterator<Substitution> itr;
		
		itr = pairs.getDefiniteSubstitutions();
		while (itr.hasNext()) {
			delta = runPartialBound(env, itr.next(), cons);
			deltas.add(delta);
		}

		itr = pairs.getPossibleSubstitutions();
		while (itr.hasNext()) {
			delta = runPartialBound(env, itr.next(), cons);
			deltas.add(delta.polarize());
		}
		
		return RelationshipDelta.equalityJoin(deltas);
	}
	
	protected FusionErrorReport checkSingleConstraint(FusionEnvironment env, Constraint cons, TACInstruction instr) {
		ConsList<Binding> boundVars = cons.getOp().matches(types, instr);

		if (boundVars == null)
			return null;

		SubPair pairs = env.findLabels(boundVars, cons.getFreeVarsExceptReqs());
		
		if (pairs.numberOfSubstitutions() == 0)
			return null;
		
		Iterator<Substitution> itr;
		List<Substitution> failingSubs = new LinkedList<Substitution>();
		
		itr = pairs.getDefiniteSubstitutions();
		while (itr.hasNext()) {
			List<Substitution> fails = checkPartialBound(env, itr.next(), cons);
			failingSubs.addAll(fails);
		}

		itr = pairs.getPossibleSubstitutions();
		while (itr.hasNext()) {
			List<Substitution> fails = checkPartialBound(env, itr.next(), cons);
			failingSubs.addAll(fails);
		}
		
		if (!failingSubs.isEmpty())
			return new FusionErrorReport(cons, failingSubs, env);
		else
			return null;
	}
	
	protected List<Substitution> checkPartialBound(FusionEnvironment env,
			Substitution boundSubs, Constraint cons) {
		
		List<Substitution> failingSubs = new LinkedList<Substitution>();
		SubPair pair = env.allValidSubs(boundSubs, cons.getFreeVarsExceptReqs());

		Iterator<Substitution> itr;
		
		itr = pair.getDefiniteSubstitutions();
		while (itr.hasNext()) {
			Substitution error = checkFullyBound(env, itr.next(), cons);
			if (error != null)
				failingSubs.add(error);
		}

		itr = pair.getPossibleSubstitutions();
		while (itr.hasNext()) {
			Substitution error = checkFullyBound(env, itr.next(), cons);
			if (error != null)
				failingSubs.add(error);
		}
		
		return failingSubs;
	}

	protected Substitution checkFullyBound(FusionEnvironment env, Substitution partialSubs, Constraint cons) {		
		ThreeValue trigger = cons.getTrigger().getTruth(env, partialSubs);
		
		if (trigger == ThreeValue.FALSE) {
			return null;
		}
		else if (trigger == ThreeValue.TRUE) {
			if (variant.isComplete()) {
				return checkCompletely(env, partialSubs, cons);
			}
			else {
				return checkSoundly(env, partialSubs, cons);
			}
		}
		else {
			if (variant.isComplete() || variant.isPragmatic()) {
				return null;
			}
			else {
				return checkSoundly(env, partialSubs, cons);
			}
		}
	}

	private Substitution checkSoundly(FusionEnvironment env,
			Substitution partialSubs, Constraint cons) {
		SubPair pair = env.allValidSubs(partialSubs, cons.getFreeVars());
		
		Iterator<Substitution> itr = pair.getDefiniteSubstitutions();
		while (itr.hasNext()) {
			Substitution fullSub = itr.next();
			ThreeValue req = cons.getRequires().getTruth(env, fullSub);
			if (req == ThreeValue.TRUE) 
				return null;
		}
		return partialSubs;
	}

	private Substitution checkCompletely(FusionEnvironment env,
			Substitution partialSubs, Constraint cons) {
		SubPair pair = env.allValidSubs(partialSubs, cons.getFreeVars());
		
		Iterator<Substitution> itr = pair.getDefiniteSubstitutions();
		while (itr.hasNext()) {
			Substitution fullSub = itr.next();
			ThreeValue req = cons.getRequires().getTruth(env, fullSub);
			if (req != ThreeValue.FALSE) 
				return null;
		}
		itr = pair.getPossibleSubstitutions();
		while (itr.hasNext()) {
			Substitution fullSub = itr.next();
			ThreeValue req = cons.getRequires().getTruth(env, fullSub);
			if (req != ThreeValue.FALSE) 
				return null;
		}
		return partialSubs;
	}

	protected RelationshipDelta runPartialBound(FusionEnvironment env, Substitution boundSubs, Constraint cons) {
		
		List<RelationshipDelta> deltas = new LinkedList<RelationshipDelta>();
		RelationshipDelta delta;
		SubPair pair = env.allValidSubs(boundSubs, cons.getFreeVarsExceptReqs());

		Iterator<Substitution> itr;
		
		itr = pair.getDefiniteSubstitutions();
		while (itr.hasNext()) {
			delta = runFullyBound(env, itr.next(), cons);
			deltas.add(delta);
		}

		itr = pair.getPossibleSubstitutions();
		while (itr.hasNext()) {
			delta = runFullyBound(env, itr.next(), cons);
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
	protected RelationshipDelta runFullyBound(FusionEnvironment env, Substitution partialSubs, Constraint cons) {	
		
		ThreeValue trigger = cons.getTrigger().getTruth(env, partialSubs);
		
		if (trigger == ThreeValue.FALSE) {
			return new RelationshipDelta();
		}
		else  {
			RelationshipDelta delta;
			//instead of below:
			//if (trigger == ThreeValue.TRUE)
			//	make effects, including aliasing
			//if (trigger == UNKNOWN
			// make effects, no aliasing
			//	if sound
			//		make aliasing
			
			
			//now make the effects
			List<RelationshipDelta> eDeltas = new LinkedList<RelationshipDelta>();
			for (Effect effect : cons.getEffects())
				eDeltas.add(effect.makeEffects(env, partialSubs));
			delta = RelationshipDelta.join(eDeltas);
			
			if (trigger == ThreeValue.UNKNOWN)
				delta = delta.polarize(); //polarize should also remove any update changes
			
			return delta;
		}
	}
}
