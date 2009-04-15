package edu.cmu.cs.fusion.constraint;

public class SpecVar {
	private static int UNIQUE = 0;
	private String var;
	private boolean isWildcard;

	public SpecVar() {
		isWildcard = true;
		var = "_" + UNIQUE;
		UNIQUE++;
	}

	public SpecVar(String var) {
		isWildcard = false;
		this.var = var;
	}

	public boolean isWildCard() {
		return isWildcard;
	}

	public String getVar() {
		return var;
	}

}
