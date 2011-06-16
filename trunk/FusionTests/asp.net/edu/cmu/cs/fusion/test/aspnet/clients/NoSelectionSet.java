package edu.cmu.cs.fusion.test.aspnet.clients;

import edu.cmu.cs.crystal.annotations.AnalysisTests;
import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.aspnet.api.*;

@AnalysisTests(
		pass={@PassingTest(analysis="FusionComplete")},
		fail={@FailingTest(value=1, analysis="FusionPragmaticShared"), @FailingTest(value=1, analysis="FusionPragmaticUnique"),@FailingTest(value=1, analysis="FusionSound")}
)
public class NoSelectionSet extends Page {
	public void noSelectionSet() {
		DropDownList ctrl = (DropDownList) findControl("DDL");
		ListItem newSel; 

		newSel = ctrl.getSelectedItem();
		newSel.setSelected(false);
		newSel.toString();
	}
}
