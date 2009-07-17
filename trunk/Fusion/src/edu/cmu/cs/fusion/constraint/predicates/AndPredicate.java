package edu.cmu.cs.fusion.constraint.predicates;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.Substitution;

public class AndPredicate extends BinaryPredicate {

	public AndPredicate(Predicate left, Predicate right) {
		super(left, right);
	}

	public ThreeValue getTruth(FusionEnvironment env, Substitution sub) {
		ThreeValue lTV = lhs.getTruth(env, sub);
		
		if (lTV == ThreeValue.FALSE)
			return ThreeValue.FALSE;
		else if (lTV == ThreeValue.UNKNOWN)
			return rhs.getTruth(env, sub) == ThreeValue.FALSE ? ThreeValue.FALSE : ThreeValue.UNKNOWN;
		else
			return  rhs.getTruth(env, sub);

	}
	
	public String toString() {return lhs.toString() + " && " + rhs.toString();}
	
}
