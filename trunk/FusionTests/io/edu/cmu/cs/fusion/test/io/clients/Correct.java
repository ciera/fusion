package edu.cmu.cs.fusion.test.io.clients;

import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.io.api.FileWriter;

@PassingTest
@UseAnalyses("FusionAnalysis")
public class Correct {
	public int m1() {
		FileWriter fw = new FileWriter();
		fw.write("");
		fw.write("");
		fw.close();
		return 23;
	}

}
