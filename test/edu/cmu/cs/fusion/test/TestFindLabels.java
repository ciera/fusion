package edu.cmu.cs.fusion.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.Binding;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.Variant;
import edu.cmu.cs.fusion.alias.ObjectLabel;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.test.constraint.operations.StubVariable;
import edu.cmu.cs.fusion.test.lattice.AbstractObjectLabel;

public class TestFindLabels {
	static private TestAliasContext aliases;
	static private ObjectLabel[] labels;
	static private StubVariable[] vars;
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
		aliases = new TestAliasContext();
		
		labels = new ObjectLabel[7];
		labels[0] = new AbstractObjectLabel("0", "Foo");
		labels[1] = new AbstractObjectLabel("1", "Bar");
		labels[2] = new AbstractObjectLabel("2", "Baz");
		labels[3] = new AbstractObjectLabel("3", "Foo");
		labels[4] = new AbstractObjectLabel("4", "SnaFu");
		labels[5] = new AbstractObjectLabel("5", "Bar");
		labels[6] = new AbstractObjectLabel("6", "Bazar");
		
		vars = new StubVariable[5];
		vars[0] = new StubVariable();
		vars[1] = new StubVariable();
		vars[2] = new StubVariable();
		vars[3] = new StubVariable();
		vars[4] = new StubVariable();
		

		aliases.addAlias(vars[0], labels[0]);
		aliases.addAlias(vars[1], labels[1]);

		aliases.addAlias(vars[2], labels[3]);
		aliases.addAlias(vars[2], labels[4]);
		
		aliases.addAlias(vars[3], labels[0]);
		aliases.addAlias(vars[3], labels[2]);
		
		aliases.addAlias(vars[4], labels[6]);
		aliases.addAlias(vars[4], labels[5]);
		aliases.addAlias(vars[4], labels[3]);
		

	}
	
	@Test
	public void testEmptyFindLabels() {
		FusionEnvironment<?> env = new FusionEnvironment<TestAliasContext>(aliases, null, null, testH, new InferenceEnvironment(null), Variant.PRAGMATIC_VARIANT);
		FreeVars fv = new FreeVars();
		ConsList<Binding> emptyList = ConsList.empty();
		List<Substitution> subs = env.findLabels(emptyList, fv);
		
		assertEquals(1, subs.size());
		
		Iterator<Substitution> itr = subs.iterator();
		assertTrue(itr.hasNext());
		assertEquals(0, itr.next().size());
	}
	
	@Test
	public void testFindLabelsOneOption() {
		FusionEnvironment<?> env = new FusionEnvironment<TestAliasContext>(aliases, null, null, testH, new InferenceEnvironment(null), Variant.PRAGMATIC_VARIANT);
		FreeVars fv = new FreeVars().addVar(new SpecVar("a"), "Foo").addVar(new SpecVar("b"), "Bar");
		ConsList<Binding> list = ConsList.empty();
		
		list = ConsList.cons(new Binding(new SpecVar("a"), vars[0]), list);
		list = ConsList.cons(new Binding(new SpecVar("b"), vars[1]), list);
		
		List<Substitution> subs = env.findLabels(list, fv);
		
		assertEquals(1, subs.size());
		
		Iterator<Substitution> itr = subs.iterator();
		assertTrue(itr.hasNext());
		Substitution sub = itr.next();
		
		assertEquals(2, sub.size());
		assertEquals(labels[0], sub.getSub(new SpecVar("a")));
		assertEquals(labels[1], sub.getSub(new SpecVar("b")));	
	}

	@Test
	public void testFindLabelsAliasesAllDefinite() {
		FusionEnvironment<?> env = new FusionEnvironment<TestAliasContext>(aliases, null, null, testH, new InferenceEnvironment(null), Variant.PRAGMATIC_VARIANT);
		FreeVars fv = new FreeVars().addVar(new SpecVar("a"), "Foo").addVar(new SpecVar("b"), "Bar");
		ConsList<Binding> list = ConsList.empty();
		
		list = ConsList.cons(new Binding(new SpecVar("a"), vars[2]), list);
		list = ConsList.cons(new Binding(new SpecVar("b"), vars[1]), list);
		
		List<Substitution> subs = env.findLabels(list, fv);
		
		assertEquals(2, subs.size());
		
		Iterator<Substitution> itr = subs.iterator();
		assertTrue(itr.hasNext());
		Substitution subA = itr.next();
		assertTrue(itr.hasNext());
		Substitution subB = itr.next();
		
		assertEquals(2, subA.size());
		assertEquals(2, subB.size());
		
		if (subA.getSub(new SpecVar("a")).equals(labels[4])) {
			assertEquals(labels[1], subA.getSub(new SpecVar("b")));			
			assertEquals(labels[3], subB.getSub(new SpecVar("a")));			
			assertEquals(labels[1], subB.getSub(new SpecVar("b")));			
		}
		else {
			assertEquals(labels[3], subA.getSub(new SpecVar("a")));			
			assertEquals(labels[1], subA.getSub(new SpecVar("b")));			
			assertEquals(labels[4], subB.getSub(new SpecVar("a")));			
			assertEquals(labels[1], subB.getSub(new SpecVar("b")));						
		}
	}
	
	@Test
	public void testFindLabelsPossibleFromSuperTypes() {
		FusionEnvironment<?> env = new FusionEnvironment<TestAliasContext>(aliases, null, null, testH, new InferenceEnvironment(null), Variant.PRAGMATIC_VARIANT);
		FreeVars fv = new FreeVars().addVar(new SpecVar("a"), "SnaFu").addVar(new SpecVar("b"), "Baz");
		ConsList<Binding> list = ConsList.empty();
		
		list = ConsList.cons(new Binding(new SpecVar("a"), vars[3]), list);
		list = ConsList.cons(new Binding(new SpecVar("b"), vars[4]), list);
		
		List<Substitution> subs = env.findLabels(list, fv);
		
		assertEquals(2, subs.size());

		Iterator<Substitution> itr = subs.iterator();
		assertTrue(itr.hasNext());
		Substitution subA = itr.next();
		assertTrue(itr.hasNext());
		Substitution subB = itr.next();	
		
		assertEquals(2, subA.size());
		assertEquals(2, subB.size());
		
		if (subA.getSub(new SpecVar("b")).equals(labels[6])) {
			assertEquals(labels[0], subA.getSub(new SpecVar("a")));			
			assertEquals(labels[5], subB.getSub(new SpecVar("b")));			
			assertEquals(labels[0], subB.getSub(new SpecVar("a")));			
		}
		else {
			assertEquals(labels[5], subA.getSub(new SpecVar("b")));			
			assertEquals(labels[0], subA.getSub(new SpecVar("a")));			
			assertEquals(labels[6], subB.getSub(new SpecVar("b")));			
			assertEquals(labels[0], subB.getSub(new SpecVar("a")));						
		}
	}

	@Test
	public void testFindLabelsDefAndPoss() {
		FusionEnvironment<?> env = new FusionEnvironment<TestAliasContext>(aliases, null, null, testH, new InferenceEnvironment(null), Variant.PRAGMATIC_VARIANT);
		FreeVars fv = new FreeVars().addVar(new SpecVar("a"), "SnaFu").addVar(new SpecVar("b"), "Bazar");
		ConsList<Binding> list = ConsList.empty();
		
		list = ConsList.cons(new Binding(new SpecVar("a"), vars[2]), list);
		list = ConsList.cons(new Binding(new SpecVar("b"), vars[4]), list);
		
		List<Substitution> subs = env.findLabels(list, fv);
		
		assertEquals(4, subs.size());

		Iterator<Substitution> itr = subs.iterator();
		assertTrue(itr.hasNext());
		Substitution subA = itr.next();
		assertTrue(itr.hasNext());
		Substitution subB = itr.next();
		
		assertTrue(itr.hasNext());
		Substitution subC = itr.next();
		assertTrue(itr.hasNext());
		Substitution subD = itr.next();	
		
		assertEquals(2, subA.size());
		assertEquals(2, subB.size());
		assertEquals(2, subC.size());	
		assertEquals(2, subD.size());
	}
}
