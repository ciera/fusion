package edu.cmu.cs.fusion.constraint.predicates;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.Substitution;

public class TestPredicate implements Predicate {
	private RelationshipPredicate inner;
	private SpecVar test;
	
	public TestPredicate(RelationshipPredicate inner, SpecVar test) {
		this.inner = inner;
		this.test = test;
	}
	
	public FreeVars getFreeVariables() {
		FreeVars fv = new FreeVars();
		fv.addVar(test, "boolean");
		
		return inner.getFreeVariables().join(fv);
	}

	public ThreeValue getTruth(FusionEnvironment env, Substitution sub) {
		ThreeValue testVal = env.getBooleanValue(sub.getSubstitution(test));
		
		if (testVal == ThreeValue.UNKNOWN)
			return ThreeValue.UNKNOWN;
		else {
			ThreeValue relVal = inner.getTruth(env, sub);
			if (relVal == ThreeValue.UNKNOWN)
				return ThreeValue.UNKNOWN;
			else if (relVal == testVal)
				return ThreeValue.TRUE;
			else
				return ThreeValue.FALSE;
		}
	}
}
