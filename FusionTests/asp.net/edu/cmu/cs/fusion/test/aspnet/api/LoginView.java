package edu.cmu.cs.fusion.test.aspnet.api;

import edu.cmu.cs.fusion.annot.Constraint;
import edu.cmu.cs.fusion.annot.Constraints;
import edu.cmu.cs.fusion.annot.Infer;
import edu.cmu.cs.fusion.test.aspnet.relations.*;

@Constraints({
@Constraint(
		op="LoginView.findControl(String name) : Control",
		trigger = "Name(name, result) AND LoggedInControl(result, target)",
		requires = "SubControl(target, page) AND PageRequest(request, page) AND Authenticated(request)",
		effects = {}
),
@Constraint(
		op="LoginView.findControl(String name) : Control",
		trigger = "Name(name, result) AND AnonymousControl(result, target)",
		requires = "SubControl(target, page) AND PageRequest(request, page) AND !Authenticated(request)",
		effects = {}
)
})
public class LoginView extends Control {
}
