package edu.cmu.cs.fusion.constraint;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.predicates.PredicateSatMap;

public interface Predicate {
	/**
	 * 
	 * @param env The environment to test truth under.
	 * @param sub The domain of the substitution must be a superset of the domain that
	 * the predicate will return when getFreeVariables is called.
	 * @return Whether this predicate is true, false, or unknown
	 */
	ThreeValue getTruth(FusionEnvironment env, Substitution sub);
	String toHumanReadable(FusionEnvironment env, Substitution sub);
	
	/**
	 * 
	 * @param env The environment to test truth under.
	 * @param sub The domain of the substitution must be a superset of the domain that
	 * the predicate will return when getFreeVariables is called.
	 * @param map:
	 * @return y if there exists an entry (this,y) in map, getTruth(env,sub) otherwise
	 */
//	ThreeValue getTruth(FusionEnvironment env, Substitution sub, PredicateSatMap map);
	/*public class GetTruthInstance
	{
		FusionEnvironment env;
		Substitution sub;
		PredicateSatMap map;
	}*/
	
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
	 * such that 
	 * this.getTruth(env',sub) == target, 
	 * where env' implements the atomic predicate mappings in f, 
	 * i.e. ap.getTruth(env',sub) = 
	 *   if (ap,y) in f, then y, 
	 *   else ap.getTruth(env,sub).
	 * Define an equivalence relation on X such that
	 * R(f,g) = true iff f is a subset or superset of g.
	 * Then getSat(env,sub,target) = {f | for all g in X such that R(f,g), |f| < |g|}, 
	 * where |f| is the number of entries in the map f.
	 * getSat(env,sub,target) returns a list of the smallest representative of 
	 * each equivalence class in the set X.
	 * 
	 *  
	 */
	java.util.List<PredicateSatMap> getSAT(FusionEnvironment env, Substitution sub, ThreeValue target);
}
