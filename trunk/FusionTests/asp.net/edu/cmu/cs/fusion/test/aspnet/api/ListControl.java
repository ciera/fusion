package edu.cmu.cs.fusion.test.aspnet.api;

import edu.cmu.cs.fusion.test.aspnet.relations.Child;
import edu.cmu.cs.fusion.test.aspnet.relations.Items;
import edu.cmu.cs.fusion.test.aspnet.relations.Selected;

public class ListControl {
	@Items({"result", "target"})
	public ListItemCollection getItems() {return null;}
	
	@Child({"result", "target"})
	@Selected({"result"})
	public ListItem getSelectedItem() {return null;}
}
