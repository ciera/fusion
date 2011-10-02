package edu.cmu.cs.fusion.constraint.predicates;



import java.util.List;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.Substitution;

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
	protected abstract String getBinaryName();
	@Override
	public String toHumanReadable(FusionEnvironment env, Substitution sub) {
		return "("+lhs.toHumanReadable(env, sub)+getBinaryName()+rhs.toHumanReadable(env, sub)+")";
	}
	/**
	 * Performs an AND of the mappings 
	 * from the left and right sides of 
	 * this binary predicate to the ThreeValues leftTarget, rightTarget
	 * respectively, under the "env" environment.
	 * Appends results to "host".
	 * @param leftTarget 
	 * @param rightTarget
	 * @param env
	 * @param sub
	 * @param host
	 */
	protected void crossPredicates(ThreeValue leftTarget, ThreeValue rightTarget, FusionEnvironment env, Substitution sub, List<PredicateSatMap> host)
	{
		List<PredicateSatMap> rightMaps = rhs.getSAT(env, sub, rightTarget),
			leftMaps = lhs.getSAT(env, sub, leftTarget);
		
		for(PredicateSatMap lshSat: leftMaps)
		{
			for(PredicateSatMap rhsSat:rightMaps) 
			{
				PredicateSatMap union = PredicateSatMap.union(lshSat, rhsSat);
				if(union!=null)
					host.add(union);
			}
		}	
	}
}
