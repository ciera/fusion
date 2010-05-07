package edu.cmu.cs.fusion.test.aspnet.clients;

import edu.cmu.cs.crystal.annotations.AnalysisTests;
import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.aspnet.api.*;

@AnalysisTests(
		pass={@PassingTest(analysis="FusionComplete"), @PassingTest(analysis="FusionPragmatic")},
		fail={@FailingTest(value=2, analysis="FusionSound")}
)
public class MultiLists extends Page {
	public void multiLists(DropDownList ctrlA, DropDownList ctrlB) {
		//DropDownList ctrlB = (DropDownList) findControl("DDL");
		ListItem newSel, oldSel; 
			
		oldSel = ctrlA.getSelectedItem();
		oldSel.setSelected(false);		
		newSel = ctrlB.getItems().findByText("foo");
		newSel.setSelected(true);
	}
}
