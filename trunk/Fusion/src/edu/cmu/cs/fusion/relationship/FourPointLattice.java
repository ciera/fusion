package edu.cmu.cs.fusion.relationship;

import edu.cmu.cs.fusion.ThreeValue;

/**
 * The four point lattice, including bottom.
 * @author ciera
 *
 */
public enum FourPointLattice {
	TRU, FAL, UNK, BOT;

	public static FourPointLattice convert(ThreeValue tv) {
		if (tv == null) 
			return BOT;
		else if (tv == ThreeValue.TRUE)
			return TRU;
		else if (tv == ThreeValue.FALSE)
			return FAL;
		else
			return UNK;
	}
}
