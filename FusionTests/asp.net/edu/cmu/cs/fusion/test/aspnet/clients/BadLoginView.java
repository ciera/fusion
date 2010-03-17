package edu.cmu.cs.fusion.test.aspnet.clients;

import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.aspnet.api.*;

@FailingTest(1)
@UseAnalyses("FusionPragmatic")
public class BadLoginView extends Page {
	public void badUseOfLoginView() {
		LoginView myView = (LoginView) findControl("LoginScreen");
		DropDownList list = (DropDownList) myView.findControl("LoggedInDDL");
		list.getSelectedItem();
	}
}
