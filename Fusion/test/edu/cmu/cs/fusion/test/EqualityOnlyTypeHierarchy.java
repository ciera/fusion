package edu.cmu.cs.fusion.test;

import org.w3c.dom.Document;

import edu.cmu.cs.crystal.util.TypeHierarchy;

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
		return existsCommonSubtype(t1, t2, false, false);
	}
	
	/* (non-Javadoc)
	 * @see edu.cmu.cs.fusion.TypeHierarchy#existsCommonSubtype(java.lang.String, java.lang.String)
	 */
	public boolean existsCommonSubtype(String t1, String t2, boolean skipCheck1, boolean skipCheck2) {
		return !skipCheck1 && isSubtypeCompatible(t1, t2) || !skipCheck2 && isSubtypeCompatible(t2, t1);
	}
	public void sendToXML(Document doc) {
	}

}
