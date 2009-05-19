package edu.cmu.cs.fusion.test.aspnet.api;



public class ListItem {
	public ListItem() {}
	
//	@Selected(value={"this"}, test="ret", effect = Relation.Effect.TEST)
	public boolean isSelected() {
		return false;
	}
	
//	@Selected(value={"this"}, test="select", effect = Relation.Effect.TEST)
	public void setSelected(boolean select) {	
	}

//	@Text({"ret", "this"})
	public String getText() {
		return null;
	}
	
//	@Relations({
//	@Text(value={"_", "this"}, effect = Relation.Effect.REMOVE)//,
//	@Text({"test", "this"})
//	})
	public void setText(String text) {
	}
}
