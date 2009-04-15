package edu.cmu.cs.fusion.constraint.predicates;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.Substitution;

public class ReferenceEqualityPredicate implements NegatablePredicate {
	private SpecVar left;
	private SpecVar right;
	
	public FreeVars getFreeVariables() {
		return new FreeVars().addVar(left, FreeVars.OBJECT_TYPE).addVar(right, FreeVars.OBJECT_TYPE);
	}

	public ThreeValue getTruth(FusionEnvironment env, Substitution sub) {
		if (sub.getSub(left).equals(sub.getSub(right)))
			return ThreeValue.TRUE;
		else
			return ThreeValue.FALSE;
	}

}
