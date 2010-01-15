package edu.cmu.cs.fusion.test.iterator.clients;

import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.iterator.api.*;

@PassingTest
@UseAnalyses("FusionAnalysis")
public class CorrectSimpleIterator {
	public void foo(Collection<String> coll) {
		Iterator<String> itr = coll.iterator();
		while (itr.hasNext()) {
			String str = itr.next();
		}
		
		coll.add("bar");
	}
}
