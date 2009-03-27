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
 * @author ciera
 *
 */
public class RelationshipDelta {
	private Map<Relationship, ThreeValue> rels;
	
	public RelationshipDelta() {
		rels = new HashMap<Relationship, ThreeValue>();
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
	
	public RelationshipDelta polarize() {
		RelationshipDelta polar = new RelationshipDelta();
		
		for (Relationship rel : rels.keySet())
			polar.setRelationship(rel, FourPointLattice.UNK);
		return polar;
	}
	
	public RelationshipContext applyChangesToContext(RelationshipContext context) {
		RelationshipContext changed = new RelationshipContext(context);
		
		for (Entry<Relationship, ThreeValue> entry : rels.entrySet())
			changed.setRelationship(entry.getKey(), entry.getValue());
		
		return changed;
	}


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
	
	
	private void join(RelationshipDelta other, boolean isEquality) {
		Set<Relationship> combinedRels = new HashSet(rels.keySet());
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
			str += entry.getKey().toString() + "->" + entry.getValue().toString() + ", ";
		}

		str += ">";
		
		return str;
	}

}
