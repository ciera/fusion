package edu.cmu.cs.fusion.constraint.predicates;

import java.util.List;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.Substitution;

public class FalsePredicate implements Predicate{

	public FreeVars getFreeVariables() {
		return new FreeVars();
	}

	public ThreeValue getTruth(FusionEnvironment env, Substitution sub) {
		return ThreeValue.FALSE;
	}

	public String toString() {return "false";}

	public String getShortString() {return "false";}
	
	@Override
	public String toHumanReadable(FusionEnvironment env, Substitution sub) {
		return "FALSE";
	}

	@Override
	public List<PredicateSatMap> getSAT(FusionEnvironment env, Substitution sub, ThreeValue target) {

		List<PredicateSatMap> list = new java.util.LinkedList<PredicateSatMap>();		
		if(target==ThreeValue.TRUE) 
		{
			list.add(PredicateSatMap.EMPTY);			
		}
		return list;
	}

	
}
