package edu.cmu.cs.fusion.test.iterator.clients;

import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import java.util.*;


@FailingTest(2)
@UseAnalyses("FusionAnalysis")
public class NotCallingHasNext {
	public void foo(Collection<String> coll) {
		Iterator<String> itr = coll.iterator();
		itr.next();
		itr.next();
	}
}
