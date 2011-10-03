package edu.cmu.cs.fusion.constraint.predicates;

import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.Predicate;

public abstract class BinaryPredicate implements Predicate {
	protected Predicate lhs;
	protected Predicate rhs;
	
	public BinaryPredicate(Predicate left, Predicate right) {
		lhs = left;
		rhs = right;
	}
	public FreeVars getFreeVariables() {
		return lhs.getFreeVariables().union(rhs.getFreeVariables());
	}
	
	public Predicate getLeft() {
		return lhs;
	}
	
	public Predicate getRight() {
		return rhs;
	}
	
}
