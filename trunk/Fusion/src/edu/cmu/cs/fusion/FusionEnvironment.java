package edu.cmu.cs.fusion;

import java.util.Map;

import edu.cmu.cs.crystal.analysis.alias.Aliasing;
import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.analysis.constant.BooleanConstantLE;
import edu.cmu.cs.crystal.tac.Variable;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.SubPair;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.relationship.RelationshipContext;

public class FusionEnvironment {
	public FusionEnvironment(Aliasing aliasLattice, RelationshipContext relLattice, BooleanConstantLE boolLattice) {
		
	}
	
	public SubPair findLabels(Map<SpecVar, Variable> variables, FreeVars fv) {
		return null;
	}
	
	public SubPair allValidSubs(Substitution subs, FreeVars fv) {
		return null;
	}
	
	public RelationshipContext getContext() {
		return null;
	}
	
	public ThreeValue getBooleanValue(ObjectLabel label) {
		return null;
	}

	public String getType(ObjectLabel obj) {
		return null;
	}
}
