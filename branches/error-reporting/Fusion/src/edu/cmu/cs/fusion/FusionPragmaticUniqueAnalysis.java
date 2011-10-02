package edu.cmu.cs.fusion;

import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.crystal.tac.AbstractTACBranchSensitiveTransferFunction;
import edu.cmu.cs.fusion.alias.MustPointsToTransferFunctions;
import edu.cmu.cs.fusion.alias.PointsToAliasContext;
import edu.cmu.cs.fusion.alias.PointsToLatticeOps;

public class FusionPragmaticUniqueAnalysis extends FusionAnalysis<PointsToAliasContext> {

	public FusionPragmaticUniqueAnalysis() {
		super(Variant.PRAGMATIC_VARIANT);
	}
	@Override
	public String getName() {return "FusionPragmaticUnique";}

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
