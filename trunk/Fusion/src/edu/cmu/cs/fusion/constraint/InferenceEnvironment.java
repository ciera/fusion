package edu.cmu.cs.fusion.constraint;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class InferenceEnvironment implements Iterable<InferredRel>{
	List<InferredRel> inferRules;
	
	public InferenceEnvironment() {
		inferRules = new LinkedList<InferredRel>();
	}
	
	public Iterator<InferredRel> iterator() {
		return inferRules.iterator();
	}

}
