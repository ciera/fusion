package edu.cmu.cs.fusion.constraint;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.relationship.FourPointLattice;
import edu.cmu.cs.fusion.relationship.RelationshipDelta;

/**
 * Represents a single relationship effect. This can be a test effect or not, and it might be negated (or not).
 * @author ciera
 *
 */
public class Effect {
	private SpecVar[] vars;
	private SpecVar test;
	private Relation type;
	private boolean negate;
	
	private Effect(Relation rel, SpecVar[] vars, SpecVar test, boolean negate) {
		this.type = rel;
		this.vars = vars;
		this.test = test;
		this.negate = negate;
	}
	
	/**
	 * Get the free variables for this effect, including the test variable if applicable.
	 * @return
	 */
	public FreeVars getFreeVariables() {
		FreeVars fv = new FreeVars();
		
		fv = fv.addVars(vars, type.getFullyQualifiedTypes());
		if (test != null)
			fv = fv.addVar(test, FreeVars.BOOL_TYPE);
		return fv;
	}

	/**
	 * Make a delta lattice from this effect. 
	 * @param env The enivonment for boolean values
	 * @param subs The substitution to use to create the relationship
	 * @return The resulting lattice will be bottom for
	 * all relationships except the one defined by this relationship.
	 */
	public RelationshipDelta makeEffects(FusionEnvironment env,
			Substitution subs) {
		ObjectLabel[] labels = new ObjectLabel[vars.length];
		RelationshipDelta delta = new RelationshipDelta();
		Relationship rel;
		FourPointLattice effect = FourPointLattice.TRU;
		
		for (int ndx = 0; ndx < vars.length; ndx++)
			labels[ndx] = subs.getSub(vars[ndx]);
		rel = new Relationship(type, labels);
		
		if (test != null) {
			ObjectLabel testLabel = subs.getSub(test);
			effect = FourPointLattice.convert(env.getBooleanValue(testLabel));
		}
		
		if (negate) {
			if (effect == FourPointLattice.TRU)
				effect = FourPointLattice.FAL;
			else if (effect == FourPointLattice.FAL)
				effect = FourPointLattice.TRU;
		}
		
		delta.setRelationship(rel, effect);
		return delta;
	}

	public static Effect createAddEffect(Relation relation, SpecVar[] specVars) {
		return new Effect(relation, specVars, null, false);
	}

	public static Effect createRemoveEffect(Relation relation, SpecVar[] specVars) {
		return new Effect(relation, specVars, null, true);
	}

	public static Effect createTestEffect(Relation relation, SpecVar[] specVars, SpecVar test) {
		return new Effect(relation, specVars, test, false);
	}

	public static Effect createNegatedTestEffect(Relation relation, SpecVar[] specVars, SpecVar test) {
		return new Effect(relation, specVars, test, true);
	}
}
