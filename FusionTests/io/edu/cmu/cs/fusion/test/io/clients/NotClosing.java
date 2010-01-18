package edu.cmu.cs.fusion.test.io.clients;

import java.io.FileWriter;
import java.io.IOException;

import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;

@FailingTest(2)
@UseAnalyses("FusionAnalysis")
public class NotClosing {
	public int m1() throws IOException {
		FileWriter fw = new FileWriter("foo");
		fw.write("");
		fw.write("");
		return 23;
	}

	public void m2() throws IOException {
		FileWriter fw = new FileWriter("bar");
		fw.write("");
		fw.write("");
	}

}
