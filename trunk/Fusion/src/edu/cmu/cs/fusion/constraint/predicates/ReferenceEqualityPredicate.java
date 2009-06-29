package edu.cmu.cs.fusion.constraint.predicates;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.Substitution;

public class ReferenceEqualityPredicate implements NegatablePredicate {
	private SpecVar left;
	private SpecVar right;
	private boolean isPositive;
	
	public ReferenceEqualityPredicate(SpecVar left, SpecVar right) {
		this.left = left;
		this.right = right;
		isPositive = true;
	}

	public FreeVars getFreeVariables() {
		return new FreeVars().addVar(left, FreeVars.OBJECT_TYPE).addVar(right, FreeVars.OBJECT_TYPE);
	}

	public ThreeValue getTruth(FusionEnvironment env, Substitution sub) {
		if (sub.getSub(left).equals(sub.getSub(right)))
			return isPositive ? ThreeValue.TRUE : ThreeValue.FALSE;
		else
			return isPositive ? ThreeValue.FALSE : ThreeValue.TRUE;
	}

	public boolean isPositive() {
		return isPositive;
	}

	public void setPositive(boolean isPositive) {
		this.isPositive = isPositive;
	}

}
