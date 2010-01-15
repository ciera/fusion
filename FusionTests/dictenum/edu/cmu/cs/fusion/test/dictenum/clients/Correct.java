package edu.cmu.cs.fusion.test.dictenum.clients;

import edu.cmu.cs.crystal.annotations.PassingTest;
import edu.cmu.cs.crystal.annotations.UseAnalyses;
import edu.cmu.cs.fusion.test.dictenum.api.Dictionary;
import edu.cmu.cs.fusion.test.dictenum.api.Enumeration;

@PassingTest
@UseAnalyses("FusionAnalysis")
public class Correct {
	public void m1() {
		Dictionary d = new Dictionary();
		Enumeration e = new Enumeration();
		d.keys();
		e.hasMoreElements();
	}
	
	public void m2() {
		Dictionary d = new Dictionary();
		Enumeration e = new Enumeration();
		d.keys();
		e.hasMoreElements();
		e.nextElement();
		d.get();     //  <<------ why is here a warning? 'e' should be in Enumeration_NextElement ...
		e.hasMoreElements();
		e.nextElement();
		d.get();
		e.hasMoreElements();
	}

}
