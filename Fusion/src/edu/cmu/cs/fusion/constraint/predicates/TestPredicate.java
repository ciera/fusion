package edu.cmu.cs.fusion.constraint.predicates;

import java.util.LinkedList;
import java.util.List;

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
	
	public TestPredicate(RelationshipPredicate inner, SpecVar test, boolean isPositive) {
		this.inner = inner;
		this.test = test;
		this.isPositive = isPositive;
	}
	
	public RelationshipPredicate getRelationship() {
		return inner;
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
	public String toString() {
		String str = inner.toString() + " ?= ";
		str += isPositive ? test.toString() : "!" + test.toString();
		
		return str;
	}

	public String getShortString() {
		String str = inner.getShortString() + " ?= ";
		str += isPositive ? test.toString() : "!" + test.toString();
		
		return str;
	}
	@Override
	public String toHumanReadable(FusionEnvironment env, Substitution sub) {
		String relPredHuman = inner.toHumanReadable(env, sub);
		String testSourceVar = env.getSourceVars(sub, test)[0].toString(); 
		return relPredHuman + "?=" +(isPositive?"!":"")+testSourceVar;
	}

	@Override
	public List<PredicateSatMap> getSAT(FusionEnvironment env, Substitution sub, ThreeValue target) {

		ThreeValue testVal = env.getBooleanValue(sub.getSub(test)), relVal = inner.getTruth(env, sub);
		if (isPositive==false) testVal = testVal.negate();		
		
		List<PredicateSatMap> list = new LinkedList<PredicateSatMap>();
		if (target == ThreeValue.UNKNOWN)
		{
			if (relVal==ThreeValue.UNKNOWN|| testVal==ThreeValue.UNKNOWN)
			{
				list.add(PredicateSatMap.EMPTY);
			}
			else
			{
				list.add(new PredicateSatMap(inner, ThreeValue.UNKNOWN)); 
				list.add(new PredicateSatMap(new BooleanValue(test, this.isPositive),ThreeValue.UNKNOWN));
			}
		}

		if (relVal!=ThreeValue.UNKNOWN && testVal!=ThreeValue.UNKNOWN)
		{
			if ( (target == ThreeValue.TRUE) == (testVal == relVal))
			{
				list.add(PredicateSatMap.EMPTY);
			}
			else
			{
				list.add(new PredicateSatMap(inner, relVal.negate()));			
				list.add(new PredicateSatMap(new BooleanValue(test, this.isPositive),testVal.negate()));//correct use of isPositive?
			}
		}		
		return list;
		
	}


	
}
