package edu.cmu.cs.fusion.test.io.clients;

import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.io.api.FileWriter;

@FailingTest(2)
@UseAnalyses("FusionAnalysis")
public class NotClosing {
	public int m1() {
		FileWriter fw = new FileWriter();
		fw.write("");
		fw.write("");
		return 23;
	}

	public void m2() {
		FileWriter fw = new FileWriter();
		fw.write("");
		fw.write("");
	}

}
