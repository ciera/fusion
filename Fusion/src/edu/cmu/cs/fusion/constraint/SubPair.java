package edu.cmu.cs.fusion.constraint;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SubPair {
	private List<Substitution> definiteSubstitutions;
	private List<Substitution> possibleSubstitutions;
	
	public SubPair() {
		definiteSubstitutions = new LinkedList<Substitution>();
		possibleSubstitutions = new LinkedList<Substitution>();
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

	public int numberOfSubstitutions() {
		return definiteSubstitutions.size() + possibleSubstitutions.size();
	}
}
