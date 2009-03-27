package edu.cmu.cs.fusion.constraint;

import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.relationship.RelationshipContext;

public interface Predicate {
	ThreeValue getTruth(RelationshipContext context, InferenceEnvironment rules, Substitution sub);
	
	FreeVars getFreeVariables();
}
