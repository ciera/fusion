package edu.cmu.cs.fusion.constraint;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class InferenceEnvironment implements Iterable<InferredRel>{
	Set<InferredRel> inferRules;
	
	public InferenceEnvironment() {
		inferRules = new HashSet<InferredRel>();
	}
	
	public Iterator<InferredRel> iterator() {
		return inferRules.iterator();
	}

	public void addRule(InferredRel inf) {
		inferRules.add(inf);
	}
}
