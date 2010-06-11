package edu.cmu.cs.fusion.test.iterator.clients;

import edu.cmu.cs.crystal.annotations.AnalysisTests;
import edu.cmu.cs.crystal.annotations.FailingTest;
import edu.cmu.cs.crystal.annotations.PassingTest;
import java.util.*;

/*
@AnalysisTests(
		pass={@PassingTest(analysis="FusionComplete")},
		fail={@FailingTest(value=1, analysis="FusionPragmatic"), @FailingTest(value=1, analysis="FusionSound")}
)
*/
public class ConcurrentModification {
	
	public void duplicate(Collection<String> coll) {
		Iterator<String> itr = coll.iterator();
		Collection<String> c = coll;
		String str;
		
		while (itr.hasNext()) {
			str = itr.next();
			c.add(str);
		}
	}
	
}
