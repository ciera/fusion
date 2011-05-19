package edu.cmu.cs.fusion.test;

import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.crystal.tac.AbstractTACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.DeclarativeRetriever;
import edu.cmu.cs.fusion.FusionAnalysis;
import edu.cmu.cs.fusion.alias.MayPointsToTransferFunctions;
import edu.cmu.cs.fusion.alias.PointsToAliasContext;
import edu.cmu.cs.fusion.alias.PointsToLatticeOps;

public class StubFusionAnalysis extends FusionAnalysis<PointsToAliasContext> {

	public StubFusionAnalysis() {
		super(null);
	}
	
	public AbstractTACBranchSensitiveTransferFunction<PointsToAliasContext> getAliasTransferFunction(DeclarativeRetriever retriever, TypeHierarchy types) {
		return new MayPointsToTransferFunctions(retriever, types);
	}

	public ILatticeOperations<PointsToAliasContext> getAliasLatticeOps(TypeHierarchy types) {
		return new PointsToLatticeOps(types);
	}
}