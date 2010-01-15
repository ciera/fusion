package edu.cmu.cs.fusion.test.iterator.api;

import edu.cmu.cs.fusion.annot.Relation;
import edu.cmu.cs.fusion.test.iterator.relations.HasNext;

public class Iterator<T> {
	public T next() { return null; }
	
	@HasNext(value={"target"}, effect=Relation.Effect.TEST, test="result")
	public boolean hasNext() { return true; }
}
