package edu.cmu.cs.fusion.test.iterator.clients;

import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import java.util.*;


@FailingTest(1)
@UseAnalyses("FusionAnalysis")
public class ConcurrentModification {
	
	public void duplicate(Collection<String> coll) {
		Iterator<String> itr = coll.iterator();
		String str;
		
		while (itr.hasNext()) {
			str = itr.next();
			coll.add(str);
		}
	}
	
}
