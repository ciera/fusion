package edu.cmu.cs.fusion.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.TypeHierarchy;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.SubPair;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.test.constraint.operations.StubVariable;
import edu.cmu.cs.fusion.test.lattice.AbstractObjectLabel;

public class TestAllValidSubs {
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
		
		labels = new ObjectLabel[8];
		labels[0] = new AbstractObjectLabel("0", "Foo");
		labels[1] = new AbstractObjectLabel("1", "Bar");
		labels[2] = new AbstractObjectLabel("2", "Baz");
		labels[3] = new AbstractObjectLabel("3", "Foo");
		labels[4] = new AbstractObjectLabel("4", "SnaFu");
		labels[5] = new AbstractObjectLabel("5", "Bar");
		labels[6] = new AbstractObjectLabel("6", "Bazar");
		labels[7] = new AbstractObjectLabel("7", "Narf");
		
		vars = new StubVariable[6];
		vars[0] = new StubVariable();
		vars[1] = new StubVariable();
		vars[2] = new StubVariable();
		vars[3] = new StubVariable();
		vars[4] = new StubVariable();
		vars[5] = new StubVariable();
		

		aliases.addAlias(vars[0], labels[0]);
		aliases.addAlias(vars[1], labels[1]);

		aliases.addAlias(vars[2], labels[3]);
		aliases.addAlias(vars[2], labels[4]);
		
		aliases.addAlias(vars[3], labels[0]);
		aliases.addAlias(vars[3], labels[2]);
		
		aliases.addAlias(vars[4], labels[6]);
		aliases.addAlias(vars[4], labels[5]);
		aliases.addAlias(vars[4], labels[3]);
		
		aliases.addAlias(vars[5], labels[7]);
	}
	
	@Test
	public void testEmptyValidLabels() {
		FusionEnvironment env = new FusionEnvironment(aliases, null, null, testH, new InferenceEnvironment());
		FreeVars fv = new FreeVars().addVar(new SpecVar("a"), "Foo").addVar(new SpecVar("b"), "Bar");
		Substitution existing = new Substitution().addSub(new SpecVar("a"), labels[0]).addSub(new SpecVar("b"), labels[1]);
		
		SubPair pair = env.allValidSubs(existing, fv);
		
		Iterator<Substitution> itr = pair.getPossibleSubstitutions();
		assertTrue(!itr.hasNext());
		
		itr = pair.getDefiniteSubstitutions();
		assertTrue(itr.hasNext());
		assertEquals(existing, itr.next());
		assertTrue(!itr.hasNext());
	}
	
	@Test
	public void testFindLabelsOneOption() {
		FusionEnvironment env = new FusionEnvironment(aliases, null, null, testH, new InferenceEnvironment());
		FreeVars fv = new FreeVars().addVar(new SpecVar("a"), "Foo").addVar(new SpecVar("b"), "Bar");
		Substitution existing = new Substitution().addSub(new SpecVar("a"), labels[0]).addSub(new SpecVar("b"), labels[1]);
		
		fv = fv.addVar(new SpecVar("c"), "Narf");
		
		SubPair pair = env.allValidSubs(existing, fv);
		
		Iterator<Substitution> itr = pair.getPossibleSubstitutions();
		assertTrue(!itr.hasNext());
		
		itr = pair.getDefiniteSubstitutions();
		assertTrue(itr.hasNext());
		Substitution sub = itr.next();
		assertTrue(!itr.hasNext());		
		
		assertEquals(3, sub.size());
		assertEquals(labels[0], sub.getSub(new SpecVar("a")));
		assertEquals(labels[1], sub.getSub(new SpecVar("b")));
		assertEquals(labels[7], sub.getSub(new SpecVar("c")));
	}

	@Test
	public void testFindLabelsAliasesAllDefinite() {
		FusionEnvironment env = new FusionEnvironment(aliases, null, null, testH, new InferenceEnvironment());
		FreeVars fv = new FreeVars().addVar(new SpecVar("a"), "Foo").addVar(new SpecVar("b"), "Bar");
		Substitution existing = new Substitution().addSub(new SpecVar("a"), labels[0]).addSub(new SpecVar("b"), labels[1]);
		
		fv = fv.addVar(new SpecVar("c"), "Foo");
		
		SubPair pair = env.allValidSubs(existing, fv);
	
		Iterator<Substitution> itr = pair.getPossibleSubstitutions();
		assertTrue(!itr.hasNext());

		itr = pair.getDefiniteSubstitutions();
		assertTrue(itr.hasNext());
		Substitution subA = itr.next();
		assertTrue(itr.hasNext());
		Substitution subB = itr.next();
		assertTrue(itr.hasNext());		
		Substitution subC = itr.next();
		assertTrue(!itr.hasNext());		
		
		assertEquals(3, subA.size());
		assertEquals(3, subB.size());
		assertEquals(3, subC.size());
		
		if (subA.getSub(new SpecVar("c")).equals(labels[0])) {
			if (subB.getSub(new SpecVar("c")).equals(labels[3])) {
				assertEquals(labels[4], subC.getSub(new SpecVar("c")));	
			}
			else {
				assertEquals(labels[4], subB.getSub(new SpecVar("c")));	
				assertEquals(labels[3], subC.getSub(new SpecVar("c")));				
			}
		}
		else if (subA.getSub(new SpecVar("c")).equals(labels[3])) {
			if (subB.getSub(new SpecVar("c")).equals(labels[0])) {
				assertEquals(labels[4], subC.getSub(new SpecVar("c")));	
			}
			else {
				assertEquals(labels[4], subB.getSub(new SpecVar("c")));	
				assertEquals(labels[0], subC.getSub(new SpecVar("c")));				
			}
		}
		else {
			assertEquals(labels[4], subA.getSub(new SpecVar("c")));	
			if (subB.getSub(new SpecVar("c")).equals(labels[0])) {
				assertEquals(labels[3], subC.getSub(new SpecVar("c")));	
			}
			else {
				assertEquals(labels[3], subB.getSub(new SpecVar("c")));	
				assertEquals(labels[0], subC.getSub(new SpecVar("c")));				
			}
		}
	}
	
	@Test
	public void testFindLabelsPossibleFromSuperTypes() {
		FusionEnvironment env = new FusionEnvironment(aliases, null, null, testH, new InferenceEnvironment());
		FreeVars fv = new FreeVars().addVar(new SpecVar("a"), "Foo").addVar(new SpecVar("b"), "Bar");
		Substitution existing = new Substitution().addSub(new SpecVar("a"), labels[0]).addSub(new SpecVar("b"), labels[1]);
		
		fv = fv.addVar(new SpecVar("c"), "Baz");
		
		SubPair pair = env.allValidSubs(existing, fv);
	
		Iterator<Substitution> itr = pair.getPossibleSubstitutions();
		assertTrue(itr.hasNext());
		Substitution subA = itr.next();
		assertTrue(itr.hasNext());
		Substitution subB = itr.next();
		assertTrue(!itr.hasNext());		

		assertEquals(3, subA.size());
		assertEquals(3, subB.size());
		
		if (subA.getSub(new SpecVar("c")).equals(labels[1]))
				assertEquals(labels[5], subB.getSub(new SpecVar("c")));	
		else {
			assertEquals(labels[5], subA.getSub(new SpecVar("c")));	
			assertEquals(labels[1], subB.getSub(new SpecVar("c")));				
		}

		itr = pair.getDefiniteSubstitutions();
		assertTrue(itr.hasNext());
		Substitution subC = itr.next();
		assertTrue(itr.hasNext());
		Substitution subD = itr.next();
		assertTrue(!itr.hasNext());		
		
		assertEquals(3, subC.size());
		assertEquals(3, subD.size());
		
		if (subC.getSub(new SpecVar("c")).equals(labels[2]))
			assertEquals(labels[6], subD.getSub(new SpecVar("c")));	
		else {
			assertEquals(labels[6], subC.getSub(new SpecVar("c")));	
			assertEquals(labels[2], subD.getSub(new SpecVar("c")));				
		}
	}

}
