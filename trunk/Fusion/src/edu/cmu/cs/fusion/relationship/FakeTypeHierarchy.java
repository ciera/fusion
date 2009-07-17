package edu.cmu.cs.fusion.relationship;

import edu.cmu.cs.fusion.TypeHierarchy;

public class FakeTypeHierarchy implements TypeHierarchy {

	public boolean existsCommonSubtype(String t1, String t2) {
		if (isSubtypeCompatible(t1, t2) || isSubtypeCompatible(t2, t1))
			return true;
		else
			return false;
	}

	public boolean isSubtypeCompatible(String subType, String superType) {
		if (subType.equals(superType) || superType.equals("java.lang.Object"))
			return true;
		else if (subType.equals("edu.cmu.cs.fusion.test.aspnet.api.DropDownList"))
			return superType.equals("edu.cmu.cs.fusion.test.aspnet.api.ListControl");
		else
			return false;
	}

}
