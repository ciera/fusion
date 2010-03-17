package edu.cmu.cs.fusion.test.io.clients;

import java.io.FileWriter;
import java.io.IOException;

import edu.cmu.cs.crystal.annotations.AnalysisTests;
import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;

@AnalysisTests(
		pass={@PassingTest(analysis="FusionComplete"), @PassingTest(analysis="FusionPragmatic"), @PassingTest(analysis="FusionSound")}
)
public class Correct {
	public int m1() throws IOException {
		FileWriter fw = new FileWriter("foo");
		fw.write("");
		fw.write("");
		fw.close();
		return 23;
	}

}
