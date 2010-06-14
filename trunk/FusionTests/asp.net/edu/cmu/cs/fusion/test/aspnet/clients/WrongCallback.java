package edu.cmu.cs.fusion.test.aspnet.clients;

import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.AnalysisTests;
import edu.cmu.cs.fusion.test.aspnet.api.*;

@AnalysisTests(
		pass={@PassingTest(analysis="FusionComplete")},
		fail={@FailingTest(value=1, analysis="FusionPragmatic"), @FailingTest(value=2, analysis="FusionSound")}
)
public class WrongCallback extends Page {
	@Override
	public void init() {
		DropDownList ctrl = (DropDownList) findControl("DDL");

		ctrl.setDataSource(null);
		ctrl.dataBind();
	}
}
