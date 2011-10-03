package edu.cmu.cs.fusion;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.fusion.alias.AliasContext;
import edu.cmu.cs.fusion.relationship.RelationshipLatticeOperations;

public class FusionLatticeOps<AC extends AliasContext> implements ILatticeOperations<FusionLattice<AC>> {
	RelationshipLatticeOperations relOps;
	ILatticeOperations<AC> aliasOps;
	
	public FusionLatticeOps(ILatticeOperations<AC> aliasOps) {
		this.relOps = new RelationshipLatticeOperations();
		this.aliasOps = aliasOps;
	}

	public boolean atLeastAsPrecise(FusionLattice<AC> info, FusionLattice<AC> reference, ASTNode node) {
		return relOps.atLeastAsPrecise(info.getRelContext(), reference.getRelContext(), node) &&
		aliasOps.atLeastAsPrecise(info.getAliasesForTrigger(), reference.getAliasesForTrigger(), node) &&
		aliasOps.atLeastAsPrecise(info.getAliasContext(), reference.getAliasContext(), node);
	}

	public FusionLattice<AC> bottom() {
		return new FusionLattice<AC>(relOps.bottom(), aliasOps.bottom(), aliasOps.bottom());
	}

	public FusionLattice<AC> copy(FusionLattice<AC> original) {
		return new FusionLattice<AC>(relOps.copy(original.getRelContext()), aliasOps.copy(original.getAliasesForTrigger()), aliasOps.copy(original.getAliasContext()));
	}

	public FusionLattice<AC> join(FusionLattice<AC> someInfo, FusionLattice<AC> otherInfo, ASTNode node) {
		return new FusionLattice<AC>(relOps.join(someInfo.getRelContext(), otherInfo.getRelContext(), node),
				aliasOps.join(someInfo.getAliasesForTrigger(), otherInfo.getAliasesForTrigger(), node),
				aliasOps.join(someInfo.getAliasContext(), otherInfo.getAliasContext(), node));
	}
}
