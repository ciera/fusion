package edu.cmu.cs.fusion.relationship;

import edu.cmu.cs.fusion.ThreeValue;


/**
 * The four point lattice, including bottom.
 * @author ciera
 *
 */
public enum FivePointLattice {
	TRU, FAL, UNK, TRU_STAR, FAL_STAR;
	
	public FivePointLattice polarize() {
		if (this == TRU)
			return TRU_STAR;
		else if (this == FAL)
			return FAL_STAR;
		else
			return this;
	}
	
	public FivePointLattice join(FivePointLattice other) {
		if (this == UNK || other == UNK)
			return UNK;
		else if (this == other)
			return this;
		else if (this == TRU || this == TRU_STAR)
			return (other == TRU || other == TRU_STAR) ? TRU_STAR : UNK;
		return (other == FAL || other == FAL_STAR) ? FAL_STAR : UNK;	
	}
	
	public ThreeValue override(ThreeValue initial) {
		if (this == UNK)
			return ThreeValue.UNKNOWN;
		else if (this == TRU)
			return ThreeValue.TRUE;
		else if (this == FAL)
			return ThreeValue.FALSE;
		else if (this == TRU_STAR)
			return initial == ThreeValue.TRUE ? ThreeValue.TRUE : ThreeValue.UNKNOWN;
		else if (this == FAL_STAR)
			return initial == ThreeValue.FALSE ? ThreeValue.FALSE : ThreeValue.UNKNOWN;
		else
			return initial;
	}
	
	public boolean isAtLeastAsPrecise(ThreeValue tv) {
		if (tv == ThreeValue.UNKNOWN)
			return this != UNK;
		else if (tv == ThreeValue.TRUE)
			return this == TRU || this == TRU_STAR;
		else
			return this == FAL || this == FAL_STAR;
	}

	public static FivePointLattice convert(ThreeValue tv) {
		if (tv == ThreeValue.TRUE)
			return TRU;
		else if (tv == ThreeValue.FALSE)
			return FAL;
		else
			return UNK;
		
	}
}
