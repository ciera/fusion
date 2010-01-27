package edu.cmu.cs.fusion.test.aspnet.clients;

import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.aspnet.api.*;

@FailingTest(2)
@UseAnalyses("FusionAnalysis")
public class MultiLists extends Page {
	public void multiLists(DropDownList ctrlA, DropDownList ctrlB) {
		ListItem newSel, oldSel; 
			
		oldSel = ctrlA.getSelectedItem();
		oldSel.setSelected(false);		
		newSel = ctrlB.getItems().findByText("foo");
		newSel.setSelected(true);
	}
}
