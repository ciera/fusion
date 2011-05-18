package edu.cmu.cs.fusion;

import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.crystal.tac.AbstractTACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.alias.MayPointsToTransferFunctions;
import edu.cmu.cs.fusion.alias.PointsToAliasContext;
import edu.cmu.cs.fusion.alias.PointsToLatticeOps;

public class FusionPragmaticSharedAnalysis extends FusionAnalysis<PointsToAliasContext> {

	public FusionPragmaticSharedAnalysis() {
		super(Variant.PRAGMATIC_VARIANT);
	}
	@Override
	public String getName() {return "FusionPragmaticShared";}

	@Override
	public AbstractTACBranchSensitiveTransferFunction<PointsToAliasContext> getAliasTransferFunction(
			DeclarativeRetriever retriever, TypeHierarchy types) {
		return new MayPointsToTransferFunctions(retriever, types);
	}

	@Override
	public ILatticeOperations<PointsToAliasContext> getAliasLatticeOps(TypeHierarchy types) {
		return new PointsToLatticeOps(types);
	}

}
