package edu.cmu.cs.fusion.test.iterator.clients;

import edu.cmu.cs.crystal.annotations.AnalysisTests;
import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import java.util.*;

@AnalysisTests(
		pass={@PassingTest(analysis="FusionComplete")},
		fail={@FailingTest(value=1, analysis="FusionPragmatic"), @FailingTest(value=1, analysis="FusionSound")}
)
public class NotCheckingHasNext {
	public void foo(Collection<String> coll) {
		Iterator<String> itr = coll.iterator();
		itr.hasNext();
		itr.next();
	}
}
