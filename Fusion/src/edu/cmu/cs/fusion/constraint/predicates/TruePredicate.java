package edu.cmu.cs.fusion.constraint.predicates;

import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.relationship.RelationshipContext;

public class TruePredicate implements Predicate {

	public FreeVars getFreeVariables() {
		return new FreeVars();
	}

	public ThreeValue getTruth(RelationshipContext context,
			InferenceEnvironment rules, Substitution sub) {
		return ThreeValue.TRUE;
	}

}
