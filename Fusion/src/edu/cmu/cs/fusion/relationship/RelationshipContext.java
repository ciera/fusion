package edu.cmu.cs.fusion.relationship;


import java.util.HashSet;
import java.util.Set;

import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.ThreeValue;

/**
 * The relationship context. This is effectively the tuple lattice which maps
 * relationships to true, false, or unknown. Bottom is not used.
 * @author ciera
 *
 */
public class RelationshipContext {

	private Set<Relationship> trueRels;
	private Set<Relationship> falseRels;	
	
	public RelationshipContext() {
		trueRels = new HashSet<Relationship>();
		falseRels = new HashSet<Relationship>();
	}
	
	public RelationshipContext(RelationshipContext copy) {
		trueRels = new HashSet<Relationship>(copy.trueRels);
		falseRels = new HashSet<Relationship>(copy.falseRels);
	}

	public ThreeValue getRelationship(Relationship rel) {
		if (trueRels.contains(rel))
			return ThreeValue.TRUE;
		else if (falseRels.contains(rel))
			return ThreeValue.FALSE;
		else
			return ThreeValue.UNKNOWN;
	}
	
	public void setRelationship(Relationship rel, ThreeValue tv) {
		if (tv == ThreeValue.TRUE) {
			falseRels.remove(rel);
			trueRels.add(rel);
		}
		else if (tv == ThreeValue.FALSE) {
			trueRels.remove(rel);
			falseRels.add(rel);
		}
		else {
			trueRels.remove(rel);
			falseRels.remove(rel);
		}
	}
	
	public boolean isMorePreciseOrEqualTo(RelationshipContext reference) {
		for (Relationship rel : falseRels) {
			if (reference.trueRels.contains(rel))
				return false;
		}
		for (Relationship rel : trueRels) {
			if (reference.falseRels.contains(rel))
				return false;
		}
		return true;
	}
	
	public boolean isStrictlyMorePrecise(RelationshipContext reference) {
		boolean isEqual = true;
		
		for (Relationship rel : falseRels) {
			if (reference.trueRels.contains(rel))
				return false;
			if (isEqual && !reference.falseRels.contains(rel))
				isEqual = false;
		}
		for (Relationship rel : trueRels) {
			if (reference.falseRels.contains(rel))
				return false;
			if (isEqual && !reference.trueRels.contains(rel))
				isEqual = false;
		}
		return !isEqual;
	}

	public RelationshipContext join(RelationshipContext other) {
		RelationshipContext join = new RelationshipContext();
		
		for (Relationship rel : falseRels) {
			if (other.falseRels.contains(rel))
				join.setRelationship(rel, ThreeValue.FALSE);
		}
		for (Relationship rel : trueRels) {
			if (other.trueRels.contains(rel))
				join.setRelationship(rel, ThreeValue.TRUE);
		}
		return join;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((falseRels == null) ? 0 : falseRels.hashCode());
		result = prime * result
				+ ((trueRels == null) ? 0 : trueRels.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final RelationshipContext other = (RelationshipContext) obj;
		if (falseRels == null) {
			if (other.falseRels != null)
				return false;
		} else if (!falseRels.equals(other.falseRels))
			return false;
		if (trueRels == null) {
			if (other.trueRels != null)
				return false;
		} else if (!trueRels.equals(other.trueRels))
			return false;
		return true;
	}

	
	public String toString() {
		String str = "{";
		
		for (Relationship rel : trueRels)
			str += rel.toString() + ", ";

		for (Relationship rel : falseRels)
			str += "!" + rel.toString() + ", ";
		str += "}";
		
		return str;
	}

}
