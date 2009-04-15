package edu.cmu.cs.fusion.constraint;

import java.util.List;

public class Constraint {
	static public final String TARGET = "target";
	static public final String RESULT = "result";
	
	private Operation op;
	private Predicate trigger;
	private Predicate requires;
	private List<Effect> effects;
}
