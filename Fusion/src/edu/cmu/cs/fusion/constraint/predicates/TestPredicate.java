package edu.cmu.cs.fusion.constraint.predicates;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.Substitution;

/**
 * The test predicate to determine the truth of a relationship based on how it compares to some other value.
 * In a test predicate, the negation is on the variable under consideration, not the relationship's value itself.
 * @author ciera
 *
 */
public class TestPredicate implements NegatablePredicate {
	private RelationshipPredicate inner;
	private SpecVar test;
	private boolean isPositive;
	
	public TestPredicate(RelationshipPredicate inner, SpecVar test) {
		this.inner = inner;
		this.test = test;
		isPositive = true;
	}
	
	public FreeVars getFreeVariables() {
		FreeVars fv = new FreeVars();
		fv = fv.addVar(test, "boolean");
		
		return inner.getFreeVariables().union(fv);
	}

	public ThreeValue getTruth(FusionEnvironment env, Substitution sub) {
		ThreeValue testVal = env.getBooleanValue(sub.getSub(test));
		ThreeValue val;
		
		testVal = isPositive ? testVal : testVal.negate();
		
		if (testVal == ThreeValue.UNKNOWN)
			val = ThreeValue.UNKNOWN;
		else {
			ThreeValue relVal = inner.getTruth(env, sub);
			if (relVal == ThreeValue.UNKNOWN)
				val = ThreeValue.UNKNOWN;
			else if (relVal == testVal)
				val = ThreeValue.TRUE;
			else
				val = ThreeValue.FALSE;
		}
		return val;
	}

	public boolean isPositive() {
		return isPositive;
	}

	public void setPositive(boolean isPositive) {
		this.isPositive = isPositive;
	}
}
