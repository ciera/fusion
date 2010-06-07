package edu.cmu.cs.fusion.test.aspnet.api;

import edu.cmu.cs.fusion.annot.Constraints;
import edu.cmu.cs.fusion.annot.Infer;
import edu.cmu.cs.fusion.annot.Constraint;
import edu.cmu.cs.fusion.test.aspnet.relations.*;
import edu.cmu.cs.plural.annot.States;

@Constraint(
		op="Control.findControl(String name) : Control",
		trigger="Name(name, result) AND SubControl(result, target)",
		requires = "TRUE",
		effects = {}
)
public class Control {
	public Control findControl(String name) {
		return null;
	}
}
