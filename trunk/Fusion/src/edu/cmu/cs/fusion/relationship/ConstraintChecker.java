package edu.cmu.cs.fusion.relationship;

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
		
		RelationshipDelta relDelta;
		AliasDelta aliasDelta;
		
		//try to avoid calling joins
		if (relDeltas.isEmpty()) {
			relDelta = new RelationshipDelta();
			aliasDelta = new AliasDelta();
		}
		else if (relDeltas.size() == 1) {
			relDelta = relDeltas.get(0);
			aliasDelta = aliasDeltas.get(0);
		}
		else {
			relDelta = RelationshipDelta.joinAlt(relDeltas);
			aliasDelta = AliasDelta.join(aliasDeltas);
		}
		
		RelationshipContext relContext = env.getContext().applyChangesFromDelta(relDelta);
		AC aliasContext = env.makeNewAliases(aliasDelta);
		
		return new Pair<AC, RelationshipContext>(aliasContext, relContext);
	}

	public List<FusionErrorReport> checkForErrors(FusionEnvironment<?> triggerEnv, TACInstruction instr) {
		List<FusionErrorReport> errors = new LinkedList<FusionErrorReport>();
		FusionErrorReport err;
		
		assert(instr != null);
		
		for (Constraint cons : constraints) {
			if (!(cons.getRequires() instanceof TruePredicate && cons.getRestrict() instanceof TruePredicate)) {
				err = checkSingleConstraint(triggerEnv, cons, instr);
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
		
		if (boundVars == null)
			return new Pair<RelationshipDelta, AliasDelta>(new RelationshipDelta(), new AliasDelta());
		
		List<RelationshipDelta> relDeltas = new LinkedList<RelationshipDelta>();
		List<Substitution> changeSubs = new LinkedList<Substitution>();
		List<Substitution> subs = env.findLabels(boundVars, cons.getUniversalFreeVars());
		
		for (Substitution sub : subs) {
			Pair<RelationshipDelta, Substitution> deltas = runFullyBound(env, sub, cons);
			relDeltas.add(deltas.fst());
			changeSubs.add(deltas.snd());
		}
		
		RelationshipDelta relDelta;
		AliasDelta aliasDelta;
			
		if (relDeltas.isEmpty()) {
			relDelta = RelationshipDelta.getTrueBottom();
			aliasDelta = new AliasDelta();
		}
		else if (relDeltas.size() == 1) {
			relDelta = relDeltas.get(0);
			aliasDelta = new AliasDelta(boundVars, changeSubs);
		}
		else {
			relDelta = RelationshipDelta.join(relDeltas);
			aliasDelta = new AliasDelta(boundVars, changeSubs);
		}
			
		return new Pair<RelationshipDelta, AliasDelta>(relDelta, aliasDelta);
	}

	
	/**
	 * 
	 * @param env
	 * @param cons
	 * @param instr
	 * @return The error report from this constraint, or null if there were no errors.
	 */
	protected FusionErrorReport checkSingleConstraint(FusionEnvironment<?> triggerEnv, Constraint cons, TACInstruction instr) {
		ConsList<Binding> boundVars = cons.getOp().matches(types, method, instr);

		if (boundVars == null)
			return null;

		List<Substitution> subs = triggerEnv.findLabels(boundVars, cons.getUniversalFreeVars());
		
		List<Substitution> failingSubs = new LinkedList<Substitution>();		
		
		boolean hasGoodSub = false;
		for (Substitution sub : subs) {
			ThreeValue trigger = cons.getTrigger().getTruth(triggerEnv, sub);
			if (isGoodSubs(trigger, triggerEnv, sub, cons)) {			
				if (checkFullyBound(triggerEnv, sub, cons))
					failingSubs.add(sub);
				hasGoodSub = true;
			}
		}
				
		if (!failingSubs.isEmpty())
			return new FusionErrorReport(cons, failingSubs, triggerEnv, false);
		else if (!subs.isEmpty() && !hasGoodSub)
			return new FusionErrorReport(cons, failingSubs, triggerEnv, true); 		
		else
			return null;
	}

	/**
	 * Check a constraint with the fully bound substitutions
	 * @param env The stating lattices
	 * @param partialSubs The substitutions for FV(cons) - FV(req)
	 * @param cons The constraint to check
	 * @param instr The instruction to report errors on
	 * @return Any change effects
	 */
	protected Pair<RelationshipDelta, Substitution> runFullyBound(FusionEnvironment<?> env, Substitution partialSubs, Constraint cons) {	
		ThreeValue trigger = cons.getTrigger().getTruth(env, partialSubs);
		Substitution keepSub = isGoodSubs(trigger, env, partialSubs, cons) ? partialSubs : null;
		RelationshipDelta delta;
		
		if (trigger == ThreeValue.FALSE) {
			delta = RelationshipDelta.getTrueBottom();
		}
		else  {
			delta = new RelationshipDelta();
			
			//now make the effects
			for (Effect effect : cons.getEffects())
				delta.override(effect.makeEffects(env, partialSubs));

			if (trigger == ThreeValue.UNKNOWN) {
				delta = delta.polarize();
			}
		}
		return new Pair<RelationshipDelta, Substitution>(delta, keepSub);
	}


	private boolean isGoodSubs(ThreeValue trigger, FusionEnvironment<?> env, Substitution partialSubs, Constraint cons) {
		
		if (trigger == ThreeValue.FALSE || trigger == ThreeValue.UNKNOWN) {
			return true;
		}
		else {
			if (cons.getRestrict() instanceof TruePredicate)
				return true;
			List<Substitution> subs = env.allValidSubs(partialSubs, cons.getFreeVars());
				
			for (Substitution sub : subs) {
				ThreeValue restrict = cons.getRestrict().getTruth(env, sub);
				
				if ((restrict == ThreeValue.TRUE) || (restrict == ThreeValue.UNKNOWN && !variant.isPragmatic()))
					return true;
			}
			return false;
		}
	}

	/**
	 * 
	 * @param triggerEnv
	 * @param postRestrict
	 * @param partialSubs
	 * @param cons
	 * @return True if there is an error, false if there is not an error
	 */
	protected boolean checkFullyBound(FusionEnvironment<?> triggerEnv, Substitution partialSubs, Constraint cons) {			
		ThreeValue trigger = cons.getTrigger().getTruth(triggerEnv, partialSubs);
		
		if (trigger == ThreeValue.FALSE)
			return false;
		else if (trigger == ThreeValue.UNKNOWN){
			if (variant.isComplete() || variant.isPragmatic()) 
				return false;
			else
				return checkSoundly(triggerEnv, partialSubs, cons);
		}
		else  {
			if (variant.isComplete())
				return checkCompletely(triggerEnv, partialSubs, cons);
			else if (variant.isSound())
				return checkSoundly(triggerEnv, partialSubs, cons);
			else
				return checkPragmatically(triggerEnv, partialSubs, cons);
		}
	}

	/**
	 * @return true if there is an error, false if it passes
	 */
	private boolean checkPragmatically(FusionEnvironment<?> env,
			Substitution partialSubs, Constraint cons) {		
		List<Substitution> subs = env.allValidSubs(partialSubs, cons.getFreeVars());
		
		for (Substitution fullSub : subs) {
			ThreeValue req = cons.getRequires().getTruth(env, fullSub);
			if (req == ThreeValue.TRUE) 
				return false;
		}
		return true;
	}

	/**
	 * @return true if there is an error, false if it passes
	 */
	private boolean checkSoundly(FusionEnvironment<?> env,
			Substitution partialSubs, Constraint cons) {
		List<Substitution> subs = env.allValidSubs(partialSubs, cons.getFreeVars());
		
		if (subs.isEmpty())
			return true;
		
		for (Substitution fullSub : subs) {
			ThreeValue req = cons.getRequires().getTruth(env, fullSub);
			if (req != ThreeValue.TRUE) 
				return true;
		}
		return false;
	}
	
	/**
	 * @return true if there is an error, false if it passes
	 */
	private boolean checkCompletely(FusionEnvironment<?> env,
			Substitution partialSubs, Constraint cons) {
		List<Substitution> subs = env.allValidSubs(partialSubs, cons.getFreeVars());
		
		for (Substitution fullSub : subs) {
			ThreeValue req = cons.getRequires().getTruth(env, fullSub);
			if (req != ThreeValue.FALSE) 
				return false;
		}
		return true;
	}


}
