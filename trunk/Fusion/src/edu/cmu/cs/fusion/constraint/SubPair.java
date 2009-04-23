package edu.cmu.cs.fusion.constraint;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.cmu.cs.crystal.analysis.alias.Aliasing;
import edu.cmu.cs.crystal.tac.Variable;

public class SubPair {
	private List<Substitution> definiteSubstitutions;
	private List<Substitution> possibleSubstitutions;
	
	public SubPair() {
		definiteSubstitutions = new LinkedList<Substitution>();
		possibleSubstitutions = new LinkedList();
	}
	
	public void addDefiniteSub(Substitution sub) {
		definiteSubstitutions.add(sub);
	}

	public void addPossibleSub(Substitution sub) {
		possibleSubstitutions.add(sub);
	}

	public Iterator<Substitution> getDefiniteSubstitutions() {
		return definiteSubstitutions.iterator();
	}
	
	public Iterator<Substitution> getPossibleSubstitutions() {
		return possibleSubstitutions.iterator();
	}
}
