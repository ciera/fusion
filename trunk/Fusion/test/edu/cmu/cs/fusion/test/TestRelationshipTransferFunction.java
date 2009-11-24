package edu.cmu.cs.fusion.test;

import edu.cmu.cs.fusion.FusionAnalysis;
import edu.cmu.cs.fusion.FusionErrorStorage;
import edu.cmu.cs.fusion.FusionException;
import edu.cmu.cs.fusion.relationship.RelationshipTransferFunction;

public class TestRelationshipTransferFunction extends
		RelationshipTransferFunction {
	
	public TestRelationshipTransferFunction(FusionAnalysis relAnalysis, FusionErrorStorage errors) throws FusionException {
		super(relAnalysis, errors);
		types = new EqualityOnlyTypeHierarchy();
	}
}
