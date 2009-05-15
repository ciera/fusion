package edu.cmu.cs.fusion.test;

import edu.cmu.cs.fusion.TypeHierarchy;

public class EqualityOnlyTypeHierarchy implements TypeHierarchy {
	/* (non-Javadoc)
	 * @see edu.cmu.cs.fusion.TypeHierarchy#isSubtypeCompatible(java.lang.String, java.lang.String)
	 */
	public boolean isSubtypeCompatible(String subType, String superType) {
		return subType.equals(superType);
	}

	/* (non-Javadoc)
	 * @see edu.cmu.cs.fusion.TypeHierarchy#existsCommonSubtype(java.lang.String, java.lang.String)
	 */
	public boolean existsCommonSubtype(String t1, String t2) {
		return isSubtypeCompatible(t1, t2) || isSubtypeCompatible(t2, t1);
	}

}
