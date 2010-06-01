package edu.cmu.cs.fusion.constraint;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.Pair;

/**
 * Represents a single substitution from spec variables to object labels.
 * 
 * This is an immutable class.
 * @author ciera
 *
 */
public class Substitution {
	ConsList<Pair<SpecVar, ObjectLabel>> subs;
	
	public Substitution() {
		subs = ConsList.empty();
	}
	
	private Substitution(ConsList<Pair<SpecVar, ObjectLabel>> subs) {
		this.subs = subs;
	}

	/**
	 * Assumes that var does not already exist in subs
	 * @param var
	 * @param label
	 */
	public Substitution addSub(SpecVar var, ObjectLabel label) {
		return new Substitution(ConsList.cons(new Pair<SpecVar, ObjectLabel>(var, label), subs));
	}
	
	/**
	 * Assumes that vars.first does not already exist in subs
	 * @param vars A list of substitutions to concat with ours. This assumes that all elements
	 * of vars.first are new to subs so that we won't have duplicates.
	 */
	public Substitution addSeveralSub(ConsList<Pair<SpecVar, ObjectLabel>> vars) {
		return new Substitution(ConsList.concat(vars, subs));
	}

	public ObjectLabel getSub(SpecVar var) {
		for (Pair<SpecVar, ObjectLabel> pair : subs) {
			if (pair.fst().equals(var))
				return pair.snd();
		}
		return null;
	}

	public int size() {
		return subs.size();
	}

	public String toString() {return subs.toString();}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subs == null) ? 0 : subs.hashCode());
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
		Substitution other = (Substitution) obj;
		if (subs == null) {
			if (other.subs != null)
				return false;
		} else if (!subs.equals(other.subs))
			return false;
		return true;
	}
}
