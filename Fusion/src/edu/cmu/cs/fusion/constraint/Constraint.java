package edu.cmu.cs.fusion.constraint;

import java.util.List;

public class Constraint {
	private Operation op;
	private Predicate trigger;
	private Predicate requires;
	private List<Effect> effects;
}
