package edu.cmu.cs.fusion.test.iterator.api;

import edu.cmu.cs.fusion.annot.Constraint;
import edu.cmu.cs.fusion.annot.Relation.Effect;
import edu.cmu.cs.fusion.test.iterator.relations.Item;
import edu.cmu.cs.fusion.test.iterator.relations.Iterator;
import edu.cmu.cs.fusion.test.iterator.relations.HasNext;

@Constraint(
		op = "Iterator.next() : Object",
		trigger = "TRUE",
		requires = "HasNext(target) AND Iterator(target, coll)",
		effects = {"!HasNext(target)", "Item(result, coll)"}
)
public class Collection {
	public Collection() {}
	
	@Iterator({"result", "target"})
	public Iterator iterator() {return null;
	}
	
	@Item({"item", "target"})
	@Iterator(value = {"*", "target"}, effect = Effect.REMOVE)
	public void add(Object item) {}
	
	@Item(value = {"item", "target"}, effect = Effect.TEST, test = "result")
	public boolean contains(Object item) {return false;}
	
	@Item(value = {"*", "target"}, effect = Effect.REMOVE)
	@Iterator(value = {"*", "target"}, effect = Effect.REMOVE)
	public void clear() {}


	@Item({"result", "target"})
	public Object getItem(int item) {return null;}
}
