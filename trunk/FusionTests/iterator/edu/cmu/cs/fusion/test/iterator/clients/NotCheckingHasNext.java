package edu.cmu.cs.fusion.test.iterator.clients;

import edu.cmu.cs.fusion.test.iterator.api.*;

public class NotCheckingHasNext {
	public void foo(Collection<String> coll) {
		Iterator<String> itr = coll.iterator();
		itr.hasNext();
		itr.next();
	}
}
