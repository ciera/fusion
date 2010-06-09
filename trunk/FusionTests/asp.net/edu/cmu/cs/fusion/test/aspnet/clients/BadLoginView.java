package edu.cmu.cs.fusion.test.aspnet.clients;

import edu.cmu.cs.crystal.annotations.AnalysisTests;
import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.aspnet.api.*;

@AnalysisTests(
		fail={@FailingTest(value=1, analysis="FusionComplete"), @FailingTest(value=1, analysis="FusionPragmatic"), @FailingTest(value=1, analysis="FusionSound")}
)
public class BadLoginView extends Page {
	public void badUseOfLoginView() {
		LoginView myView = (LoginView) findControl("LoginScreen");
		DropDownList list = (DropDownList) myView.findControl("LoggedInDDL");
		list.getSelectedItem();
	}
}
