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
public class Substitution {// implements Cloneable {
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
	
//	@Override
	/**
	 * Will depply copy the subsitituion list, but not the substutions themselves.
	 */
/*	public Substitution clone() {
		Substitution cloned = new Substitution();
		
		for (Pair<SpecVar, ObjectLabel> pair : this.subs) {
			cloned.subs = ConsList.cons(pair, cloned.subs);
		}
		
		return cloned;
	}
*/
}
