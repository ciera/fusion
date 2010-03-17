package edu.cmu.cs.fusion.test.iterator.clients;

import edu.cmu.cs.crystal.annotations.AnalysisTests;
import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import java.util.*;

@AnalysisTests(
		pass={@PassingTest(analysis="FusionComplete"), @PassingTest(analysis="FusionPragmatic"), @PassingTest(analysis="FusionSound")}
)
public class CorrectCollectionAccess {
	public void foo(Collection<String> coll) {
		Iterator<String> itr = coll.iterator();
		while (itr.hasNext()) {
			String item = itr.next();
			coll.isEmpty();
			coll.contains(item);
		}
	}
}
