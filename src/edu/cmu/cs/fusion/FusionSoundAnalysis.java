package edu.cmu.cs.fusion;

import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.crystal.tac.AbstractTACBranchSensitiveTransferFunction;
import edu.cmu.cs.fusion.alias.MayPointsToTransferFunctions;
import edu.cmu.cs.fusion.alias.PointsToAliasContext;
import edu.cmu.cs.fusion.alias.PointsToLatticeOps;

public class FusionSoundAnalysis extends FusionAnalysis<PointsToAliasContext> {

	public FusionSoundAnalysis() {
		super(Variant.SOUND_VARIANT);
	}

	@Override
	public String getName() {return "FusionSound";}

	@Override
	public AbstractTACBranchSensitiveTransferFunction<PointsToAliasContext> getAliasTransferFunction(
			DeclarativeRetriever retriever) {
		return new MayPointsToTransferFunctions(retriever, getHierarchy());
	}

	@Override
	public ILatticeOperations<PointsToAliasContext> getAliasLatticeOps() {
		return new PointsToLatticeOps(getHierarchy());
	}

}
