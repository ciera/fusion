package edu.cmu.cs.fusion;

import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.crystal.tac.AbstractTACBranchSensitiveTransferFunction;
import edu.cmu.cs.fusion.alias.MustPointsToTransferFunctions;
import edu.cmu.cs.fusion.alias.PointsToAliasContext;
import edu.cmu.cs.fusion.alias.PointsToLatticeOps;

public class FusionCompleteAnalysis extends FusionAnalysis<PointsToAliasContext> {

	public FusionCompleteAnalysis() {
		super(Variant.COMPLETE_VARIANT);
	}

	@Override
	public String getName() {return "FusionComplete";}

	@Override
	public AbstractTACBranchSensitiveTransferFunction<PointsToAliasContext> getAliasTransferFunction(
			DeclarativeRetriever retriever) {
		return new MustPointsToTransferFunctions(retriever, getHierarchy());
	}

	@Override
	public ILatticeOperations<PointsToAliasContext> getAliasLatticeOps() {
		return new PointsToLatticeOps(getHierarchy());
	}
}
