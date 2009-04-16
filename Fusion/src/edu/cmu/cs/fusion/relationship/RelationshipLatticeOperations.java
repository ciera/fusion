package edu.cmu.cs.fusion.relationship;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.ThreeValue;

/**
 * The operations used by the static analysis on the RelationshipContext lattice.
 * These operations just call through to the lattice.
 * @author ciera
 *
 */
public class RelationshipLatticeOperations implements
		ILatticeOperations<RelationshipContext> {

	public boolean atLeastAsPrecise(RelationshipContext info,
			RelationshipContext reference, ASTNode node) {
		return info.isMorePreciseOrEqualTo(reference);
	}

	public RelationshipContext bottom() {
		return new RelationshipContext(true);
	}

	public RelationshipContext copy(RelationshipContext original) {
		return original;
	}

	public RelationshipContext join(RelationshipContext someInfo,
			RelationshipContext otherInfo, ASTNode node) {
		return someInfo.join(otherInfo);
	}
}
