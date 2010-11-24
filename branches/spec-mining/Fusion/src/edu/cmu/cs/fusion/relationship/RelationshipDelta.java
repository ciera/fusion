package edu.cmu.cs.fusion.relationship;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
public class RelationshipDelta implements Iterable<Entry<Relationship, SevenPointLattice>> {	
	private Map<Relationship, SevenPointLattice> rels;	
	static private RelationshipDelta FULL_BOT = new RelationshipDelta(true);
	
	static public RelationshipDelta getTrueBottom() {return FULL_BOT;}
	
	/**
	 * Creates a new delta where everything maps to bottom.
	 */
	private RelationshipDelta(boolean isBot) {
	}

	/**
	 * Creates a new delta where everything maps to star.
	 */
	public RelationshipDelta() {
		rels = new LinkedHashMap<Relationship, SevenPointLattice>();
	}

	/**
	 * 
	 * @return Number of relationship changes. Does not count alias updates!
	 */
	public int numberOfChanges() {
		if (this == FULL_BOT)
			return 0;
		return rels.size();
	}
	
	public SevenPointLattice getValue(Relationship rel) {
		if (this == FULL_BOT)
			return SevenPointLattice.BOT;
		SevenPointLattice val = rels.get(rel);
		if (val == null)
			return SevenPointLattice.STAR;
		else
			return val;
	}

	public void setRelationship(Relationship rel, SevenPointLattice fp) {
		assert (this != FULL_BOT);
		rels.put(rel, fp);
	}

	public RelationshipDelta polarize() {
		if (this == FULL_BOT)
			return this;
		RelationshipDelta polar = new RelationshipDelta();
		
		for (Entry<Relationship, SevenPointLattice> entry : rels.entrySet())
			polar.setRelationship(entry.getKey(), entry.getValue().polarize());
		return polar;
	}

	/**
	 * Joins a list of relationship deltas.
	 * 
	 * This algorithm needs to be as fast as possible. The calls to join(RelationshipDelta) are fairly
	 * expensive as they have to go through all of the relationships in both deltas to be joined. We can't
	 * do any better than linear there. Therefore, this method needs to avoid as many of those calls as possible.
	 * It attempts to do so by skipping items in the list that are either fully bot or fully star (the default state).
	 * Bots are skipped entirely. All-stars are remembered, and then the result is polarized if any existed.
	 * @param deltas
	 * @return
	 */
	static public RelationshipDelta join(List<RelationshipDelta> deltas) {
		Iterator<RelationshipDelta> itr = deltas.iterator();
		RelationshipDelta joined = new RelationshipDelta();;
		boolean hasAllStars = false, found = false;
		
		assert(!deltas.isEmpty());
		
		RelationshipDelta first = null;
		
		//find the first one that isn't either a bot or an all-stars
		while (itr.hasNext() && !found) {
			 first = itr.next();	
			 found = first != FULL_BOT && !first.rels.isEmpty();
			 hasAllStars = hasAllStars | (first != FULL_BOT && first.rels.isEmpty());
		}

		if (!found && hasAllStars)
			return joined;
		else if (!found && !hasAllStars)
				return FULL_BOT;	
		else
			joined.rels = new LinkedHashMap<Relationship, SevenPointLattice>(first.rels);
		
		while (itr.hasNext()) {
			RelationshipDelta next = itr.next();
			if (next == FULL_BOT) //bot case, ignore everything
				continue;
			if (next.rels.isEmpty()) //top case, polarize at the end.
				hasAllStars = true;
			else
				joined.join(next);
		}
		
		if (hasAllStars) {
			for (Entry<Relationship, SevenPointLattice> entry : joined.rels.entrySet())
				entry.setValue(entry.getValue().polarize());
		}

		return joined;
	}
	
	static public RelationshipDelta joinAlt(List<RelationshipDelta> deltas) {
		Iterator<RelationshipDelta> itr = deltas.iterator();
		RelationshipDelta joined = new RelationshipDelta();;
		boolean hasChanges = false;
		
		assert(!deltas.isEmpty());
		
		RelationshipDelta first = null;		
		while (itr.hasNext() && !hasChanges) {
			first = itr.next();
			hasChanges = !first.rels.isEmpty();
		}
		
		if (hasChanges) {
			joined.rels = new LinkedHashMap<Relationship, SevenPointLattice>(first.rels);
		}
		else
			return joined;
		
		while (itr.hasNext()) {
			RelationshipDelta next = itr.next();
			if (!next.rels.isEmpty()) {
				joined.joinAlt(next);
				hasChanges = true;
			}
		}
		return hasChanges ? joined : FULL_BOT;
	}


	
	/**
	 * This and other cannot be a full bot
	 * @param other
	 */
	private void join(RelationshipDelta other) {
		Iterator<Entry<Relationship, SevenPointLattice>> itr = rels.entrySet().iterator();		
		while (itr.hasNext()) {
			Entry<Relationship, SevenPointLattice> entry = itr.next();
			entry.setValue(entry.getValue().join(other.getValue(entry.getKey())));
		}
		
		itr = other.rels.entrySet().iterator();		
		while (itr.hasNext()) {
			Entry<Relationship, SevenPointLattice> entry = itr.next();
			//if we don't have this relationship, then it must be star. Save the cost
			//of trying to find it again. Hashing is expensive!
			if (!rels.containsKey(entry.getKey()))
				setRelationship(entry.getKey(), entry.getValue().join(SevenPointLattice.STAR));
		}
	}

		
	private void joinAlt(RelationshipDelta other) {
		Iterator<Entry<Relationship, SevenPointLattice>> itr = rels.entrySet().iterator();		
		while (itr.hasNext()) {
			Entry<Relationship, SevenPointLattice> entry = itr.next();
			entry.setValue(entry.getValue().joinAlt(other.getValue(entry.getKey())));
		}
		
		itr = other.rels.entrySet().iterator();		
		while (itr.hasNext()) {
			Entry<Relationship, SevenPointLattice> entry = itr.next();
			//if we don't have this relationship, then it must be star. Save the cost
			//of trying to find it again. Hashing is expensive!
			if (!rels.containsKey(entry.getKey()))
				setRelationship(entry.getKey(), entry.getValue().joinAlt(SevenPointLattice.STAR));
		}
	}

	/*
	 * Can't call this on the bottom delta
	 */
	public void override(RelationshipDelta makeEffects) {
		Set<Relationship> combinedRels = new HashSet<Relationship>(rels.keySet());
		combinedRels.addAll(makeEffects.rels.keySet());
		
		for (Relationship rel : combinedRels) {
			SevenPointLattice myVal = getValue(rel);
			SevenPointLattice otherVal = makeEffects.getValue(rel);
			setRelationship(rel, myVal.override(otherVal));
		}
	}

	/*
	 * Can't call this on the bottom delta
	 */
	public boolean isStrictlyMorePrecise(RelationshipContext context) {
		boolean strictlyMore = false;
		if (rels.isEmpty())
			return false;
		for (Entry<Relationship, SevenPointLattice> entry : rels.entrySet()) {
			ThreeValue contextVal = context.getRelationship(entry.getKey());
			//all most be more precise or equal to
			if (!entry.getValue().isAtLeastAsPrecise(contextVal))
				return false;
			//and one must be more precise
			if (contextVal == ThreeValue.UNKNOWN && (entry.getValue() == SevenPointLattice.TRU || entry.getValue() == SevenPointLattice.FAL))
				strictlyMore = true;
		}
		
		return strictlyMore;
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
		if (this == FULL_BOT)
			return "<BOTTOM>";
		
		String str = "<";
		
		for (Entry<Relationship, SevenPointLattice> entry : rels.entrySet()) {
			str += entry.getKey() + "->" + entry.getValue() + ", ";
		}

		str += ">";
		
		return str;
	}

	public Iterator<Entry<Relationship, SevenPointLattice>> iterator() {
		assert (this != FULL_BOT);
		return rels.entrySet().iterator();
	}
}
