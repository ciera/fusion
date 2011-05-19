package edu.cmu.cs.fusion.test.aspnet.api;

import java.util.List;

import edu.cmu.cs.fusion.annot.Constraint;
import edu.cmu.cs.fusion.annot.Constraints;
import edu.cmu.cs.fusion.annot.Infer;
import edu.cmu.cs.fusion.test.aspnet.relations.*;
@Constraints({
@Constraint(
		op="EOM",
		trigger = "DataSourceReset(list, control)",
		requires = "DataBound(control)",
		effects = {}
),
@Constraint(
		op="ListControl.getItems() : ListItemCollection",
		effects = {"!Items(result, *)", "Items(result, target)"}
),
@Constraint(
		op="ListControl.getSelectedItem() : ListItem",
		effects = {"Selected(result)", "!Child(result, *)", "Child(result, target)"}
)
})
public class ListControl extends Control {
	public ListItemCollection getItems() {return null;}
	
	public ListItem getSelectedItem() {return null;}
	
	@DataSourceReset({"data", "target"})
	public void setDataSource(List<?> data) {};
	
	@DataBound({"target"})
	public void dataBind() {};
}
