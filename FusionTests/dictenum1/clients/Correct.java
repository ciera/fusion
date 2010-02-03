package clients;

import api.Dictionary;
import api.Enumeration;

public class Correct {

	public void m1() {
		Dictionary<Object> d  = new Dictionary<Object>();
		Enumeration e;
		d.size();
		e = d.keys();		
		e.nextElement();
		e.nextElement();
		e.nextElement();
	}
	
}
