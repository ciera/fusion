package edu.cmu.cs.crystal.analysis.relationship;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.cmu.cs.crystal.flow.ILatticeOperations;

public class RelationshipLatticeOperations implements
		ILatticeOperations<RelationshipContext> {
	private static final RelationshipContext BOT = new RelationshipContext();

	public boolean atLeastAsPrecise(RelationshipContext info,
			RelationshipContext reference, ASTNode node) {
		for (Relationship rel : info.getFalseRels()) {
			if (reference.getTrueRels().contains(rel))
				return false;
		}
		for (Relationship rel : info.getTrueRels()) {
			if (reference.getFalseRels().contains(rel))
				return false;
		}
		return true;
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
		else {
		
			RelationshipContext join = new RelationshipContext();
			
			for (Relationship rel : someInfo.getFalseRels()) {
				if (otherInfo.getFalseRels().contains(rel))
					join.setRelationship(rel, ThreeValue.FALSE);
			}
			for (Relationship rel : someInfo.getTrueRels()) {
				if (otherInfo.getTrueRels().contains(rel))
					join.setRelationship(rel, ThreeValue.TRUE);
			}
			return join;
		}
	}
}
