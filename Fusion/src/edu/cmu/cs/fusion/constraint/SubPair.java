package edu.cmu.cs.fusion.constraint;

import java.util.List;
import java.util.Set;

import edu.cmu.cs.crystal.analysis.alias.Aliasing;
import edu.cmu.cs.crystal.tac.Variable;

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
