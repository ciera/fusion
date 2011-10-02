package edu.cmu.cs.fusion.constraint.predicates;

import java.util.LinkedList;
import java.util.List;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.Substitution;

public class OrPredicate extends BinaryPredicate {

	public OrPredicate(Predicate left, Predicate right) {
		super(left, right);
	}

	public ThreeValue getTruth(FusionEnvironment env, Substitution sub) {
		ThreeValue lTV = lhs.getTruth(env, sub);
		
		if (lTV == ThreeValue.TRUE)
			return ThreeValue.TRUE;
		else if (lTV == ThreeValue.UNKNOWN)
			return rhs.getTruth(env, sub) == ThreeValue.TRUE ? ThreeValue.TRUE : ThreeValue.UNKNOWN;
		else
			return  rhs.getTruth(env, sub);
	}

	public String toString() {return lhs.toString() + " or " + rhs.toString();}

	public String getShortString() {return lhs.getShortString() + " or " + rhs.getShortString();}
	
	@Override
	public List<PredicateSatMap> getSAT(FusionEnvironment env, Substitution sub, ThreeValue target) {
		
		List<PredicateSatMap> list;
		list = new LinkedList<PredicateSatMap>();		
		switch(target)
		{
		case TRUE:
			list.addAll(lhs.getSAT(env, sub, ThreeValue.TRUE));  
			list.addAll(rhs.getSAT(env, sub, ThreeValue.TRUE));
			break;
		case FALSE:
			crossPredicates(ThreeValue.FALSE, ThreeValue.FALSE, env, sub, list);
			break;
		case UNKNOWN:
			crossPredicates(ThreeValue.UNKNOWN, ThreeValue.UNKNOWN, env, sub, list);
			crossPredicates(ThreeValue.FALSE, ThreeValue.UNKNOWN, env, sub, list);
			crossPredicates(ThreeValue.UNKNOWN, ThreeValue.FALSE, env, sub, list);
			break;
		}
		return list;
	}

	@Override
	protected String getBinaryName() {
		return "OR";
	}	
}
