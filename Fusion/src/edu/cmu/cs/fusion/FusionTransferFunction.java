package edu.cmu.cs.fusion;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.crystal.tac.AbstractTACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.fusion.alias.MayPointsToAliasContext;
import edu.cmu.cs.fusion.alias.MayPointsToTransferFunctions;
import edu.cmu.cs.fusion.relationship.RelationshipContext;
import edu.cmu.cs.fusion.relationship.RelationshipTransferFunction;

/**
 * These transfer functions will always run the qtransfer functions for the points-to analysis first. However, it will
 * then run the ones for the relationships. The individual relationship transfer functions will also modify the aliasing context.
 * This modification will be done in place, without copying, so that the changes persist into the rest of the analysis.
 * 
 * @author ciera
 *
 */
public class FusionTransferFunction extends AbstractTACBranchSensitiveTransferFunction<Pair<MayPointsToAliasContext, RelationshipContext>> {

	private MayPointsToTransferFunctions aliasTF;
	private RelationshipTransferFunction relsTF;
	private PairLatticeOps<MayPointsToAliasContext, RelationshipContext> ops;

	public FusionTransferFunction(MayPointsToTransferFunctions aliasing, RelationshipTransferFunction relationships) {
		this.aliasTF = aliasing;
		this.relsTF = relationships;
		ops = new PairLatticeOps<MayPointsToAliasContext, RelationshipContext>(aliasing.getLatticeOperations(), relationships.getLatticeOperations());
	}

	public Pair<MayPointsToAliasContext, RelationshipContext> createEntryValue(
			MethodDeclaration method) {
		return new Pair<MayPointsToAliasContext, RelationshipContext>(aliasTF.createEntryValue(method), relsTF.createEntryValue(method));
	}

	public ILatticeOperations<Pair<MayPointsToAliasContext, RelationshipContext>> getLatticeOperations() {
		return ops;
	}

	
}
