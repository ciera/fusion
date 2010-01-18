package edu.cmu.cs.fusion.test.io.clients;

import java.io.FileWriter;
import java.io.IOException;

import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;

@PassingTest
@UseAnalyses("FusionAnalysis")
public class Correct {
	public int m1() throws IOException {
		FileWriter fw = new FileWriter("foo");
		fw.write("");
		fw.write("");
		fw.close();
		return 23;
	}

}
