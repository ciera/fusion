package edu.cmu.cs.fusion.constraint;

import java.util.List;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.alias.ObjectLabel;
import edu.cmu.cs.fusion.relationship.SevenPointLattice;
import edu.cmu.cs.fusion.relationship.RelationshipDelta;

/**
 * Represents a single relationship effect. This can be a test effect or not, and it might be negated (or not).
 * @author ciera
 *
 */
public class RelEffect implements Effect {
	public enum EffectType {
		ADD, REMOVE, TEST, NEG_TEST;
		
		public boolean isTest() {return this == TEST || this == NEG_TEST;}
		public boolean isNegative() {return this == REMOVE || this == NEG_TEST;}		
	}

	public static RelEffect createAddEffect(Relation relation, SpecVar[] specVars) {
		return new RelEffect(relation, specVars, null, false);
	}

	public static RelEffect createRemoveEffect(Relation relation, SpecVar[] specVars) {
		return new RelEffect(relation, specVars, null, true);
	}

	public static RelEffect createTestEffect(Relation relation, SpecVar[] specVars, SpecVar test) {
		return new RelEffect(relation, specVars, test, false);
	}

	public static RelEffect createNegatedTestEffect(Relation relation, SpecVar[] specVars, SpecVar test) {
		return new RelEffect(relation, specVars, test, true);
	}

	private SpecVar[] vars;
	private SpecVar test;
	private Relation type;
	private boolean negate;
	
	private RelEffect(Relation rel, SpecVar[] vars, SpecVar test, boolean negate) {
		this.type = rel;
		this.vars = vars;
		this.test = test;
		this.negate = negate;
	}
	
	/* (non-Javadoc)
	 * @see edu.cmu.cs.fusion.constraint.Effect#getFreeVariables()
	 */
	public FreeVars getFreeVariables() {
		FreeVars fv = new FreeVars();
		String[] fqt = type.getFullyQualifiedTypes();
		
		for (int ndx = 0; ndx < vars.length; ndx++) {
			if (!vars[ndx].isWildCard())
				fv = fv.addVar(vars[ndx], fqt[ndx]);
		}
		
		if (test != null)
			fv = fv.addVar(test, FreeVars.BOOL_TYPE);
		return fv;
	}
	
	/* (non-Javadoc)
	 * @see edu.cmu.cs.fusion.constraint.Effect#getWildCards()
	 */
	public FreeVars getWildCards() {
		FreeVars fv = new FreeVars();
		String[] fqt = type.getFullyQualifiedTypes();
		
		for (int ndx = 0; ndx < vars.length; ndx++) {
			if (vars[ndx].isWildCard())
				fv = fv.addVar(vars[ndx], fqt[ndx]);
		}
		return fv;
	}


	/* (non-Javadoc)
	 * @see edu.cmu.cs.fusion.constraint.Effect#makeEffects(edu.cmu.cs.fusion.FusionEnvironment, edu.cmu.cs.fusion.constraint.Substitution)
	 */
	public RelationshipDelta makeEffects(FusionEnvironment env,
			Substitution subs) {
		RelationshipDelta delta = new RelationshipDelta();
		SevenPointLattice effect = SevenPointLattice.TRU;
		
		//determine the effect for the test case. This variable cannot be wildcarded, so safe to acccess here.
		if (test != null) {
			ObjectLabel testLabel = subs.getSub(test);
			effect = SevenPointLattice.convert(env.getBooleanValue(testLabel));
		}

		//now, fill in the wildcards and get the real substitutions.
		List<Substitution> allSubs = env.allValidSubs(subs, getWildCards());

		//for definite wildcard subs, make an effect change according to effect
		for (Substitution sub : allSubs) 
			makeEffectChange(sub, delta, effect);
				
		return delta;
	}
	
	private void makeEffectChange(Substitution subs, RelationshipDelta delta, SevenPointLattice effect) {
		ObjectLabel[] labels = new ObjectLabel[vars.length];
		Relationship rel;

		for (int ndx = 0; ndx < vars.length; ndx++) {
			labels[ndx] = subs.getSub(vars[ndx]);
			assert labels[ndx] != null;
		}
		rel = new Relationship(type, labels);
		
		
		if (negate) {
			if (effect == SevenPointLattice.TRU)
				effect = SevenPointLattice.FAL;
			else if (effect == SevenPointLattice.FAL)
				effect = SevenPointLattice.TRU;
		}
		
		delta.setRelationship(rel, effect);
	}
	
	public Relation getRelation() {
		return type;
	}

	/* (non-Javadoc)
	 * @see edu.cmu.cs.fusion.constraint.Effect#getVars()
	 */
	public SpecVar[] getVars() {
		return vars;
	}
	
	public EffectType getType() {
		if (test == null) 
			return negate ? EffectType.REMOVE : EffectType.ADD;
		else
			return negate ? EffectType.NEG_TEST : EffectType.TEST;
	}
	

	/* (non-Javadoc)
	 * @see edu.cmu.cs.fusion.constraint.Effect#toString()
	 */
	public String toString() {
		String str = "";
		
		if (test != null)
			str += "?";
		if (negate)
			str += "!";
		str += type.getName() + "(";
		
		for (int ndx = 0; ndx < vars.length; ndx++) {
			str += vars[ndx];
			if (ndx < vars.length - 1)
				str += ", ";
		}
		
		str += ")";
		
		if (test != null)
			str += ":" + test;
		return str;
	}

}
