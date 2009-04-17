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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isWildcard ? 1231 : 1237);
		result = prime * result + ((var == null) ? 0 : var.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpecVar other = (SpecVar) obj;
		if (isWildcard != other.isWildcard)
			return false;
		if (var == null) {
			if (other.var != null)
				return false;
		} else if (!var.equals(other.var))
			return false;
		return true;
	}
	
}
