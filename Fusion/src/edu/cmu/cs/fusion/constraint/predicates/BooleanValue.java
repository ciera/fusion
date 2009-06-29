package edu.cmu.cs.fusion.constraint.predicates;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.Substitution;

public class BooleanValue implements NegatablePredicate {
	private SpecVar value;
	private boolean isPositive;
	
	public BooleanValue(SpecVar var) {
		value = var;
		isPositive = true;
	}

	public FreeVars getFreeVariables() {
		return new FreeVars().addVar(value, FreeVars.BOOL_TYPE);
	}

	public ThreeValue getTruth(FusionEnvironment env, Substitution sub) {
		ThreeValue val = env.getBooleanValue(sub.getSub(value));
		return isPositive ? val : val.negate();
	}

	public boolean isPositive() {
		return isPositive;
	}

	public void setPositive(boolean isPositive) {
		this.isPositive = isPositive;
	}

}
