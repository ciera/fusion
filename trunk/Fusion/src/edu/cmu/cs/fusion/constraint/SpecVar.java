package edu.cmu.cs.fusion.constraint;

public class SpecVar {
	public static final String WILD_CARD = "*";
	private static int UNIQUE = 0;
	private String var;
	private boolean isWildCard;

	public SpecVar() {
		this(false);
	}

	public SpecVar(String var) {
		this.var = var;
		isWildCard = false;
	}

	private SpecVar(boolean wildCard) {
		var = (wildCard ? WILD_CARD : "_") + Integer.toString(UNIQUE);
		isWildCard = wildCard;
		UNIQUE++;
	}

	static public SpecVar createWildCard() {
		return new SpecVar(true);
	}
	
	public boolean isWildCard() {
		return isWildCard;
	}

	public String getVar() {
		return var;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		if (var == null) {
			if (other.var != null)
				return false;
		} else if (!var.equals(other.var))
			return false;
		return true;
	}
	
	public String toString() {
		return var;
	}
}
