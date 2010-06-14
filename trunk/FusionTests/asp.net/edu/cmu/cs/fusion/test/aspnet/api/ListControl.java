package edu.cmu.cs.fusion.test.aspnet.api;

import java.util.List;

import edu.cmu.cs.fusion.annot.Infer;
import edu.cmu.cs.fusion.test.aspnet.relations.*;


@Infer(
		trigger = "Items(list, ctrl) AND Item(item, list)",
		effects = {"Child(item, ctrl)"}
	)
public class ListControl extends Control {
	@Items({"result", "target"})
	public ListItemCollection getItems() {return null;}
	
	@Child({"result", "target"})
	@Selected({"result"})
	public ListItem getSelectedItem() {return null;}
	
	public void setDataSource(List data) {};
	
	public void dataBind() {};
}
