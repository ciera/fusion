package edu.cmu.cs.fusion.constraint.predicates;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.Substitution;

public class ImpliesPredicate extends BinaryPredicate {

	public ImpliesPredicate(Predicate left, Predicate right) {
		super(left, right);
	}

	public ThreeValue getTruth(FusionEnvironment env, Substitution sub) {
		ThreeValue lTV = lhs.getTruth(env, sub);
		
		if (lTV == ThreeValue.TRUE)
			return rhs.getTruth(env, sub);
		else if (lTV == ThreeValue.FALSE)
			return ThreeValue.TRUE;
		else
			return ThreeValue.UNKNOWN;
	}

	public String toString() {return lhs.toString() + " -> " + rhs.toString();}

	public String getShortString() {return lhs.getShortString() + " -> " + rhs.getShortString();}
}
