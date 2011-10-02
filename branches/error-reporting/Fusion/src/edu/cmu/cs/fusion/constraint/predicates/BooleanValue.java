package edu.cmu.cs.fusion.constraint.predicates;

import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.Substitution;

public class BooleanValue extends AtomicPredicate implements NegatablePredicate {
	private SpecVar value;
	private boolean isPositive;
	
	public BooleanValue(SpecVar var) {
		this(var, true);
	}

	public BooleanValue(SpecVar var, boolean isPos) {
		value = var;
		isPositive = isPos;
	}

	public FreeVars getFreeVariables() {
		return new FreeVars().addVar(value, FreeVars.BOOL_TYPE);
	}

	public ThreeValue getRawTruth(FusionEnvironment env, Substitution sub) {
		ThreeValue val = env.getBooleanValue(sub.getSub(value));
		return isPositive ? val : val.negate();
	}

	public boolean isPositive() {
		return isPositive;
	}

	public void setPositive(boolean isPositive) {
		this.isPositive = isPositive;
	}

	public String toString() {return isPositive ? value.toString() : "!" + value.toString();}

	public String getShortString() {
		return toString();
	}

	@Override
	public String toHumanReadable(FusionEnvironment env, Substitution sub) {
		Variable[] sourceVars = env.getSourceVars(sub, value);
		return (isPositive?"":"!")+sourceVars[0].toString();
	}
	
}
