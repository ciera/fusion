package edu.cmu.cs.fusion.test.iterator.api;

import edu.cmu.cs.fusion.annot.Constraint;
import edu.cmu.cs.fusion.annot.Relation.Effect;
import edu.cmu.cs.fusion.test.iterator.relations.Item;
import edu.cmu.cs.fusion.test.iterator.relations.CollIterator;
import edu.cmu.cs.fusion.test.iterator.relations.HasNext;

@Constraint(
		op = "Iterator.next() : Object",
		trigger = "TRUE",
		requires = "HasNext(target) AND CollIterator(target, coll)",
		effects = {"!HasNext(target)", "Item(result, coll)"}
)
public class Collection<T extends CharSequence> {
	public Collection() {}
	
	@CollIterator({"result", "target"})
	@HasNext(value={"result"}, effect = Effect.REMOVE)
	public Iterator<T> iterator() {return null;
	}
	
	@Item({"item", "target"})
	@CollIterator(value = {"*", "target"}, effect = Effect.REMOVE)
	public void add(T item) {}
	
	@Item(value = {"item", "target"}, effect = Effect.TEST, test = "result")
	public boolean contains(T item) {return false;}
	
	@Item(value = {"*", "target"}, effect = Effect.REMOVE)
	@CollIterator(value = {"*", "target"}, effect = Effect.REMOVE)
	public void clear() {}


	@Item({"result", "target"})
	public T getItem(int item) {return null;}
}
