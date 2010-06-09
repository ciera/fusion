package edu.cmu.cs.fusion.test.aspnet.clients;

import edu.cmu.cs.crystal.annotations.AnalysisTests;
import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.aspnet.api.*;

@AnalysisTests(
		pass={@PassingTest(analysis="FusionComplete"), @PassingTest(analysis="FusionPragmatic"), @PassingTest(analysis="FusionSound")},
		fail={}
)
public class GoodLoginView extends Page {
	LoginView myView;
	public void goodSetSelection() {
		if (this.getRequest().isAuthenticated()) {
			DropDownList list = (DropDownList) myView.findControl("LoggedInDDL");
			list.getSelectedItem();
		}
	}
}
