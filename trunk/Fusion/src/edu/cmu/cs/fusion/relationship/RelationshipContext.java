package edu.cmu.cs.fusion.relationship;


import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.ThreeValue;

/**
 * The relationship context. This is effectively the tuple lattice which maps
 * relationships to true, false, or unknown. Bottom is not used.
 * 
 * Notice that since it does not use bottom, it treats the bottom
 * of the lattice as the same as the top of the lattice. This issue
 * is handled by a seperate bottom flag. Notice that as soon as ANY
 * modification is made, it goes from bottom directly to top.
 * 
 * This is an immutable lattice! The only way to make changes is by joining (and getting a new lattice)
 * or by applying delta changes (and getting a new lattice).
 * 
 * @author ciera
 *
 */
public class RelationshipContext {

	private Set<Relationship> trueRels;
	private Set<Relationship> falseRels;
	private boolean isBottom;
	
	public RelationshipContext(boolean isBottom) {
		this.isBottom = isBottom;
		trueRels = new HashSet<Relationship>();
		falseRels = new HashSet<Relationship>();		
	}
	
	private RelationshipContext(RelationshipContext copy) {
		this.isBottom = copy.isBottom;
		trueRels = new HashSet<Relationship>(copy.trueRels);
		falseRels = new HashSet<Relationship>(copy.falseRels);
	}

	public ThreeValue getRelationship(Relationship rel) {
		assert !isBottom : "Shouldn't be possible as the flow analysis will only use a bottom lattice to join from univisited nodes";
		if (trueRels.contains(rel))
			return ThreeValue.TRUE;
		else if (falseRels.contains(rel))
			return ThreeValue.FALSE;
		else
			return ThreeValue.UNKNOWN;
	}
	
	private void setRelationship(Relationship rel, ThreeValue tv) {
		isBottom = false;
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
	
	/**
	 * @return true if this is more precise than reference for every relationship, and false otherwise.
	 * That is, for every relationship
	 * U more precise or equal to U
	 * T more precise or equal to U
	 * T more precise or equal to T
	 * F more precise or equal to U
	 * F more precise or equal to F
	 */
	public boolean isMorePreciseOrEqualTo(RelationshipContext reference) {
		if (isBottom)
			return true;
		else if (reference.isBottom)
			return false;
		else if (!trueRels.containsAll(reference.trueRels))
			return false;
		else if (!falseRels.containsAll(reference.falseRels))
			return false;
		return true;
	}
	
	/**
	 * @return true if this is strictly more precise than reference for every relationship, and false otherwise.
	 * That is, for every relationship
	 * U more precise or equal to U
	 * T more precise or equal to U
	 * T more precise or equal to T
	 * F more precise or equal to U
	 * F more precise or equal to F
	 * 
	 * AND this is not exactly the same as reference.
	 */
	public boolean isStrictlyMorePrecise(RelationshipContext reference) {
		if (isBottom)
			return !reference.isBottom;
		else if (reference.isBottom)
			return false;
		else
			return isMorePreciseOrEqualTo(reference) &&
			(trueRels.size() != reference.trueRels.size() ||
		     falseRels.size() != reference.falseRels.size());
	}

	/**
	 * Join the this with other and return a new lattice such that for every relationship in this or other
	 * U join U = U
	 * T join U = U
	 * F join U = U
	 * T join T = T
	 * F join F = F
	 * F join T = U
	 */
	public RelationshipContext join(RelationshipContext other) {
		if (isBottom)
			return new RelationshipContext(other);
		else if (other.isBottom)
			return new RelationshipContext(this);
		else {
			RelationshipContext join = new RelationshipContext(false);
			
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
	}
	
	/**
	 * Apply the changes from a delta lattice.
	 * @param delta
	 * @return
	 */
	public RelationshipContext applyChangesFromDelta(RelationshipDelta delta) {
		RelationshipContext changed = new RelationshipContext(this);
		changed.isBottom = !delta.hasChanges();
		
		for (Entry<Relationship, ThreeValue> entry : delta)
			changed.setRelationship(entry.getKey(), entry.getValue());
		
		return changed;
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
