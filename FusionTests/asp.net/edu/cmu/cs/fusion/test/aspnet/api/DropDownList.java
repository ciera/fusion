package edu.cmu.cs.fusion.test.aspnet.api;

import edu.cmu.cs.fusion.annot.Constraint;
import edu.cmu.cs.fusion.annot.Constraints;
import edu.cmu.cs.fusion.annot.Infer;
import edu.cmu.cs.fusion.test.aspnet.relations.*;

@Constraints({
@Constraint(
	op="BOM",
	effects = {"CorrectlySelected(*)"}
),
@Constraint(
	op="ListItem.setSelected(boolean selected) : void",
	trigger = "selected AND Child(target, ctrl) AND ctrl instanceof DropDownList",
	requires = "!CorrectlySelected(ctrl)",
	effects = {"CorrectlySelected(ctrl)"}
),
@Constraint(
	op="ListItem.setSelected(boolean selected) : void",
	trigger = "!selected AND Child(target, ctrl) AND ctrl instanceof DropDownList",
	requires = "Selected(target)",
	effects = {"!CorrectlySelected(ctrl)"}
),
@Constraint(
	op="EOM",
	trigger = "ctrl instanceof DropDownList",
	requires = "CorrectlySelected(ctrl)",
	effects = {}
)
})
public class DropDownList extends ListControl {

}
