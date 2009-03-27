package edu.cmu.cs.fusion.constraint.predicates;

import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.relationship.RelationshipContext;

public abstract class BinaryPredicate implements Predicate {
	protected Predicate lhs;
	protected Predicate rhs;
	
	public BinaryPredicate(Predicate left, Predicate right) {
		lhs = left;
		rhs = right;
	}
	public FreeVars getFreeVariables() {
		return lhs.getFreeVariables().join(rhs.getFreeVariables());
	}
}
