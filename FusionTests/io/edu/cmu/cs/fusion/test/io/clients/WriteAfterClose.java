package edu.cmu.cs.fusion.test.io.clients;

import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.io.api.FileWriter;

@FailingTest(1)
@UseAnalyses("FusionAnalysis")
public class WriteAfterClose {
	public int m1() {
		FileWriter fw = new FileWriter();
		fw.write("");
		fw.close();
		fw.write("");
		return 23;
	}

}
