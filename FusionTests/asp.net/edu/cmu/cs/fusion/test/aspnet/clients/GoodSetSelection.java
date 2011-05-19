package edu.cmu.cs.fusion.test.aspnet.clients;

import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.AnalysisTests;
import edu.cmu.cs.fusion.test.aspnet.api.*;

@AnalysisTests(
		pass={@PassingTest(analysis="FusionComplete")},
		fail={@FailingTest(value=1, analysis="FusionPragmaticShared"), @FailingTest(value=1, analysis="FusionPragmaticUnique"),@FailingTest(value=2, analysis="FusionSound")}
)
public class GoodSetSelection extends Page {
	public void goodSetSelection() {
		DropDownList ctrl = (DropDownList) findControl("DDL");
		ListItemCollection items;
		ListItem newSel, oldSel; 
		
		oldSel = ctrl.getSelectedItem();
		oldSel.setSelected(false);
		
		items = ctrl.getItems();
		newSel = items.findByText("foo");
		newSel.setSelected(true);
		newSel.getText();
	}
}
