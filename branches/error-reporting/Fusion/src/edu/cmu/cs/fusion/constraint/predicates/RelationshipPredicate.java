package edu.cmu.cs.fusion.constraint.predicates;

import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.alias.ObjectLabel;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.relationship.RelationshipContext;
import edu.cmu.cs.fusion.relationship.RelationshipDelta;

public class RelationshipPredicate extends AtomicPredicate {
	private SpecVar[] vars;
	private Relation type;
	private boolean isPositive;
	
	
	public RelationshipPredicate(Relation type, SpecVar[] vars) { 
		this(type, vars, true);
	}

	public RelationshipPredicate(Relation type, SpecVar[] vars,
			boolean isPositive) {
		this.vars = vars;
		this.type = type;
		this.isPositive = isPositive;
		
	}

	public FreeVars getFreeVariables() {
		return new FreeVars(vars, type.getFullyQualifiedTypes());
	}
	
	public SpecVar[] getVars() {
		return vars;
	}

	public ThreeValue getRawTruth(FusionEnvironment env, Substitution sub) {
		
		ObjectLabel[] objLabels = new ObjectLabel[vars.length];
		
		for (int ndx = 0; ndx < vars.length; ndx++) {
			objLabels[ndx] = sub.getSub(vars[ndx]);
		}
		
		Relationship rel = new Relationship(type, objLabels);
		ThreeValue val = env.getContext().getRelationship(rel);
		
		if (val == ThreeValue.UNKNOWN) {
			RelationshipDelta delta = env.getInferredDelta(this, sub);
			
			if (delta != null) {
				RelationshipContext newContext = env.getContext().applyChangesFromDelta(delta);
				FusionEnvironment newEnv = env.copy(newContext);
				val = getTruth(newEnv, sub);
			}
		}
		
		return isPositive ? val : val.negate();
	}

	public Relation getRelation() {
		return type;
	}

	public boolean isPositive() {
		return isPositive;
	}

	public void setPositive(boolean isPositive) {
		this.isPositive = isPositive;
	}
	public String getShortString() {
		String longType = type.getName();
		String shortType = longType.lastIndexOf('.') != -1 ? longType.substring(longType.lastIndexOf('.') + 1) : longType;
		String str = shortType + "(";
		for (int ndx = 0; ndx < vars.length; ndx++) {
			str += vars[ndx];
			if (ndx < vars.length - 1)
				str += ", ";
		}
		str += ")";	
		return isPositive ? str : "!" + str;

	}
	@Override
	public String toHumanReadable(FusionEnvironment env, Substitution sub) {

		String longType = type.getName();
		StringBuffer str = new StringBuffer();
		if(!isPositive)str.append("!");
		str.append(longType.lastIndexOf('.') != -1 ? longType.substring(longType.lastIndexOf('.') + 1) : longType);
		Variable[] sourceVars = env.getSourceVars(sub,this.vars);
		str.append("(");
		for(Variable sourceVar: sourceVars)
		{
			//get source string?
			if(sourceVar==null) continue;
			str.append(sourceVar.getSourceString());
			str.append(", ");
		}
		if(sourceVars.length>0)
			{
				str.setLength(str.length()-2);
				str.append(")");
			}
		return str.toString();
		
	}
	public String toString() {
		String str = type.getName() + "(";
		for (int ndx = 0; ndx < vars.length; ndx++) {
			str += vars[ndx];
			if (ndx < vars.length - 1)
				str += ", ";
		}
		str += ")";	
		return isPositive ? str : "!" + str;
	}		
}
