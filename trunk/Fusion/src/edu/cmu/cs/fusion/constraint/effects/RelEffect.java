package edu.cmu.cs.fusion.constraint.effects;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.constraint.Effect;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.relationship.FourPointLattice;
import edu.cmu.cs.fusion.relationship.RelationshipDelta;

public class RelEffect implements Effect {
	private SpecVar[] vars;
	private Relation type;
	private boolean isRemoveEffect;
	
	public FreeVars getFreeVariables() {
		FreeVars fv = new FreeVars();
		for (int ndx = 0; ndx < vars.length; ndx++) {
			fv.addVar(vars[ndx], type.getFullyQualifiedTypes()[ndx]);
		}
		return fv;
	}

	public RelationshipDelta makeEffects(FusionEnvironment env,
			Substitution subs) {
		ObjectLabel[] labels = new ObjectLabel[vars.length];
		FourPointLattice effect = isRemoveEffect ? FourPointLattice.FAL : FourPointLattice.TRU;
		RelationshipDelta delta = new RelationshipDelta();
		
		for (int ndx = 0; ndx < vars.length; ndx++)
			labels[ndx] = subs.getSub(vars[ndx]);
		
		delta.setRelationship(new Relationship(type, labels), effect);
		return delta;
	}
}
