package edu.cmu.cs.fusion.constraint.predicates;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.Substitution;

public class OrPredicate extends BinaryPredicate {

	public OrPredicate(Predicate left, Predicate right) {
		super(left, right);
	}

	public ThreeValue getTruth(FusionEnvironment env, Substitution sub) {
		ThreeValue lTV = lhs.getTruth(env, sub);
		
		if (lTV == ThreeValue.TRUE)
			return ThreeValue.TRUE;
		else if (lTV == ThreeValue.UNKNOWN)
			return rhs.getTruth(env, sub) == ThreeValue.TRUE ? ThreeValue.TRUE : ThreeValue.UNKNOWN;
		else
			return  rhs.getTruth(env, sub);
	}

	public String toString() {return lhs.toString() + " or " + rhs.toString();}

	public String getShortString() {return lhs.getShortString() + " or " + rhs.getShortString();}
}
