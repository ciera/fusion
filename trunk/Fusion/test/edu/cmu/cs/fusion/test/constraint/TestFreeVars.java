package edu.cmu.cs.fusion.test.constraint;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.FusionException;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;

public class TestFreeVars {
	static private TypeHierarchy testH = new TypeHierarchy() {
		public boolean existsCommonSubtype(String t1, String t2, boolean skipCheck1, boolean skipCheck2) {
			if (!skipCheck1 && isSubtypeCompatible(t1, t2) || !skipCheck2 && isSubtypeCompatible(t2, t1))
				return true;
			else if (t1.equals("Bar"))
				return t2.equals("Baz");
			else if (t1.equals("Baz"))
				return t2.equals("Bar");
			else
				return false;
		}
		
		public boolean existsCommonSubtype(String t1, String t2) {
			return existsCommonSubtype(t1, t2, false, false);
		}

		public boolean isSubtypeCompatible(String subType, String superType) {
			if (subType.equals(superType))
				return true;
			else if (subType.equals("SnaFu"))
				return superType.equals("Foo");
			else if (subType.equals("Bazar"))
				return superType.equals("Baz") || superType.equals("Bar");
			else
				return false;
		}
	};

	@BeforeClass
	static public void setup() {
		FreeVars.setHierarchy(testH);
	}
	
	@Test
	public void testAdd() throws FusionException {
		FreeVars vars = new FreeVars();
		vars = vars.addVar(new SpecVar("foo"), "Foo");
		assertEquals(1, vars.size());
		assertEquals("Foo", vars.getType(new SpecVar("foo")));
	}
	
	@Test
	public void testUnionNormal() throws FusionException {
		FreeVars vars1 = new FreeVars();
		vars1 = vars1.addVar(new SpecVar("foo"), "Foo");

		FreeVars vars2 = new FreeVars();
		vars2 = vars2.addVar(new SpecVar("bar"), "Bar");
		vars2 = vars2.union(vars1);
		assertEquals(2, vars2.size());
		assertEquals("Foo", vars2.getType(new SpecVar("foo")));
		assertEquals("Bar", vars2.getType(new SpecVar("bar")));
	}

	@Test
	public void testUnionSubtype() throws FusionException {
		FreeVars vars1 = new FreeVars();
		vars1 = vars1.addVar(new SpecVar("foo"), "Foo");

		FreeVars vars2 = new FreeVars();
		vars2 = vars2.addVar(new SpecVar("foo"), "SnaFu");
		vars2 = vars2.addVar(new SpecVar("bar"), "Bar");
		vars2 = vars2.union(vars1);
		assertEquals(2, vars2.size());
		assertEquals("Foo", vars2.getType(new SpecVar("foo")));
		assertEquals("Bar", vars2.getType(new SpecVar("bar")));
	}

	@Test
	public void testUnionSupertype() throws FusionException {
		FreeVars vars1 = new FreeVars();
		vars1 = vars1.addVar(new SpecVar("foo"), "SnaFu");

		FreeVars vars2 = new FreeVars();
		vars2 = vars2.addVar(new SpecVar("foo"), "Foo");
		vars2 = vars2.addVar(new SpecVar("bar"), "Bar");
		vars2 = vars2.union(vars1);
		assertEquals(2, vars2.size());
		assertEquals("Foo", vars2.getType(new SpecVar("foo")));
		assertEquals("Bar", vars2.getType(new SpecVar("bar")));
	}
	
	@Test
	public void testUnionRemove() throws FusionException {
		FreeVars vars1 = new FreeVars();
		vars1 = vars1.addVar(new SpecVar("foo"), "SnaFu");
		vars1 = vars1.addVar(new SpecVar("bazar"), "Bazar");

		FreeVars vars2 = new FreeVars();
		vars2 = vars2.addVar(new SpecVar("foo"), "Foo");
		vars2 = vars2.addVar(new SpecVar("bar"), "Bar");
		vars2 = vars2.union(vars1).subtract(vars2);
		assertEquals(1, vars2.size());
		assertEquals("Bazar", vars2.getType(new SpecVar("bazar")));
	}

	@Test
	public void testUnionUnequal() throws FusionException {
		FreeVars vars1 = new FreeVars();
		vars1 = vars1.addVar(new SpecVar("foo"), "SnaFu");
		vars1 = vars1.addVar(new SpecVar("bar"), "Bazar");

		FreeVars vars2 = new FreeVars();
		vars2 = vars2.addVar(new SpecVar("foo"), "Bazar");
		vars2 = vars2.addVar(new SpecVar("bar"), "Snafu");
		vars2 = vars2.union(vars1);
		assertEquals(2, vars2.size());
		assertEquals(FreeVars.OBJECT_TYPE, vars2.getType(new SpecVar("foo")));
		assertEquals(FreeVars.OBJECT_TYPE, vars2.getType(new SpecVar("bar")));
	}

}