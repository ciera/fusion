package edu.cmu.cs.fusion;

public enum ThreeValue {
	TRUE, FALSE, UNKNOWN;

	public ThreeValue negate() {
		switch (this) {
		case TRUE: return FALSE;
		case FALSE: return TRUE;
		default: return UNKNOWN;
		}
	}
}
