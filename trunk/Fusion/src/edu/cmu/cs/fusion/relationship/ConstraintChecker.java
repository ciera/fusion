package edu.cmu.cs.fusion.relationship;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.Variant;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.ConstraintEnvironment;
import edu.cmu.cs.fusion.constraint.Effect;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.SubPair;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.constraint.predicates.TruePredicate;

public class ConstraintChecker {
	private ConstraintEnvironment constraints;
	protected TypeHierarchy types;
	
	public ConstraintChecker(ConstraintEnvironment constraints, TypeHierarchy types) {
		this.constraints = constraints;
		this.types = types;
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
		ConsList<Pair<SpecVar, Variable>> boundVars = cons.getOp().matches(types, instr);
		List<RelationshipDelta> deltas = new LinkedList<RelationshipDelta>();
		RelationshipDelta delta;
		
		if (boundVars == null)
			return new RelationshipDelta();
		
		SubPair pairs = env.findLabels(boundVars, cons.getFreeVarsExceptReqs());
		Iterator<Substitution> itr;
		
		itr = pairs.getDefiniteSubstitutions();
		while (itr.hasNext()) {
			delta = runPartialBound(env, itr.next(), cons, instr);
			deltas.add(delta);
		}

		itr = pairs.getPossibleSubstitutions();
		while (itr.hasNext()) {
			delta = runPartialBound(env, itr.next(), cons, instr);
			deltas.add(delta.polarize());
		}
		
		return RelationshipDelta.equalityJoin(deltas);
	}
	
	protected FusionErrorReport checkSingleConstraint(FusionEnvironment env, Constraint cons, TACInstruction instr) {
		ConsList<Pair<SpecVar, Variable>> boundVars = cons.getOp().matches(types, instr);

		if (boundVars == null)
			return null;

		SubPair pairs = env.findLabels(boundVars, cons.getFreeVarsExceptReqs());
		
		if (pairs.numberOfSubstitutions() == 0)
			return null;
		
		Iterator<Substitution> itr;
		Variant err = new Variant(0);
		List<Substitution> failingSubs = new LinkedList<Substitution>();
		
		itr = pairs.getDefiniteSubstitutions();
		while (itr.hasNext()) {
			Pair<Variant, List<Substitution>> error = checkPartialBound(env, itr.next(), cons, instr);
			err = err.merge(error.fst());
			if (!error.fst().noError())
				failingSubs.addAll(error.snd());
		}

		itr = pairs.getPossibleSubstitutions();
		while (itr.hasNext()) {
			Pair<Variant, List<Substitution>> error = checkPartialBound(env, itr.next(), cons, instr);
			err = err.merge(error.fst());
			if (!error.fst().noError())
				failingSubs.addAll(error.snd());
		}
		
		if (!err.noError())
			return new FusionErrorReport(err, cons, failingSubs, env);
		else
			return null;
	}
	
	protected Pair<Variant, List<Substitution>> checkPartialBound(FusionEnvironment env,
			Substitution boundSubs, Constraint cons, TACInstruction instr) {
		
		Variant err = new Variant(0);
		List<Substitution> failingSubs = new LinkedList<Substitution>();
		SubPair pair = env.allValidSubs(boundSubs, cons.getFreeVarsExceptReqs());

		Iterator<Substitution> itr;
		
		itr = pair.getDefiniteSubstitutions();
		while (itr.hasNext()) {
			Pair<Variant, Substitution> error = checkFullyBound(env, itr.next(), cons, instr);
			err = err.merge(error.fst());
			if (!error.fst().noError())
				failingSubs.add(error.snd());
		}

		itr = pair.getPossibleSubstitutions();
		while (itr.hasNext()) {
			Pair<Variant, Substitution> error = checkFullyBound(env, itr.next(), cons, instr);
			err = err.merge(error.fst());
			if (!error.fst().noError())
				failingSubs.add(error.snd());
		}
		
		return new Pair<Variant, List<Substitution>>(err, failingSubs);
	}

	protected Pair<Variant, Substitution> checkFullyBound(FusionEnvironment env, Substitution partialSubs, Constraint cons, TACInstruction instr) {	
		int error = 0;
		boolean definitelyPasses = false;
		boolean possiblyPasses = false;
		Substitution failingSubs = null;
		
		ThreeValue trigger = cons.getTrigger().getTruth(env, partialSubs);

		if (trigger != ThreeValue.FALSE) {
			failingSubs = partialSubs;
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
		}

		return new Pair<Variant, Substitution>(new Variant(error), failingSubs);
	}

	protected RelationshipDelta runPartialBound(FusionEnvironment env,
			Substitution boundSubs, Constraint cons, TACInstruction instr) {
		
		List<RelationshipDelta> deltas = new LinkedList<RelationshipDelta>();
		RelationshipDelta delta;
		SubPair pair = env.allValidSubs(boundSubs, cons.getFreeVarsExceptReqs());

		Iterator<Substitution> itr;
		
		itr = pair.getDefiniteSubstitutions();
		while (itr.hasNext()) {
			delta = runFullyBound(env, itr.next(), cons, instr);
			deltas.add(delta);
		}

		itr = pair.getPossibleSubstitutions();
		while (itr.hasNext()) {
			delta = runFullyBound(env, itr.next(), cons, instr);
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
	protected RelationshipDelta runFullyBound(FusionEnvironment env, Substitution partialSubs, Constraint cons, TACInstruction instr) {	
		RelationshipDelta delta;
		
		ThreeValue trigger = cons.getTrigger().getTruth(env, partialSubs);

		if (trigger != ThreeValue.FALSE) {			
			//now make the effects
			List<RelationshipDelta> eDeltas = new LinkedList<RelationshipDelta>();
			for (Effect effect : cons.getEffects())
				eDeltas.add(effect.makeEffects(env, partialSubs));
			delta = RelationshipDelta.join(eDeltas);
			
			if (trigger == ThreeValue.UNKNOWN)
				delta = delta.polarize();
		}
		else
			delta = new RelationshipDelta();	
		
		return delta;
	}
}
