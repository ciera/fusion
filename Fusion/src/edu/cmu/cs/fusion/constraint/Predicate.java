package edu.cmu.cs.fusion.constraint;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.relationship.RelationshipContext;

public interface Predicate {
	ThreeValue getTruth(FusionEnvironment env, Substitution sub);
	
	FreeVars getFreeVariables();
}
