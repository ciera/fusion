package edu.cmu.cs.fusion.constraint.predicates;

import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.relationship.RelationshipContext;
import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;

public class NegativeRelationship implements Predicate {
	private RelationshipPredicate rel;
	
	public FreeVars getFreeVariables() {
		return rel.getFreeVariables();
	}

	public ThreeValue getTruth(RelationshipContext context,
			InferenceEnvironment rules, Substitution sub) {
		ThreeValue tv = rel.getTruth(context, rules, sub);
		if (tv == ThreeValue.TRUE)
			return ThreeValue.FALSE;
		else if (tv == ThreeValue.FALSE)
			return ThreeValue.TRUE;
		else
			return ThreeValue.UNKNOWN;
	}
}
