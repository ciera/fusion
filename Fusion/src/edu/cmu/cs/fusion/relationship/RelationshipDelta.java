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
public class RelationshipDelta implements Iterable<Entry<Relationship, FivePointLattice>> {
	private Map<Relationship, FivePointLattice> rels;
	
	/**
	 * Creates a new delta where everything maps to bottom.
	 */
	public RelationshipDelta() {
		rels = new HashMap<Relationship, FivePointLattice>();
	}

	/**
	 * 
	 * @return Number of relationship changes. Does not count alias updates!
	 */
	public int numberOfChanges() {
		return rels.size();
	}
	
	public FivePointLattice getValue(Relationship rel) {
		return rels.get(rel);
	}

	public void setRelationship(Relationship rel, FivePointLattice fp) {
		rels.put(rel, fp);
	}

	public RelationshipDelta polarize() {
		RelationshipDelta polar = new RelationshipDelta();
		
		for (Entry<Relationship, FivePointLattice> entry : rels.entrySet())
			polar.setRelationship(entry.getKey(), entry.getValue().polarize());
		return polar;
	}

	static public RelationshipDelta join(List<RelationshipDelta> deltas, boolean ignoreAsBot) {
		Iterator<RelationshipDelta> itr = deltas.iterator();
		RelationshipDelta joined = new RelationshipDelta();
		
		if (deltas.isEmpty())
			return joined;
		
		joined.rels = new HashMap<Relationship, FivePointLattice>(itr.next().rels);
		
		while (itr.hasNext()) {
			if (ignoreAsBot)
				joined.joinStar(itr.next());
			else
				joined.join(itr.next());
		}
		return joined;
	}
		
	private void joinStar(RelationshipDelta other) {
		Set<Relationship> combinedRels = new HashSet<Relationship>(rels.keySet());
		combinedRels.addAll(other.rels.keySet());
		
		for (Relationship rel : combinedRels) {
			FivePointLattice myVal = rels.get(rel);
			FivePointLattice otherVal = other.rels.get(rel);
			if (myVal == null)
				rels.put(rel, otherVal);
			else if (otherVal != null && myVal != otherVal) {
				rels.put(rel, myVal.join(otherVal));
			}
		}
	}
	
	private void join(RelationshipDelta other) {
		Set<Relationship> combinedRels = new HashSet<Relationship>(rels.keySet());
		combinedRels.addAll(other.rels.keySet());
		
		for (Relationship rel : combinedRels) {
			FivePointLattice myVal = rels.get(rel);
			FivePointLattice otherVal = other.rels.get(rel);
			if (myVal == null && otherVal != null)
				rels.put(rel, otherVal.polarize());
			else if (otherVal == null && myVal != null)
				rels.put(rel, myVal.polarize());
			else if (myVal != otherVal) {
				rels.put(rel, myVal.join(otherVal));
			}
			else {
				//if vals are equal then leave it be
			}
		}
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
		
		for (Entry<Relationship, FivePointLattice> entry : rels.entrySet()) {
			str += entry.getKey() + "->" + entry.getValue() + ", ";
		}

		str += ">";
		
		return str;
	}

	public Iterator<Entry<Relationship, FivePointLattice>> iterator() {
		return rels.entrySet().iterator();
	}

	public boolean isStrictlyMorePrecise(RelationshipContext context) {
		boolean strictlyMore = false;
		if (rels.isEmpty())
			return false;
		for (Entry<Relationship, FivePointLattice> entry : rels.entrySet()) {
			ThreeValue contextVal = context.getRelationship(entry.getKey());
			//all most be more precise or equal to
			if (!entry.getValue().isAtLeastAsPrecise(contextVal))
				return false;
			//and one must be more precise
			if (contextVal == ThreeValue.UNKNOWN && (entry.getValue() == FivePointLattice.TRU || entry.getValue() == FivePointLattice.FAL))
				strictlyMore = true;
		}
		
		return strictlyMore;
	}

	public void override(RelationshipDelta makeEffects) {
		for (Entry<Relationship, FivePointLattice> entry : makeEffects) {
			this.rels.put(entry.getKey(), entry.getValue());
		}
	}
}
