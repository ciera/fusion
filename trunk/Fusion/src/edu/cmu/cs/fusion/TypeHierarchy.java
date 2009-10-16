package edu.cmu.cs.fusion;

public interface TypeHierarchy {

	/**
	 * 
	 * @param subType A fully qualified type name in the classpath
	 * @param superType A fully qualified type name in the classpath
	 * @return true if subType <: superType
	 */
	public abstract boolean isSubtypeCompatible(String subType, String superType);

	/**
	 * 
	 * @param t1 A fully qualified type name in the classpath
	 * @param t2 A fully qualified type name in the classpath
	 * @return true if there exists some t3 such that t3 <: t1 and t3 <: t2
	 */
	public abstract boolean existsCommonSubtype(String t1, String t2);

}