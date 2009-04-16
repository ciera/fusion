package edu.cmu.cs.fusion.constraint.effects;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.Effect;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.relationship.FourPointLattice;
import edu.cmu.cs.fusion.relationship.RelationshipDelta;

public class TestEffect implements Effect {
	private SpecVar[] vars;
	private SpecVar test;
	private Relation type;
	private boolean negate;
	
	public FreeVars getFreeVariables() {
		FreeVars fv = new FreeVars();
		for (int ndx = 0; ndx < vars.length; ndx++) {
			fv = fv.addVar(vars[ndx], type.getFullyQualifiedTypes()[ndx]);
		}
		fv = fv.addVar(test, FreeVars.BOOL_TYPE);
		return fv;
	}

	public RelationshipDelta makeEffects(FusionEnvironment env,
			Substitution subs) {
		ObjectLabel[] labels = new ObjectLabel[vars.length];
		RelationshipDelta delta = new RelationshipDelta();
		Relationship rel;
		FourPointLattice effect;
		ObjectLabel testLabel = subs.getSub(test);
		
		for (int ndx = 0; ndx < vars.length; ndx++)
			labels[ndx] = subs.getSub(vars[ndx]);
		rel = new Relationship(type, labels);
		
		if (env.getBooleanValue(testLabel) == ThreeValue.TRUE)
			effect = negate ? FourPointLattice.FAL : FourPointLattice.TRU;
		else if (env.getBooleanValue(testLabel) == ThreeValue.FALSE)
			effect = negate ? FourPointLattice.TRU : FourPointLattice.FAL;
		else
			effect = FourPointLattice.UNK;
		
		delta.setRelationship(rel, effect);
		return delta;
	}
}
