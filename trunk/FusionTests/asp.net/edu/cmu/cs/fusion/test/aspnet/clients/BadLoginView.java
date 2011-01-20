package edu.cmu.cs.fusion.test.aspnet.clients;

import edu.cmu.cs.crystal.annotations.AnalysisTests;
import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.aspnet.api.*;

@AnalysisTests(
		pass={},
		fail={@FailingTest(value=1, analysis="FusionComplete"),@FailingTest(value=1, analysis="FusionPragmatic"), @FailingTest(value=3, analysis="FusionSound")}
)
public class BadLoginView extends Page {
	
	public void load() {
		LoginView myView;
		myView = (LoginView) findControl("LoginScreen");
		DropDownList list;
		list = (DropDownList) myView.findControl("LoggedInDDL");
		list.getSelectedItem();
	}
	
}
