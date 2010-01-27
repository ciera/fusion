package edu.cmu.cs.fusion.test.aspnet.clients;

import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.aspnet.api.*;

@PassingTest
@UseAnalyses("FusionAnalysis")
public class GoodSetSelection extends Page {
	public void goodSetSelection(DropDownList ctrl) {
		ListItem newSel, oldSel; 
		
		oldSel = ctrl.getSelectedItem();
		oldSel.setSelected(false);
		newSel = ctrl.getItems().findByText("foo");
		newSel.setSelected(true);
	}
}
