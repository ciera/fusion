package edu.cmu.cs.fusion.test.iterator.clients;

import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.iterator.api.*;


@FailingTest(1)
@UseAnalyses("FusionAnalysis")
public class ConcurrentModification {
	public void foo(Collection<String> coll) {
		Iterator<String> itr = coll.iterator();
		while (itr.hasNext()) {
			itr.next();
			coll.add("bar");
		}
	}
}
