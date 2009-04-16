package edu.cmu.cs.fusion.constraint;

import java.util.HashMap;
import java.util.Map;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.Pair;

/**
 * Represents a single substitution from spec variables to object labels.
 * 
 * While this class is mutable, it uses an immutable datastructure internally in
 * order to save on space.
 * @author ciera
 *
 */
public class Substitution {
	ConsList<Pair<SpecVar, ObjectLabel>> subs;
	
	public Substitution() {
		subs = ConsList.empty();
	}

	/**
	 * Assumes that var does not already exist in subs
	 * @param var
	 * @param label
	 */
	public void addSub(SpecVar var, ObjectLabel label) {
		subs = ConsList.cons(new Pair<SpecVar, ObjectLabel>(var, label), subs);
	}
	
	/**
	 * Assumes that vars.first does not already exist in subs
	 * @param var
	 * @param label
	 */
	public void addSeveralSub(ConsList<Pair<SpecVar, ObjectLabel>> vars) {
		subs = ConsList.concat(vars, subs);
	}

	public ObjectLabel getSub(SpecVar var) {
		for (Pair<SpecVar, ObjectLabel> pair : subs) {
			if (pair.fst().equals(var))
				return pair.snd();
		}
		assert false : "asked for a spec variable that didn't exist in this substution";
		return null;
	}

}
