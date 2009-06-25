package edu.cmu.cs.fusion.test.aspnet.clients;

import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.aspnet.api.*;

@FailingTest(1)
@UseAnalyses("FusionAnalysis")
public class NoSelectionSet {
	public void noSelectionSet(DropDownList ctrl) {
		ListItem newSel; 
		ListItemCollection coll;
		
		coll = ctrl.getItems();
		
		newSel = coll.findByText("foo");
		newSel.setSelected(false);
	}
}