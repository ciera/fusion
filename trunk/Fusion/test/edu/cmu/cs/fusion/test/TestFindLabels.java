package edu.cmu.cs.fusion.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.tac.Variable;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.TypeHierarchy;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.SubPair;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.test.constraint.operations.StubVariable;
import edu.cmu.cs.fusion.test.lattice.AbstractObjectLabel;

public class TestFindLabels {
	static private TestAliasContext aliases;
	static private ObjectLabel[] labels;
	static private StubVariable[] vars;
	static private TypeHierarchy testH = new TypeHierarchy() {
		public boolean existsCommonSubtype(String t1, String t2) {
			if (isSubtypeCompatible(t1, t2) || isSubtypeCompatible(t2, t1))
				return true;
			else if (t1.equals("Bar"))
				return t2.equals("Baz");
			else if (t1.equals("Baz"))
				return t2.equals("Bar");
			else
				return false;
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
		FusionEnvironment env = new FusionEnvironment(aliases, null, null, testH);
		FreeVars fv = new FreeVars().addVar(new SpecVar(), "Foo").addVar(new SpecVar(), "Bar");
		ConsList<Pair<SpecVar, Variable>> emptyList = ConsList.empty();
		SubPair pair = env.findLabels(emptyList, fv);
		
		Iterator<Substitution> itr = pair.getPossibleSubstitutions();
		assertTrue(!itr.hasNext());
		
		itr = pair.getDefiniteSubstitutions();
		assertTrue(itr.hasNext());
		assertEquals(0, itr.next().size());
		assertTrue(!itr.hasNext());
	}
	
	@Test
	public void testFindLabelsOneOption() {
		FusionEnvironment env = new FusionEnvironment(aliases, null, null, testH);
		FreeVars fv = new FreeVars().addVar(new SpecVar("a"), "Foo").addVar(new SpecVar("b"), "Bar");
		ConsList<Pair<SpecVar, Variable>> list = ConsList.empty();
		
		list = ConsList.cons(new Pair<SpecVar, Variable>(new SpecVar("a"), vars[0]), list);
		list = ConsList.cons(new Pair<SpecVar, Variable>(new SpecVar("b"), vars[1]), list);
		
		SubPair pair = env.findLabels(list, fv);
		
		Iterator<Substitution> itr = pair.getPossibleSubstitutions();
		assertTrue(!itr.hasNext());
		
		itr = pair.getDefiniteSubstitutions();
		assertTrue(itr.hasNext());
		Substitution sub = itr.next();
		
		assertEquals(2, sub.size());
		assertEquals(labels[0], sub.getSub(new SpecVar("a")));
		assertEquals(labels[1], sub.getSub(new SpecVar("b")));
		
		
		assertTrue(!itr.hasNext());		
	}

	@Test
	public void testFindLabelsAliasesAllDefinite() {
		FusionEnvironment env = new FusionEnvironment(aliases, null, null, testH);
		FreeVars fv = new FreeVars().addVar(new SpecVar("a"), "Foo").addVar(new SpecVar("b"), "Bar");
		ConsList<Pair<SpecVar, Variable>> list = ConsList.empty();
		
		list = ConsList.cons(new Pair<SpecVar, Variable>(new SpecVar("a"), vars[2]), list);
		list = ConsList.cons(new Pair<SpecVar, Variable>(new SpecVar("b"), vars[1]), list);
		
		SubPair pair = env.findLabels(list, fv);
		
		Iterator<Substitution> itr = pair.getPossibleSubstitutions();
		assertTrue(!itr.hasNext());
		
		itr = pair.getDefiniteSubstitutions();
		assertTrue(itr.hasNext());
		Substitution subA = itr.next();
		assertTrue(itr.hasNext());
		Substitution subB = itr.next();
		assertTrue(!itr.hasNext());		
		
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
		FusionEnvironment env = new FusionEnvironment(aliases, null, null, testH);
		FreeVars fv = new FreeVars().addVar(new SpecVar("a"), "SnaFu").addVar(new SpecVar("b"), "Baz");
		ConsList<Pair<SpecVar, Variable>> list = ConsList.empty();
		
		list = ConsList.cons(new Pair<SpecVar, Variable>(new SpecVar("a"), vars[3]), list);
		list = ConsList.cons(new Pair<SpecVar, Variable>(new SpecVar("b"), vars[4]), list);
		
		SubPair pair = env.findLabels(list, fv);
		
		Iterator<Substitution> itr = pair.getDefiniteSubstitutions();
		assertTrue(!itr.hasNext());
		
		itr = pair.getPossibleSubstitutions();
		assertTrue(itr.hasNext());
		Substitution subA = itr.next();
		assertTrue(itr.hasNext());
		Substitution subB = itr.next();
		assertTrue(!itr.hasNext());		
		
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
		FusionEnvironment env = new FusionEnvironment(aliases, null, null, testH);
		FreeVars fv = new FreeVars().addVar(new SpecVar("a"), "SnaFu").addVar(new SpecVar("b"), "Bazar");
		ConsList<Pair<SpecVar, Variable>> list = ConsList.empty();
		
		list = ConsList.cons(new Pair<SpecVar, Variable>(new SpecVar("a"), vars[2]), list);
		list = ConsList.cons(new Pair<SpecVar, Variable>(new SpecVar("b"), vars[4]), list);
		
		SubPair pair = env.findLabels(list, fv);
		
		Iterator<Substitution> itr = pair.getDefiniteSubstitutions();
		assertTrue(itr.hasNext());
		Substitution sub = itr.next();
		assertEquals(2, sub.size());
		assertEquals(labels[4], sub.getSub(new SpecVar("a")));			
		assertEquals(labels[6], sub.getSub(new SpecVar("b")));			
		assertTrue(!itr.hasNext());
		
		itr = pair.getPossibleSubstitutions();
		assertTrue(itr.hasNext());
		Substitution subA = itr.next();
		assertTrue(itr.hasNext());
		Substitution subB = itr.next();
		assertTrue(itr.hasNext());
		Substitution subC = itr.next();
		assertTrue(!itr.hasNext());		
		
		assertEquals(2, subA.size());
		assertEquals(2, subB.size());
		assertEquals(2, subC.size());	
	}
}
