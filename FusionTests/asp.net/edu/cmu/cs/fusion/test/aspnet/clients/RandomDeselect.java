package edu.cmu.cs.fusion.test.aspnet.clients;

import edu.cmu.cs.crystal.annotations.AnalysisTests;
import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.aspnet.api.*;

@AnalysisTests(
		fail={@FailingTest(value=1, analysis="FusionComplete"), @FailingTest(value=2, analysis="FusionPragmatic"), @FailingTest(value=2, analysis="FusionSound")}
)
public class RandomDeselect extends Page {
	public void noSelectionSet() {
		DropDownList ctrl = (DropDownList) findControl("DDL");
		ListItem newSel; 
		ListItemCollection coll;
		
		coll = ctrl.getItems();
		
		newSel = coll.findByText("foo");
		newSel.setSelected(false);
	}
}
