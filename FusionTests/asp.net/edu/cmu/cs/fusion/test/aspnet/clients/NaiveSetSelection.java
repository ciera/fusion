package edu.cmu.cs.fusion.test.aspnet.clients;

import edu.cmu.cs.crystal.annotations.AnalysisTests;
import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.aspnet.api.*;

@AnalysisTests(
		pass={@PassingTest(analysis="FusionComplete")},
		fail={@FailingTest(value=1, analysis="FusionPragmatic"), @FailingTest(value=2, analysis="FusionSound")}
)
public class NaiveSetSelection extends Page {
	public void naiveSetSelection() { 
		DropDownList ctrl = (DropDownList) findControl("DDL");
		ListItem newSel; 
		ListItemCollection coll;
		
		coll = ctrl.getItems();
		newSel = coll.findByText("foo");
		newSel.setSelected(true);
	}
}
