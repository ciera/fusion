package edu.cmu.cs.fusion.constraint.predicates;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.Substitution;

public class NotPredicate implements Predicate {
	private NegatablePredicate rel;
	
	public NotPredicate(NegatablePredicate rel) {
		this.rel = rel;
	}

	public FreeVars getFreeVariables() {
		return rel.getFreeVariables();
	}

	public ThreeValue getTruth(FusionEnvironment env, Substitution sub) {
		ThreeValue tv = rel.getTruth(env, sub);
		if (tv == ThreeValue.TRUE)
			return ThreeValue.FALSE;
		else if (tv == ThreeValue.FALSE)
			return ThreeValue.TRUE;
		else
			return ThreeValue.UNKNOWN;
	}
}
