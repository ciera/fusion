package edu.cmu.cs.fusion.constraint.predicates;

import edu.cmu.cs.fusion.constraint.Predicate;

public interface NegatablePredicate extends Predicate {
	public boolean isPositive();
	
	public void setPositive(boolean isPositive);
}
