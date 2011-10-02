package edu.cmu.cs.fusion.constraint.predicates;

import java.util.List;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.Substitution;

public class ImpliesPredicate extends BinaryPredicate {

	public ImpliesPredicate(Predicate left, Predicate right) {
		super(left, right);
	}

	public ThreeValue getTruth(FusionEnvironment env, Substitution sub) {
		ThreeValue lTV = lhs.getTruth(env, sub);
		
		if (lTV == ThreeValue.TRUE)
			return rhs.getTruth(env, sub);
		else if (lTV == ThreeValue.FALSE)
			return ThreeValue.TRUE;
		else
			return ThreeValue.UNKNOWN;
	}

	public String toString() {return lhs.toString() + " -> " + rhs.toString();}

	public String getShortString() {return lhs.getShortString() + " -> " + rhs.getShortString();}

	@Override
	public List<PredicateSatMap> getSAT(FusionEnvironment env, Substitution sub, ThreeValue target) {
		List<PredicateSatMap> list;
		list = new java.util.LinkedList<PredicateSatMap>();		
		switch(target)
		{
		case TRUE:
			crossPredicates(ThreeValue.TRUE, ThreeValue.TRUE, env, sub, list);
			
			crossPredicates(ThreeValue.FALSE, ThreeValue.FALSE, env, sub, list);
			crossPredicates(ThreeValue.FALSE, ThreeValue.TRUE, env, sub, list);
			crossPredicates(ThreeValue.FALSE, ThreeValue.UNKNOWN, env, sub, list);
			break;
		case FALSE:
			crossPredicates(ThreeValue.TRUE, ThreeValue.FALSE, env, sub, list);
			break;		
		case UNKNOWN:
			crossPredicates(ThreeValue.UNKNOWN, ThreeValue.TRUE, env, sub, list);
			crossPredicates(ThreeValue.UNKNOWN, ThreeValue.FALSE, env, sub, list);
			crossPredicates(ThreeValue.UNKNOWN, ThreeValue.UNKNOWN, env, sub, list);
			
			crossPredicates(ThreeValue.TRUE, ThreeValue.UNKNOWN, env, sub, list);
			break;
		}
		return list;		
	}

	@Override
	protected String getBinaryName() {
		return "IMPLIES";
	}
}
