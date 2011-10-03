package edu.cmu.cs.fusion.constraint;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.predicates.PredicateSatMap;

public interface Predicate {
	/**
	 * 
	 * @param env The environment where to test truth.
	 * @param sub There must be a substitution for each free variable in this predicate, 
	 * e.g. sub.getSub(x) != null any x in this.getFreeVars
	 * @return Whether this predicate is true, false, or unknown
	 */
	ThreeValue getTruth(FusionEnvironment env, Substitution sub);
	String toHumanReadable(FusionEnvironment env, Substitution sub);
	
	/**
	 * The free variables for the predicate, and the type which
	 * is expected by them.
	 * @return a collection of our free variables
	 */
	FreeVars getFreeVariables();

	@Deprecated
	String getShortString();
	
	/**
	 * Let X be the set of all possible maps f:AtomicPredicate-->TruthValue
	 * such that this.getTruth(env',sub) == target, 
	 * where env' implements the atomic predicate mappings in f, 
	 *  that is , ap.getTruth(env',sub) = 
	 *   if (ap,y) in f, then y, 
	 *   else ap.getTruth(env,sub).
	 * Define an equivalence relation R on X such that
	 * R(f,g) = true iff domain(f) (contains or is contained by) domain(g), 
	 * where domain(f) is the set of atomic predicates which get mapped 
	 * to a new value in the environment.  
	 * Then getSat(env,sub,target) = {f | for all g in X such that R(f,g), |f| < |g|}, 
	 * getSat(env,sub,target) returns a list of the smallest representative of 
	 * each equivalence class in the set X under R.
	 *  
	 */
	java.util.List<PredicateSatMap> getSAT(FusionEnvironment env, Substitution sub, ThreeValue target);
}
