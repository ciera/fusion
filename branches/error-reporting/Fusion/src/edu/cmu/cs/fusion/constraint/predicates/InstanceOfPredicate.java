package edu.cmu.cs.fusion.constraint.predicates;

import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.alias.ObjectLabel;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.Substitution;

public class InstanceOfPredicate extends AtomicPredicate implements NegatablePredicate {
	private String type;
	private SpecVar variable;
	private boolean isPositive;
	
	public InstanceOfPredicate(SpecVar var, String fullyQualifiedType) {
		variable = var;
		type = fullyQualifiedType;
		isPositive = true;
	}

	public InstanceOfPredicate(SpecVar var, String fullyQualifiedType, boolean isPositive) {
		variable = var;
		type = fullyQualifiedType;
		this.isPositive = isPositive;
	}

	public FreeVars getFreeVariables() {
		return new FreeVars().addVar(variable, isPositive ? type : FreeVars.OBJECT_TYPE);
	}

	public ThreeValue getRawTruth(FusionEnvironment env, Substitution sub) {
		ObjectLabel obj = sub.getSub(variable);
		if (env.isSubtypeCompatible(obj.getTypeName(), type))
			return isPositive ? ThreeValue.TRUE : ThreeValue.FALSE;
		else if (env.existsPossibleSubtype(obj.getTypeName(), type))
			return ThreeValue.UNKNOWN;
		else
			return isPositive ? ThreeValue.FALSE : ThreeValue.TRUE;
	}

	public boolean isPositive() {
		return isPositive;
	}

	public void setPositive(boolean isPositive) {
		this.isPositive = isPositive;
	}

	public String toString() {
		return isPositive ? variable.toString() + " iof " + type : variable.toString() + " !iof " + type;
	}
	public String getShortString() {
		String shortType = type.lastIndexOf('.') != -1 ? type.substring(type.lastIndexOf('.') + 1) : type;
		return isPositive ? variable.toString() + " iof " + shortType : variable.toString() + " !iof " + shortType;
	}

	@Override
	public String toHumanReadable(FusionEnvironment env, Substitution sub) {
		Variable[] sourceVars = env.getSourceVars(sub, variable);
		assert(sourceVars.length==1);
		String shortType = type.lastIndexOf('.') != -1 ? type.substring(type.lastIndexOf('.') + 1) : type;
		return sourceVars[0].toString() + (isPositive? "":"!")+"iof"+shortType;
	}
}
