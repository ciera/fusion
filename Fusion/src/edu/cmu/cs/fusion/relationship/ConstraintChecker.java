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

	public List<FusionErrorReport> checkForErrors(FusionEnvironment<?> env, TACInstruction instr) {
		List<FusionErrorReport> errors = new LinkedList<FusionErrorReport>();
		FusionErrorReport err;
		
		assert(instr != null);
		
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
		List<AliasDelta> aliasDeltas = new LinkedList<AliasDelta>();
		
		if (boundVars == null)
			return new Pair<RelationshipDelta, AliasDelta>(new RelationshipDelta(), new AliasDelta());
		
		List<Substitution> subs = env.findLabels(boundVars, cons.getUniversalFreeVars());
		
		for (Substitution sub : subs) {
			Pair<RelationshipDelta, Substitution> deltas = runFullyBound(env, sub, cons);
			relDeltas.add(deltas.fst());
			aliasDeltas.add(new AliasDelta(boundVars, deltas.snd()));
		}
		
		RelationshipDelta relDelta;
		AliasDelta aliasDelta;
			
		if (relDeltas.isEmpty()) {
			relDelta = RelationshipDelta.getTrueBottom();
			aliasDelta = new AliasDelta();
		}
		else if (relDeltas.size() == 1) {
			relDelta = relDeltas.get(0);
			aliasDelta = aliasDeltas.get(0);
		}
		else {
			relDelta = RelationshipDelta.join(relDeltas);
			aliasDelta = AliasDelta.join(aliasDeltas);
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
	protected FusionErrorReport checkSingleConstraint(FusionEnvironment<?> env, Constraint cons, TACInstruction instr) {
		ConsList<Binding> boundVars = cons.getOp().matches(types, method, instr);

		if (boundVars == null)
			return null;

		List<Substitution> subs = env.findLabels(boundVars, cons.getUniversalFreeVars());
		
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
		
		if (trigger == ThreeValue.FALSE) {
			return new Pair<RelationshipDelta, Substitution>(RelationshipDelta.getTrueBottom(), partialSubs);
		}
		else  {
			RelationshipDelta delta = new RelationshipDelta();
			
			//now make the effects
			for (Effect effect : cons.getEffects())
				delta.override(effect.makeEffects(env, partialSubs));

			if (trigger == ThreeValue.UNKNOWN) {
				return new Pair<RelationshipDelta, Substitution>(delta.polarize(), partialSubs);
			}
			else { //trigger == ThreeValue.TRUE
				return new Pair<RelationshipDelta, Substitution>(delta, isGoodSubs(env, partialSubs, cons) ? partialSubs : null);
			}
		}
	}

	private boolean isGoodSubs(FusionEnvironment<?> env, Substitution partialSubs, Constraint cons) {
		if (cons.getRestrict() instanceof TruePredicate)
			return true;
		
		List<Substitution> subs = env.allValidSubs(partialSubs, cons.getFreeVars());
			
		for (Substitution sub : subs) {
			ThreeValue restrict = cons.getRestrict().getTruth(env, sub);
			
			if ((restrict == ThreeValue.TRUE) || (restrict == ThreeValue.UNKNOWN && variant.isPragmatic()))
				return true;
		}
		return false;
	}

	/**
	 * 
	 * @param env
	 * @param partialSubs
	 * @param cons
	 * @return true if there is an error, false if it passes
	 */
	protected boolean checkFullyBound(FusionEnvironment<?> env, Substitution partialSubs, Constraint cons) {		
		ThreeValue trigger = cons.getTrigger().getTruth(env, partialSubs);
		
		if (trigger == ThreeValue.FALSE) {
			return false;
		}
		else if (trigger == ThreeValue.TRUE) {
			if (variant.isComplete())
				return checkCompletely(env, partialSubs, cons);
			else if (variant.isPragmatic())
				return checkPragmatically(env, partialSubs, cons);
			else
				return checkSoundly(env, partialSubs, cons);
		}
		else {
			if (variant.isComplete() || variant.isPragmatic()) 
				return false;
			else
				return checkSoundly(env, partialSubs, cons);
		}
	}


	/**
	 * 
	 * @param env
	 * @param partialSubs
	 * @param cons
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
	 * 
	 * @param env
	 * @param partialSubs
	 * @param cons
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
	 * 
	 * @param env
	 * @param partialSubs
	 * @param cons
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
