package edu.cmu.cs.fusion.test.aspnet.clients;

import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.aspnet.api.*;

public class GoodLoginView extends Page {
	LoginView myView;
	public void goodSetSelection() {
		if (this.getRequest().isAuthenticated()) {
			DropDownList list = (DropDownList) myView.findControl("LoggedInDDL");
			list.getSelectedItem();
		}
	}
}
