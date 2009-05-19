package edu.cmu.cs.fusion.test.aspnet.clients;

import java.util.Iterator;

import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.aspnet.api.*;

@FailingTest(1)
@UseAnalyses("FusionAnalysis")
public class NaiveSetSelection {
	public void naiveSetSelection(DropDownList ctrl) {
		ListItem newSel; 
		newSel = ctrl.getItems().findByText("foo");
		newSel.setSelected(true);
	}
}
