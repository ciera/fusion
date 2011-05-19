package edu.cmu.cs.fusion.test.aspnet.clients;

import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.AnalysisTests;
import edu.cmu.cs.fusion.test.aspnet.api.*;

@AnalysisTests(
		pass={@PassingTest(analysis="FusionComplete"), @PassingTest(analysis="FusionPragmaticShared")},
		fail={@FailingTest(value=1, analysis="FusionPragmaticUnique"),@FailingTest(value=1, analysis="FusionSound")}
)
public class CorrectCallback extends Page {
	@Override
	public void load() {
		DropDownList ctrl = (DropDownList) findControl("DDL");

		ctrl.setDataSource(null);
		ctrl.dataBind();
	}
}
