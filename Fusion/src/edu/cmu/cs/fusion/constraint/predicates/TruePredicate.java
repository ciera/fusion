package edu.cmu.cs.fusion.constraint.predicates;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.Substitution;

public class TruePredicate implements Predicate {

	public FreeVars getFreeVariables() {
		return new FreeVars();
	}

	public ThreeValue getTruth(FusionEnvironment env, Substitution sub) {
		return ThreeValue.TRUE;
	}
	
	public String toString() {return "true";}

	public String getShortString() {
		return toString();
	}

}
