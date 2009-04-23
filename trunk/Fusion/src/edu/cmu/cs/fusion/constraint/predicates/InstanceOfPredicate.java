package edu.cmu.cs.fusion.constraint.predicates;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.test.EqualityOnlyTypeHierarchy;

public class InstanceOfPredicate implements NegatablePredicate {
	private String type;
	private SpecVar variable;
	
	public InstanceOfPredicate(SpecVar var, String fullyQualifiedType) {
		variable = var;
		type = fullyQualifiedType;
	}

	public FreeVars getFreeVariables() {
		return new FreeVars().addVar(variable, FreeVars.OBJECT_TYPE);
	}

	public ThreeValue getTruth(FusionEnvironment env, Substitution sub) {
		ObjectLabel obj = sub.getSub(variable);
		if (env.isSubtypeCompatible(env.getType(obj), type))
			return ThreeValue.TRUE;
		else
			return ThreeValue.FALSE;
				
	}

}
