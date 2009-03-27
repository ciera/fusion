package edu.cmu.cs.fusion.constraint.predicates;

import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.relationship.RelationshipContext;

public class AndPredicate extends BinaryPredicate {

	public AndPredicate(Predicate left, Predicate right) {
		super(left, right);
	}

	public ThreeValue getTruth(RelationshipContext context,
			InferenceEnvironment rules, Substitution sub) {
		ThreeValue lTV = lhs.getTruth(context, rules, sub);
		
		if (lTV == ThreeValue.FALSE)
			return ThreeValue.FALSE;
		else if (lTV == ThreeValue.UNKNOWN)
			return ThreeValue.UNKNOWN;
		else
			return  rhs.getTruth(context, rules, sub);

	}
	
	
}
