package edu.cmu.cs.fusion.test.aspnet.api;

import java.util.Iterator;
import java.util.List;

public class ListItemCollection implements Iterable<ListItem> {
	private List<ListItem> items;
	
//	@Item({"item", "this"})
	public void remove(ListItem item) {
		items.remove(item);
	}
	
//	@Item({"item", "this"})
	public void add(ListItem item) {
		items.add(item);
	}
	
//	@Item(value={"item", "this"}, effect = Relation.Effect.TEST, test="ret")
	public boolean contains(ListItem item) {
		return items.contains(item);
	}
	
//	@Item(value={"item", "this"}, effect = Relation.Effect.TEST, test="add")
	public void addOrRemove(ListItem item, boolean add) {
		if (add)
			items.add(item);
		else
			items.remove(item);
	}
	
//	@Item({"ret", "this"})
//	@Text({"ret", "text"})
	public ListItem findByText(String text) {
		return null;
	}

//	@Item(value={"_", "this"}, effect = Relation.Effect.REMOVE)
	public void clear() {
		items.clear();
	}

	public Iterator<ListItem> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
}
