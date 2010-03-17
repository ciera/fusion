package edu.cmu.cs.fusion.test.aspnet.clients;

import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.AnalysisTests;
import edu.cmu.cs.fusion.test.aspnet.api.*;

@AnalysisTests(
		pass={@PassingTest(analysis="FusionComplete"), @PassingTest(analysis="FusionPragmatic")},
		fail={@FailingTest(value=1, analysis="FusionSound")}
)
public class GoodSetSelection extends Page {
	public void goodSetSelection(DropDownList ctrl) {
		ListItem newSel, oldSel; 
		
		oldSel = ctrl.getSelectedItem();
		oldSel.setSelected(false);
		
		newSel = ctrl.getItems().findByText("foo");
		newSel.setSelected(true);
	}
}
