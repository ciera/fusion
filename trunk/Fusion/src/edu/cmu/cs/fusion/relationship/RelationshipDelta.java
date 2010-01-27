package edu.cmu.cs.fusion.relationship;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.ThreeValue;

/**
 * Represents the delta lattice. These are the changes to make to the final lattice.
 * A bottom value signifies no change.
 * 
 * Notice that this is a mutable class!
 * @author ciera
 *
 */
public class RelationshipDelta implements Iterable<Entry<Relationship, ThreeValue>> {
	private Map<Relationship, ThreeValue> rels;
	
	/**
	 * Creates a new delta where everything maps to bottom.
	 */
	public RelationshipDelta() {
		rels = new HashMap<Relationship, ThreeValue>();
	}
	
	public int numberOfChanges() {
		return rels.size();
	}
	
	public FourPointLattice getValue(Relationship rel) {
		return FourPointLattice.convert(rels.get(rel));
	}

	public void setRelationship(Relationship rel, FourPointLattice fp) {
		if (fp == FourPointLattice.TRU)
			rels.put(rel, ThreeValue.TRUE);
		else if (fp == FourPointLattice.FAL)
			rels.put(rel, ThreeValue.FALSE);
		else if (fp == FourPointLattice.UNK)
			rels.put(rel, ThreeValue.UNKNOWN);
		else
			rels.remove(rel);
	}
	
	/**
	 * Polarizing function.
	 * 
	 * B | B 
	 * T | U 
	 * F | U 
	 * U | U 
	 */
	public RelationshipDelta polarize() {
		RelationshipDelta polar = new RelationshipDelta();
		
		for (Relationship rel : rels.keySet())
			polar.setRelationship(rel, FourPointLattice.UNK);
		return polar;
	}

	/**
	 * Joins together a list of deltas into a single delta.
	 *    
	 *   __B_T_F_U_ 
	 * B | B T F U
	 * T | T T U U
	 * F | F U F U
	 * U | U U U U
	 */
	static public RelationshipDelta join(List<RelationshipDelta> deltas) {
		Iterator<RelationshipDelta> itr = deltas.iterator();
		RelationshipDelta joined = new RelationshipDelta();
		
		if (deltas.isEmpty())
			return joined;
		
		joined.rels = new HashMap<Relationship, ThreeValue>(itr.next().rels);
		
		while (itr.hasNext()) {
			joined.join(itr.next(), false);
		}
		return joined;
	}
	
	/**
	 * Join two deltas together. Convenience method for use instead of join(List<RelationshipDelta>).
	 */
	static public RelationshipDelta join(RelationshipDelta d1, RelationshipDelta d2) {
		RelationshipDelta joined = new RelationshipDelta();
		
		joined.rels = new HashMap<Relationship, ThreeValue>(d1.rels);
		joined.join(d2, false);
		
		return joined;
	}
	
	
	private void join(RelationshipDelta other, boolean isEquality) {
		Set<Relationship> combinedRels = new HashSet<Relationship>(rels.keySet());
		combinedRels.addAll(other.rels.keySet());
		
		for (Relationship rel : combinedRels) {
			ThreeValue myVal = rels.get(rel);
			ThreeValue otherVal = other.rels.get(rel);
			if (myVal == null && otherVal != null) {
				rels.put(rel, isEquality ? ThreeValue.UNKNOWN : otherVal);
			}
			else if (myVal != null && otherVal == null) {
				if (isEquality)
					rels.put(rel, ThreeValue.UNKNOWN);
			}
			else if (myVal != otherVal) {
				rels.put(rel, ThreeValue.UNKNOWN);
			}
			else {
				//if vals are equal then leave it be
			}
		}
	}

	/**
	 * Equality joins a list of deltas. The equality join maps any differences to unknown. 
	 * 
	 *   __B_T_F_U_ 
	 * B | B U U U
	 * T | U T U U
	 * F | U U F U
	 * U | U U U U
	 */
	static public RelationshipDelta equalityJoin(List<RelationshipDelta> deltas) {
		Iterator<RelationshipDelta> itr = deltas.iterator();
		RelationshipDelta joined = new RelationshipDelta();
		
		if (deltas.isEmpty())
			return joined;
		
		joined.rels = new HashMap<Relationship, ThreeValue>(itr.next().rels);
		
		while (itr.hasNext()) {
			joined.join(itr.next(), true);
		}
		return joined;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((rels == null) ? 0 : rels.hashCode());
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
		final RelationshipDelta other = (RelationshipDelta) obj;
		if (rels == null) {
			if (other.rels != null)
				return false;
		} else if (!rels.equals(other.rels))
			return false;
		return true;
	}

	
	public String toString() {
		String str = "<";
		
		for (Entry<Relationship, ThreeValue> entry : rels.entrySet()) {
			str += entry.getKey() + "->" + entry.getValue() + ", ";
		}

		str += ">";
		
		return str;
	}

	public Iterator<Entry<Relationship, ThreeValue>> iterator() {
		return rels.entrySet().iterator();
	}

	public boolean isStrictlyMorePrecise(RelationshipContext context) {
		boolean strictlyMore = false;
		if (rels.isEmpty())
			return false;
		for (Entry<Relationship, ThreeValue> entry : rels.entrySet()) {
			ThreeValue contextVal = context.getRelationship(entry.getKey());
			//all most be more precise or equal to
			if (contextVal != ThreeValue.UNKNOWN && contextVal != entry.getValue())
				return false;
			//and one must be more precise
			if (contextVal == ThreeValue.UNKNOWN && entry.getValue() != ThreeValue.UNKNOWN)
				strictlyMore = true;
		}
		
		return strictlyMore;
	}

}
