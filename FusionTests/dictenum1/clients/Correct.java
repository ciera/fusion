package clients;

import api.Dictionary;
import api.Enumeration;

public class Correct {

	public void m1() {
		Dictionary d  = new Dictionary();
		Enumeration e;
		d.size();
		e = d.keys();		
		e.nextElement();
		e.nextElement();
		e.nextElement();
	}
	
}
