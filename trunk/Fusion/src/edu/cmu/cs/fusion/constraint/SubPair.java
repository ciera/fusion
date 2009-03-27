package edu.cmu.cs.fusion.constraint;

import java.util.List;

public class SubPair {
	private List<Substitution> definiteSubstitutions;
	private List<Substitution> possibleSubstitutions;
	
	public List<Substitution> getDefiniteSubstitutions() {
		return definiteSubstitutions;
	}
	public List<Substitution> getPossibleSubstitutions() {
		return possibleSubstitutions;
	}
	
}
