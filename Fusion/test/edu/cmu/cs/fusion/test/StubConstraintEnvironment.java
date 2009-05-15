package edu.cmu.cs.fusion.test;

import java.util.LinkedList;
import java.util.List;

import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.ConstraintEnvironment;

public class StubConstraintEnvironment extends ConstraintEnvironment {
	List<Constraint> list;
	
	public StubConstraintEnvironment() {
		list = new LinkedList<Constraint>();
	}
	
	public void addConstraint(Constraint cons) {
		list.add(cons);
	}
}
