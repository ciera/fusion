package edu.cmu.cs.fusion.test.iterator.clients;

import edu.cmu.cs.crystal.annotations.AnalysisTests;
import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import java.util.*;

/*
@AnalysisTests(
		fail={@FailingTest(value=2, analysis="FusionComplete"), @FailingTest(value=2, analysis="FusionPragmatic"), @FailingTest(value=2, analysis="FusionSound")}
)
*/
public class NotCallingHasNext {
	public void foo(Collection<String> collection) {
		Iterator<String> itr = collection.iterator();
		itr.next();
		itr.next();
	}
}
