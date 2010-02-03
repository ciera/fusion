package edu.cmu.cs.fusion.test.aspnet.api;

import edu.cmu.cs.fusion.annot.Constraint;
import edu.cmu.cs.fusion.annot.Infer;
import edu.cmu.cs.fusion.test.aspnet.relations.*;

/*
@Constraint(
		op="LoginView.FindControl(String name) : Control",
		trigger = "Name(result, id)",
		requires = "LoggedIn(result, target) IMPLIES (SubControl(target, page) AND PageRequest(request, page) AND Authenticated(request))",
		effects = {}
)
*/
public class LoginView extends Control {
}
