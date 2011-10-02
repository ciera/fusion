package edu.cmu.cs.fusion.constraint.predicates;

import java.util.List;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.Substitution;

public class AndPredicate extends BinaryPredicate {

	public AndPredicate(Predicate left, Predicate right) {
		super(left, right);
	}

	public ThreeValue getTruth(FusionEnvironment env, Substitution sub) {
		ThreeValue lTV = lhs.getTruth(env, sub);
		
		if (lTV == ThreeValue.FALSE)
			return ThreeValue.FALSE;
		else if (lTV == ThreeValue.UNKNOWN)
			return rhs.getTruth(env, sub) == ThreeValue.FALSE ? ThreeValue.FALSE : ThreeValue.UNKNOWN;
		else
			return  rhs.getTruth(env, sub);

	}
	
	public String getShortString() {return lhs.getShortString() + " and " + rhs.getShortString();}

	public String toString() {return lhs.toString() + " && " + rhs.toString();}

	@Override
	public List<PredicateSatMap> getSAT(FusionEnvironment env, Substitution sub, ThreeValue target) {

		List<PredicateSatMap> list;
		list = new java.util.LinkedList<PredicateSatMap>();		
		switch(target)
		{
		case FALSE:
			list.addAll(lhs.getSAT(env, sub, ThreeValue.FALSE));  
			list.addAll(rhs.getSAT(env, sub, ThreeValue.FALSE));
			break;
		case TRUE:
			crossPredicates(ThreeValue.TRUE, ThreeValue.TRUE, env, sub, list);
			break;
		case UNKNOWN:
			crossPredicates(ThreeValue.UNKNOWN, ThreeValue.UNKNOWN, env, sub, list);
			crossPredicates(ThreeValue.TRUE, ThreeValue.UNKNOWN, env, sub, list);
			crossPredicates(ThreeValue.UNKNOWN, ThreeValue.TRUE, env, sub, list);
			break;
		}
		return list;
	}

	@Override
	protected String getBinaryName() {
		return "AND";
	}
	
}
