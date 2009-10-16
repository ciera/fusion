package edu.cmu.cs.fusion.test.aspnet.api;

import edu.cmu.cs.fusion.test.aspnet.relations.Child;
import edu.cmu.cs.fusion.test.aspnet.relations.Items;

public class ListControl {
	@Items({"ret", "this"})
	public ListItemCollection getItems() {return null;}
	
	@Child({"ret", "this"})
//	@Selected({"ret"})
	public ListItem getSelectedItem() {return null;}
}
