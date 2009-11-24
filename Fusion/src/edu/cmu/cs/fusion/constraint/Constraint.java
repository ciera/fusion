package edu.cmu.cs.fusion.constraint;

import java.util.List;

public class Constraint {
	static public final SpecVar RECEIVER = new SpecVar("target");
	static public final SpecVar RESULT = new SpecVar("result");
		
	private Operation op;
	private Predicate trigger;
	private Predicate requires;
	private List<Effect> effects;
	
	public Constraint(Operation op, Predicate trigger, Predicate requires, List<Effect> effects) {
		this.op = op;
		this.trigger = trigger;
		this.requires = requires;
		this.effects = effects;
	}
	
	public Operation getOp() {
		return op;
	}
	public Predicate getTrigger() {
		return trigger;
	}
	public Predicate getRequires() {
		return requires;
	}
	public List<Effect> getEffects() {
		return effects;
	}
	
	public FreeVars getFreeVars() {
		return getFreeVarsExceptReqs().union(requires.getFreeVariables());
	}


	public FreeVars getFreeVarsExceptReqs() {
		FreeVars fv = new FreeVars();
		
		for (Effect eff : effects) {
			fv = fv.union(eff.getFreeVariables());
		}
		
		fv = fv.union(op.getFreeVariables());
		fv = fv.union(trigger.getFreeVariables());

		return fv;
	}
	
	public String toString() {
		return op + " AND\n" + trigger + " IMPLIES\n" + requires + " EFFECTS\n" + effects + "\n";
	}
	
	
}
