package edu.cmu.cs.fusion.test;

import java.util.HashMap;
import java.util.Map;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.Pair;
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

public class TestEnvironment extends FusionEnvironment {
	Map<ObjectLabel, ThreeValue> bools;
	Map<ObjectLabel, String> types;
	
	public TestEnvironment(RelationshipContext relLattice) {
		super(null, relLattice, null, new EqualityOnlyTypeHierarchy());
		bools = new HashMap<ObjectLabel, ThreeValue>();
	}

	public TestEnvironment(RelationshipContext relLattice, Map<ObjectLabel, ThreeValue> bools) {
		super(null, relLattice, null, new EqualityOnlyTypeHierarchy());
		this.bools = bools;
	}


	public TestEnvironment(RelationshipContext context, Map<ObjectLabel, ThreeValue> bools,
	 Map<ObjectLabel, String> types) {
		super(null, context, null, new EqualityOnlyTypeHierarchy());
		this.types = types;
	}

	@Override
	public SubPair allValidSubs(Substitution subs, FreeVars fv) {
		assert false;
		return null;
	}

	@Override
	public SubPair findLabels(ConsList<Pair<SpecVar, Variable>> variables, FreeVars fv) {
		assert false;
		return null;
	}

	@Override
	public ThreeValue getBooleanValue(ObjectLabel label) {
		return bools.get(label);
	}

	@Override
	public String getType(ObjectLabel obj) {
		return types.get(obj);
	}

}
