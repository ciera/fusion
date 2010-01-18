package edu.cmu.cs.fusion.test.io.clients;

import java.io.FileWriter;
import java.io.IOException;

import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;

@FailingTest(1)
@UseAnalyses("FusionAnalysis")
public class WriteAfterClose {
	public int m1() throws IOException {
		FileWriter fw = new FileWriter("foo");
		fw.write("");
		fw.close();
		fw.write("");
		return 23;
	}

}
