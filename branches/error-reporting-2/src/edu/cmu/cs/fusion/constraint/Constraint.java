package edu.cmu.cs.fusion.constraint;

import java.util.List;

import edu.cmu.cs.fusion.constraint.predicates.TruePredicate;

public class Constraint {
	static public final SpecVar RECEIVER = new SpecVar("target");
	static public final SpecVar RESULT = new SpecVar("result");

	private Operation op;
	private Predicate trigger;
	private Predicate restrict;
	private Predicate requires;
	private List<Effect> effects;
	private String declarer;

	public Constraint(String declarer, Operation op, Predicate trigger,
			Predicate restrict, Predicate requires, List<Effect> effects) {
		this.declarer = declarer;
		this.op = op;
		this.trigger = trigger;
		this.restrict = restrict;
		this.requires = requires;
		this.effects = effects;
	}

	public String getDeclarer() {
		return declarer;
	}

	public Operation getOp() {
		return op;
	}

	public Predicate getTrigger() {
		return trigger;
	}

	public Predicate getRestrict() {
		return restrict;
	}

	public Predicate getRequires() {
		return requires;
	}

	public List<Effect> getEffects() {
		return effects;
	}

	public FreeVars getFreeVars() {
		return getUniversalFreeVars().union(requires.getFreeVariables()).union(
				restrict.getFreeVariables());
	}

	public FreeVars getUniversalFreeVars() {
		FreeVars fv = new FreeVars();

		for (Effect eff : effects) {
			fv = fv.union(eff.getFreeVariables());
		}

		fv = fv.union(op.getFreeVariables());
		fv = fv.union(trigger.getFreeVariables());

		return fv;
	}

	public String toErrorString() {

		String message = declarer + ": ";
		if (trigger instanceof TruePredicate)
			message += requires.getShortString();
		else
			message += "(" + trigger.getShortString() + ") IMPLIES ("
					+ requires.getShortString() + ")";
		return message;
	}

	public String toString() {
		return op + " AND\n" + trigger + " IMPLIES\n" + requires
				+ " RESTRICTS\n" + restrict + " EFFECTS\n" + effects + "\n";
	}

}
