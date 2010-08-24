package edu.cmu.cs.fusion.test.aspnet.clients;

import edu.cmu.cs.crystal.annotations.AnalysisTests;
import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.aspnet.api.*;

@AnalysisTests(
		pass={@PassingTest(analysis="FusionComplete"), @PassingTest(analysis="FusionPragmatic")},
		fail={@FailingTest(value=3, analysis="FusionSound")}
)
public class GoodLoginView extends Page {
	public void goodSetSelection() {
		LoginView myView = (LoginView) findControl("LoginScreen");
		if (this.getRequest().isAuthenticated()) {
			DropDownList list = (DropDownList) myView.findControl("LoggedInDDL");
			ListItem item = list.getSelectedItem();
			item.getText();
		}
	}
}
