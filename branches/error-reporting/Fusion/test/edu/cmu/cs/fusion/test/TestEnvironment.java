package edu.cmu.cs.fusion.test;

import java.util.HashMap;
import java.util.Map;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.Variant;
import edu.cmu.cs.fusion.alias.AliasContext;
import edu.cmu.cs.fusion.alias.ObjectLabel;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.relationship.RelationshipContext;

public class TestEnvironment extends FusionEnvironment<AliasContext> {
	Map<ObjectLabel, ThreeValue> bools;
	
	public TestEnvironment(RelationshipContext relLattice, Variant variant) {
		super(null, relLattice, null, new EqualityOnlyTypeHierarchy(), new InferenceEnvironment(null), variant);
		bools = new HashMap<ObjectLabel, ThreeValue>();
	}

	public TestEnvironment(RelationshipContext relLattice, Map<ObjectLabel, ThreeValue> bools, Variant variant) {
		super(null, relLattice, null, new EqualityOnlyTypeHierarchy(), new InferenceEnvironment(null), variant);
		this.bools = bools;
	}
	
	public TestEnvironment(AliasContext aliases, RelationshipContext relLattice, Map<ObjectLabel, ThreeValue> bools, Variant variant) {
		super(aliases, relLattice, null, new EqualityOnlyTypeHierarchy(), new InferenceEnvironment(null), variant);
		this.bools = bools;
	}
	

	@Override
	public ThreeValue getBooleanValue(ObjectLabel label) {
		return bools.get(label);
	}
}
