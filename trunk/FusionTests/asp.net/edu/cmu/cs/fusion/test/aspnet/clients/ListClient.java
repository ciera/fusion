package edu.cmu.cs.fusion.test.aspnet.clients;

import java.util.Iterator;

import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.aspnet.api.*;

@FailingTest(1)
@UseAnalyses("FusionAnalysis")
public class ListClient {
	
	
	void goodSetSelection(DropDownList ctrl) {
		ListItem newSel, oldSel; 
		
		oldSel = ctrl.getSelectedItem();
		oldSel.setSelected(false);
		newSel = ctrl.getItems().findByText("foo");
		newSel.setSelected(true);
	}

	void naiveSetSelection(DropDownList ctrl) {
		ListItem newSel; 
		newSel = ctrl.getItems().findByText("foo");
		newSel.setSelected(true);
	}
/*	
	void multiLists(DropDownList ctrlA, DropDownList ctrlB) {
		ListItem newSel, oldSel; 
			
		oldSel = ctrlA.getSelectedItem();
		oldSel.setSelected(false);		
		newSel = ctrlB.getItems().findByText("foo");
		newSel.setSelected(true);
	}
	
	
	
	
	void wrongOrder(DropDownList ctrl) {
		ListItem newSel, oldSel; 
		
		newSel = ctrl.getItems().findByText("foo");
		newSel.setSelected(true);
		oldSel = ctrl.getSelectedItem();
		oldSel.setSelected(false);
	}



	
	public void badSetSelection2(DropDownList ctrl) {
		ListItem newSel; 
		ListItemCollection coll;
		
		coll = ctrl.getItems();
		
		newSel = coll.findByText("foo");
		newSel.setSelected(false);
	}
	


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
	
	public void foo() {
		ListItem a, b, c, d, e;
		ListItemCollection coll = new ListItemCollection();
		boolean add = false;
		
		a = new ListItem();
		b = new ListItem();
		c = new ListItem();
		d = new ListItem();
		e = new ListItem();
		
		coll.add(a);
		coll.add(b);
		coll.remove(b);
		
		if (coll.contains(d)) {
			coll.remove(a);
		}
		else {
			coll.remove(a);
			coll.add(c);
		}
		
		coll.addOrRemove(e, true);
		coll.addOrRemove(d, add);
		
		coll.clear();
		
		coll.add(c);
		
	}
*/
	public void copy(ListItemCollection coll) {
		ListItemCollection copy = new ListItemCollection();
		Iterator<ListItem> itr = coll.iterator();
		
		while (itr.hasNext())
			copy.add(itr.next());
	}

}
