package edu.cmu.cs.fusion.test;

import edu.cmu.cs.fusion.FusionAnalysis;
import edu.cmu.cs.fusion.FusionException;
import edu.cmu.cs.fusion.alias.MayPointsToTransferFunctions;
import edu.cmu.cs.fusion.relationship.RelationshipTransferFunction;

public class TestRelationshipTransferFunction extends
		RelationshipTransferFunction {
	
	public TestRelationshipTransferFunction(FusionAnalysis relAnalysis) throws FusionException {
		super(relAnalysis, new MayPointsToTransferFunctions(null, null));
		types = new EqualityOnlyTypeHierarchy();
	}
}
