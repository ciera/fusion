package edu.cmu.cs.fusion.test.iterator.clients;

import edu.cmu.cs.fusion.test.iterator.api.*;


public class NotCallingHasNext {
	public void foo(Collection<String> coll) {
		Iterator<String> itr = coll.iterator();
		itr.next();
		itr.next();
	}
}
