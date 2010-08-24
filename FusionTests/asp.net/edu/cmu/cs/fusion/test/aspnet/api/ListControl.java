package edu.cmu.cs.fusion.test.aspnet.api;

import java.util.List;

import edu.cmu.cs.fusion.annot.Constraint;
import edu.cmu.cs.fusion.annot.Constraints;
import edu.cmu.cs.fusion.annot.Infer;
import edu.cmu.cs.fusion.test.aspnet.relations.*;


@Infer(
		trigger = "Items(list, ctrl) AND Item(item, list)",
		effects = {"Child(item, ctrl)"}
	)
@Constraints({
@Constraint(
		op="EOM",
		trigger = "DataSourceReset(list, control)",
		requires = "DataBound(control)",
		effects = {}
),

@Constraint(
		op="ListControl.getSelectedItem() : ListItem",
		trigger = "TRUE",
		requires = "TRUE",
		effects = {"!Child(result, *)", "Child(result, target)", "Selected(result)"}
)
})

public class ListControl extends Control {
	@Items({"result", "target"})
	public ListItemCollection getItems() {return null;}
	
//	@Child({"result", "target"})
//	@Selected({"result"})
	public ListItem getSelectedItem() {return null;}
	
	@DataSourceReset({"data", "target"})
	public void setDataSource(List<?> data) {};
	
	@DataBound({"target"})
	public void dataBind() {};
}
