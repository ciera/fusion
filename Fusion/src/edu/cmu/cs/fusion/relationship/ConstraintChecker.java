package edu.cmu.cs.fusion.relationship;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.Binding;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.Method;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.Variant;
import edu.cmu.cs.fusion.alias.AliasContext;
import edu.cmu.cs.fusion.alias.AliasDelta;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.ConstraintEnvironment;
import edu.cmu.cs.fusion.constraint.Effect;
import edu.cmu.cs.fusion.constraint.SpecDelta;
import edu.cmu.cs.fusion.constraint.SubPair;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.constraint.predicates.TruePredicate;

public class ConstraintChecker {
	private ConstraintEnvironment constraints;
	protected TypeHierarchy types;
	protected Variant variant;
	protected Method method;
	
	public ConstraintChecker(ConstraintEnvironment constraints, TypeHierarchy types, Variant var, Method method) {
		this.constraints = constraints;
		this.types = types;
		this.variant = var;
		this.method = method;
	}

	/**
	 * Run the flow function for each constraint. Join the results from each constraint together and adjust the lattice
	 * @param env
	 * @param instr
	 * @return The new relationship lattice.
	 */
	public <AC extends AliasContext> Pair<AC, RelationshipContext> runGenericTransfer(FusionEnvironment<AC> env, TACInstruction instr) {
		List<RelationshipDelta> relDeltas = new LinkedList<RelationshipDelta>();
		List<AliasDelta> aliasDeltas = new LinkedList<AliasDelta>();
		
		for (Constraint cons : constraints) {
			Pair<RelationshipDelta, AliasDelta> deltas = runSingleConstraint(env, cons, instr);
			relDeltas.add(deltas.fst());
			aliasDeltas.add(deltas.snd());
		}
		
		RelationshipDelta relDelta = RelationshipDelta.joinAlt(relDeltas);
		AliasDelta aliasDelta = AliasDelta.join(aliasDeltas);
		
		RelationshipContext relContext = env.getContext().applyChangesFromDelta(relDelta);
		AC aliasContext = env.makeNewAliases(aliasDelta);
		
		return new Pair<AC, RelationshipContext>(aliasContext, relContext);
	}

	public List<FusionErrorReport> checkForErrors(FusionEnvironment<?> env, TACInstruction instr) {
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
	protected Pair<RelationshipDelta, AliasDelta> runSingleConstraint(FusionEnvironment<?> env,
			Constraint cons, TACInstruction instr) {
		ConsList<Binding> boundVars = cons.getOp().matches(types, method, instr);
		List<RelationshipDelta> relDeltas = new LinkedList<RelationshipDelta>();
		List<AliasDelta> specDeltas = new LinkedList<AliasDelta>();
		
		if (boundVars == null)
			return new Pair<RelationshipDelta, AliasDelta>(new RelationshipDelta(), new AliasDelta());
		
		List<Substitution> subs = env.findLabels(boundVars, cons.getFreeVarsExceptReqs());
		
		for (Substitution sub : subs) {
			Pair<RelationshipDelta, SpecDelta> deltas = runFullyBound(env, sub, cons);
			relDeltas.add(deltas.fst());
			specDeltas.add(deltas.snd().turnToSource(boundVars));
		}
		
		RelationshipDelta relDelta = RelationshipDelta.join(relDeltas);
		AliasDelta specDelta = AliasDelta.join(specDeltas);
		
		return new Pair<RelationshipDelta, AliasDelta>(relDelta, specDelta);
	}

	/**
	 * Check a constraint with the fully bound substitutions
	 * @param env The stating lattices
	 * @param partialSubs The substitutions for FV(cons) - FV(req)
	 * @param cons The constraint to check
	 * @param instr The instruction to report errors on
	 * @return Any change effects
	 */
	protected Pair<RelationshipDelta, SpecDelta> runFullyBound(FusionEnvironment<?> env, Substitution partialSubs, Constraint cons) {	
		
		ThreeValue trigger = cons.getTrigger().getTruth(env, partialSubs);
		
		if (trigger == ThreeValue.FALSE) {
			return new Pair<RelationshipDelta, SpecDelta>(new RelationshipDelta(), SpecDelta.createBottomSpecDelta(partialSubs));
		}
		else  {
			RelationshipDelta delta = new RelationshipDelta();
			SpecDelta specs;
			
			//now make the effects
			for (Effect effect : cons.getEffects())
				delta.override(effect.makeEffects(env, partialSubs));
			
			if (trigger == ThreeValue.TRUE) {
				specs = SpecDelta.createSubstitutionSpecDelta(partialSubs);
			}
			else {
				delta = delta.polarize();
			
				if (variant.isSound())
					specs = SpecDelta.createTopSpecDelta(partialSubs);
				else
					specs = SpecDelta.createBottomSpecDelta(partialSubs);
			}
			
			return new Pair<RelationshipDelta, SpecDelta>(delta, specs);
		}
	}
	
	
	
	protected FusionErrorReport checkSingleConstraint(FusionEnvironment<?> env, Constraint cons, TACInstruction instr) {
		ConsList<Binding> boundVars = cons.getOp().matches(types, method, instr);

		if (boundVars == null)
			return null;

		List<Substitution> subs = env.findLabels(boundVars, cons.getFreeVarsExceptReqs());
		
		if (subs.isEmpty())
			return null;
		
		List<Substitution> failingSubs = new LinkedList<Substitution>();
		
		for (Substitution sub : subs) {
			if (checkFullyBound(env, sub, cons))
				failingSubs.add(sub);
		}
		
		if (!failingSubs.isEmpty())
			return new FusionErrorReport(cons, failingSubs, env);
		else
			return null;
	}

	protected boolean checkFullyBound(FusionEnvironment<?> env, Substitution partialSubs, Constraint cons) {		
		ThreeValue trigger = cons.getTrigger().getTruth(env, partialSubs);
		
		if (trigger == ThreeValue.FALSE) {
			return false;
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
				return false;
			}
			else {
				return checkSoundly(env, partialSubs, cons);
			}
		}
	}

	private boolean checkSoundly(FusionEnvironment<?> env,
			Substitution partialSubs, Constraint cons) {
		SubPair pair = env.allValidSubs(partialSubs, cons.getFreeVars());
		
		Iterator<Substitution> itr = pair.getDefiniteSubstitutions();
		while (itr.hasNext()) {
			Substitution fullSub = itr.next();
			ThreeValue req = cons.getRequires().getTruth(env, fullSub);
			if (req == ThreeValue.TRUE) 
				return false;
		}
		return true;
	}

	private boolean checkCompletely(FusionEnvironment<?> env,
			Substitution partialSubs, Constraint cons) {
		SubPair pair = env.allValidSubs(partialSubs, cons.getFreeVars());
		
		Iterator<Substitution> itr = pair.getDefiniteSubstitutions();
		while (itr.hasNext()) {
			Substitution fullSub = itr.next();
			ThreeValue req = cons.getRequires().getTruth(env, fullSub);
			if (req != ThreeValue.FALSE) 
				return false;
		}
		itr = pair.getPossibleSubstitutions();
		while (itr.hasNext()) {
			Substitution fullSub = itr.next();
			ThreeValue req = cons.getRequires().getTruth(env, fullSub);
			if (req != ThreeValue.FALSE) 
				return false;
		}
		return true;
	}


}
