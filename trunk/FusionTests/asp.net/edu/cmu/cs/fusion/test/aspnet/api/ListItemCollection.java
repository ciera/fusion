package edu.cmu.cs.fusion.test.aspnet.api;

import java.util.Iterator;
import java.util.List;

import edu.cmu.cs.fusion.annot.Relation;
import edu.cmu.cs.fusion.test.aspnet.relations.Item;
import edu.cmu.cs.fusion.test.aspnet.relations.Text;

public class ListItemCollection implements Iterable<ListItem> {
	private List<ListItem> items;
	
	@Item(value = {"item", "target"}, effect = Relation.Effect.REMOVE)
	public void remove(ListItem item) {
		items.remove(item);
	}
	
	@Item({"item", "target"})
	public void add(ListItem item) {
		items.add(item);
	}
	
	@Item(value={"item", "target"}, effect = Relation.Effect.TEST, test="result")
	public boolean contains(ListItem item) {
		return items.contains(item);
	}
	
	@Item(value={"item", "target"}, effect = Relation.Effect.TEST, test="add")
	public void addOrRemove(ListItem item, boolean add) {
		if (add)
			items.add(item);
		else
			items.remove(item);
	}
	
	@Item({"result", "target"})
	@Text({"result", "text"})
	public ListItem findByText(String text) {
		return null;
	}

	@Item(value={"*", "target"}, effect = Relation.Effect.REMOVE)
	public void clear() {
		items.clear();
	}

	public Iterator<ListItem> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
}
