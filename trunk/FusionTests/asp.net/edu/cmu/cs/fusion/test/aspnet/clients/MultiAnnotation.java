package edu.cmu.cs.fusion.test.aspnet.clients;

import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.aspnet.api.*;

@FailingTest(1)
@UseAnalyses("FusionAnalysis")
public class MultiAnnotation {
	public void multiAnnotation() {
		ListItemCollection coll = new ListItemCollection();
		ListItem a, b, c;
	
		a = coll.findByText("foo");
		b = coll.findByText("bar");
		c = coll.findByText("test");
		
		b.setText("blah");
		c.setSelected(true);
		
		if (a.isSelected())
			a.setText("in then");
		else
			a.setText("in else");
	}
}
