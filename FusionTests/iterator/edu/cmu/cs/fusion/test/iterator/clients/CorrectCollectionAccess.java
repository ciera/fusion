package edu.cmu.cs.fusion.test.iterator.clients;

import edu.cmu.cs.fusion.test.iterator.api.*;

public class CorrectCollectionAccess {
	public void foo(Collection<String> coll) {
		Iterator<String> itr = coll.iterator();
		while (itr.hasNext()) {
			itr.next();
			coll.getItem(2);
		}
	}
}
