package edu.cmu.cs.fusion.test.aspnet.api;

import edu.cmu.cs.fusion.annot.Relation.Effect;
import edu.cmu.cs.fusion.test.aspnet.relations.Selected;
import edu.cmu.cs.fusion.test.aspnet.relations.Text;


public class ListItem {
	public ListItem() {}
	
	@Selected(value={"target"}, effect = Effect.TEST, test="result")
	public boolean isSelected() {
		return false;
	}
	
	@Selected(value={"target"}, effect = Effect.TEST, test="select")
	public void setSelected(boolean select) {	
	}

	@Text({"result", "target"})
	public String getText() {
		return null;
	}
	
//	@Relations({
//	@Text(value={"*", "this"}, effect = Relation.Effect.REMOVE)//,
//	@Text({"test", "this"})
//	})
	public void setText(String text) {
	}
}
