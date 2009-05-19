package edu.cmu.cs.fusion.test.aspnet.clients;

import java.util.Iterator;

import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.aspnet.api.*;

@FailingTest(1)
@UseAnalyses("FusionAnalysis")
public class WrongOrder {
	public void wrongOrder(DropDownList ctrl) {
		ListItem newSel, oldSel; 
		
		newSel = ctrl.getItems().findByText("foo");
		newSel.setSelected(true);
		oldSel = ctrl.getSelectedItem();
		oldSel.setSelected(false);
	}
}
