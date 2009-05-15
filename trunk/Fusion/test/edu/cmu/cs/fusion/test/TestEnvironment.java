package edu.cmu.cs.fusion.test;

import java.util.HashMap;
import java.util.Map;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.fusion.AliasContext;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.relationship.RelationshipContext;

public class TestEnvironment extends FusionEnvironment {
	Map<ObjectLabel, ThreeValue> bools;
	
	public TestEnvironment(RelationshipContext relLattice) {
		super(null, relLattice, null, new EqualityOnlyTypeHierarchy());
		bools = new HashMap<ObjectLabel, ThreeValue>();
	}

	public TestEnvironment(RelationshipContext relLattice, Map<ObjectLabel, ThreeValue> bools) {
		super(null, relLattice, null, new EqualityOnlyTypeHierarchy());
		this.bools = bools;
	}
	
	public TestEnvironment(AliasContext aliases, RelationshipContext relLattice, Map<ObjectLabel, ThreeValue> bools) {
		super(aliases, relLattice, null, new EqualityOnlyTypeHierarchy());
		this.bools = bools;
	}
	

	@Override
	public ThreeValue getBooleanValue(ObjectLabel label) {
		return bools.get(label);
	}
}
