package edu.cmu.cs.fusion.constraint.predicates;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.relationship.RelationshipContext;
import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;

public class RelationshipPredicate implements Predicate {
	private SpecVar[] vars;
	private Relation type;
	
	public FreeVars getFreeVariables() {
		FreeVars fv = new FreeVars();
		for (int ndx = 0; ndx < vars.length; ndx++) {
			fv.addVar(vars[ndx], type.getFullyQualifiedTypes()[ndx]);
		}
		return fv;
	}

	public ThreeValue getTruth(FusionEnvironment env, Substitution sub) {
		
		ObjectLabel[] objLabels = new ObjectLabel[vars.length];
		
		for (int ndx = 0; ndx < vars.length; ndx++) {
			objLabels[ndx] = sub.getSubstitution(vars[ndx]);
		}
		
		Relationship rel = new Relationship(type, objLabels);
		ThreeValue val = env.getContext().getRelationship(rel);
		
		return val;
	}
}
