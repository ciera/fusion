package edu.cmu.cs.fusion.constraint;

import java.util.HashMap;
import java.util.Map;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;

public class Substitution {
	Map<SpecVar, ObjectLabel> subs;

	public Substitution() {
		subs = new HashMap<SpecVar, ObjectLabel>();
	}

	public ObjectLabel getSub(SpecVar var) {
		return subs.get(var);
	}

}
