package edu.cmu.cs.fusion.test.aspnet.clients;

import edu.cmu.cs.crystal.annotations.AnalysisTests;
import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.aspnet.api.*;

@AnalysisTests(
		pass={@PassingTest(analysis="FusionComplete")},
		fail={@FailingTest(value=2, analysis="FusionPragmatic"), @FailingTest(value=3, analysis="FusionSound")}
)
public class WrongOrder extends Page {
	public void wrongOrder(DropDownList ctrl) {
		//DropDownList ctrl = (DropDownList) findControl("DDL");
		ListItem newSel, oldSel; 
		
		newSel = ctrl.getItems().findByText("foo");
		newSel.setSelected(true);
		
		oldSel = ctrl.getSelectedItem();
		oldSel.setSelected(false);
	}
}
