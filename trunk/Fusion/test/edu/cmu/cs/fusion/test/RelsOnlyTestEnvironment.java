package edu.cmu.cs.fusion.test;

import java.util.Map;

import edu.cmu.cs.crystal.analysis.alias.Aliasing;
import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.analysis.constant.BooleanConstantLE;
import edu.cmu.cs.crystal.tac.Variable;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.SubPair;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.relationship.RelationshipContext;

public class RelsOnlyTestEnvironment extends FusionEnvironment {

	public RelsOnlyTestEnvironment(RelationshipContext relLattice) {
		super(null, relLattice, null);
	}

	@Override
	public SubPair allValidSubs(Substitution subs, FreeVars fv) {
		assert false;
		return null;
	}

	@Override
	public SubPair findLabels(Map<SpecVar, Variable> variables, FreeVars fv) {
		assert false;
		return null;
	}

	@Override
	public ThreeValue getBooleanValue(ObjectLabel label) {
		assert false;
		return null;
	}

	@Override
	public String getType(ObjectLabel obj) {
		assert false;
		return null;
	}

}
