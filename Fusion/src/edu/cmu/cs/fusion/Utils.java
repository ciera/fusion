package edu.cmu.cs.fusion;

import java.util.Set;

public class Utils {
	/**
	 * Types which we have complete type hierarchy knowledge for.
	 */
	private Set<String> knownTypes;

	/**
	 * 
	 * @param subType A fully qualified type name in the classpath
	 * @param superType A fully qualified type name in the classpath
	 * @return true if subType <: superType
	 */
	public static boolean isSubtypeCompatible(String subType, String superType) {
		return subType.equals(superType);
	}

	/**
	 * 
	 * @param type1 A fully qualified type name in the classpath
	 * @param type2 A fully qualified type name in the classpath
	 * @return true if there exists some t3 such that t3 <: t1 and t3 <: t2
	 */
	public static boolean existsCommonSubtype(String t1, String t2) {
		return isSubtypeCompatible(t1, t2) || isSubtypeCompatible(t2, t1);
	}

}
