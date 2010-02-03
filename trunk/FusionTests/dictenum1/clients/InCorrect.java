package clients;

import api.Dictionary;
import api.Enumeration;

public class InCorrect {

	public void m1() {
		Dictionary<Object> d = new Dictionary<Object>();
		//Dictionary d = new Dictionary();
		Enumeration e;
		//d.size();
		e = d.keys();   // WRONG
		e.nextElement();
		e.nextElement();
		d.size();		
	}
	
	public void m2() {
		Dictionary<Object> d = new Dictionary<Object>();
		Enumeration e;
		d.size();
		e = d.keys();   
		// WRONG - not call to e.nextElement()  
	}
	
}
