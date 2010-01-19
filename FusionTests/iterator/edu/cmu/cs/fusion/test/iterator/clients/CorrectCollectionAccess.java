package edu.cmu.cs.fusion.test.iterator.clients;

import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import java.util.*;

@PassingTest
@UseAnalyses("FusionAnalysis")
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
