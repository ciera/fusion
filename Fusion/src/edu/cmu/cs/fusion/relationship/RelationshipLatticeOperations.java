package edu.cmu.cs.fusion.relationship;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.ThreeValue;

public class RelationshipLatticeOperations implements
		ILatticeOperations<RelationshipContext> {
	private static final RelationshipContext BOT = new RelationshipContext();

	public boolean atLeastAsPrecise(RelationshipContext info,
			RelationshipContext reference, ASTNode node) {
		if (info == BOT)
			return true;
		else if (reference == BOT)
			return false;
		else
			return info.isMorePreciseOrEqualTo(reference);
	}

	public RelationshipContext bottom() {
		return BOT;
	}

	public RelationshipContext copy(RelationshipContext original) {
		return original;
	}

	public RelationshipContext join(RelationshipContext someInfo,
			RelationshipContext otherInfo, ASTNode node) {
		
		if (someInfo == BOT)
			return otherInfo;
		else if (otherInfo == BOT)
			return someInfo;
		else
			return someInfo.join(otherInfo);
	}
}
