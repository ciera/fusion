package edu.cmu.cs.fusion.constraint;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;

public interface Predicate {
	/**
	 * 
	 * @param env The environment to test truth under.
	 * @param sub The domain of the substitution must be a superset of the domain that
	 * the predicate will return when getFreeVariables is called.
	 * @return Whether this predicate is true, false, or unknown
	 */
	ThreeValue getTruth(FusionEnvironment env, Substitution sub);
	
	/**
	 * The free variables for the predicate, and the type which
	 * is expected by them.
	 * @return a collection of our free variables
	 */
	FreeVars getFreeVariables();

	String getShortString();
}
