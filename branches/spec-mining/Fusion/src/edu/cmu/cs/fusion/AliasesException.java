package edu.cmu.cs.fusion;

public class AliasesException extends RuntimeException {
	private int numSubs;
	public AliasesException(int numSubs) {
		this.numSubs = numSubs;
	}
	
	public int getNumSubs() {
		return numSubs;
	}
}
