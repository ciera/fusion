package edu.cmu.cs.fusion.relationship;

import edu.cmu.cs.fusion.ThreeValue;


/**
 * The six point lattice, not including a bottom.
 * @author ciera
 *
 */
public enum FivePointLattice {
	TRU, FAL, UNK, TRU_STAR, FAL_STAR, STAR;
	
	/**
	 * This is a polarizing operation. TRU goes to TRU_STAR, and FAL goes to FAL_STAR.
	 * All other enums return themselves.
	 * @return
	 */
	public FivePointLattice polarize() {
		if (this == TRU)
			return TRU_STAR;
		else if (this == FAL)
			return FAL_STAR;
		else
			return this;
	}
	
	/**
	 * The expected join operation.
	 * @param other
	 * @return
	 */
	public FivePointLattice join(FivePointLattice other) {
		if (this == UNK || other == UNK)
			return UNK;
		else if (this == other)
			return this;
		else if (this == STAR || other == STAR) {
			if (this == TRU || this == TRU_STAR || other == TRU || other == TRU_STAR)
				return TRU_STAR;
			else
				return FAL_STAR;
		}
		else if (this == TRU || this == TRU_STAR)
			return (other == TRU || other == TRU_STAR) ? TRU_STAR : UNK;
		return (other == FAL || other == FAL_STAR) ? FAL_STAR : UNK;	
	}
	
	/**
	 * This join operation works a little differently. It will
	 * place TRU and FAL above TRU_STAR and FAL_STAR respectively. This will in turn
	 * cause STAR to be bottom.
	 * @param other
	 * @return
	 */
	public FivePointLattice joinAlt(FivePointLattice other) {
		if (this == UNK || other == UNK)
			return UNK;
		else if (this == other)
			return this;
		else if (this == STAR)
			return other;
		else if (other == STAR)
			return this;
		else if (this == TRU || this == TRU_STAR)
			return (other == TRU || other == TRU_STAR) ? TRU : UNK;
		return (other == FAL || other == FAL_STAR) ? FAL : UNK;	
	}
	
	
	/**
	 * The overriding operator for the main lattice. This is particularly used when
	 * creating effects that need to be combined in order of appearance.
	 * @param other
	 * @return
	 */
	public FivePointLattice override(FivePointLattice override) {
		if (override != STAR)
			return override;
		else
			return this;
	}

	/**
	 * The overriding operator for three values.
	 * @param initial
	 * @return
	 */
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
