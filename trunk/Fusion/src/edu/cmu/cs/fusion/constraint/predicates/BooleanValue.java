package edu.cmu.cs.fusion.constraint.predicates;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.Substitution;

public class BooleanValue implements NegatablePredicate {
	private SpecVar value;
	
	public BooleanValue(SpecVar var) {
		value = var;
	}

	public FreeVars getFreeVariables() {
		return new FreeVars().addVar(value, FreeVars.BOOL_TYPE);
	}

	public ThreeValue getTruth(FusionEnvironment env, Substitution sub) {
		return env.getBooleanValue(sub.getSub(value));
	}

}
