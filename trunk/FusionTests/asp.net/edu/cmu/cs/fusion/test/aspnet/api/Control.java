package edu.cmu.cs.fusion.test.aspnet.api;

import edu.cmu.cs.fusion.annot.Infer;
import edu.cmu.cs.fusion.annot.Constraint;
import edu.cmu.cs.fusion.test.aspnet.relations.*;
import edu.cmu.cs.plural.annot.States;

@Constraint(
		op="Control.findControl(String name)",
		trigger="Name(name, sub) AND SubControl(sub, target)",
		requires = "TRUE",
		effects = {"sub == result"}
)
public class Control {
	@SubControl({"result", "target"})
	public Control findControl(String name) {
		return null;
	}
}
