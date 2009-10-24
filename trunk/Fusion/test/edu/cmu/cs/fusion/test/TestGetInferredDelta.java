package edu.cmu.cs.fusion.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.constraint.Effect;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.constraint.InferredRel;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.constraint.predicates.RelationshipPredicate;
import edu.cmu.cs.fusion.relationship.FourPointLattice;
import edu.cmu.cs.fusion.relationship.RelationshipContext;
import edu.cmu.cs.fusion.relationship.RelationshipDelta;
import edu.cmu.cs.fusion.test.constraint.operations.StubVariable;
import edu.cmu.cs.fusion.test.lattice.AbstractObjectLabel;


public class TestGetInferredDelta {
	static private TestAliasContext aliases;
	static private ObjectLabel[] labels;
	static private StubVariable[] vars;
	static private RelationshipContext ctx;
	
	private static Relation[] relations;
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
		
		labels = new ObjectLabel[4];
		labels[0] = new AbstractObjectLabel("0", "Foo");
		labels[1] = new AbstractObjectLabel("1", "Bar");
		labels[2] = new AbstractObjectLabel("2", "Foo");
		labels[3] = new AbstractObjectLabel("3", "Bar");

		
		vars = new StubVariable[5];
		vars[0] = new StubVariable();
		vars[1] = new StubVariable();
		vars[2] = new StubVariable();
		vars[3] = new StubVariable();
		vars[4] = new StubVariable();
		
		aliases.addAlias(vars[0], labels[0]);
		aliases.addAlias(vars[1], labels[1]);
		aliases.addAlias(vars[2], labels[3]);
		aliases.addAlias(vars[3], labels[0]);
		aliases.addAlias(vars[4], labels[2]);
		aliases.addAlias(vars[4], labels[3]);

		relations = new Relation[2];
		relations[0] = new Relation("A", new String[] {"Foo", "Bar"});
		relations[1] = new Relation("B", new String[] {"Foo", "Foo"});
		
		ctx = new RelationshipContext(false);
		RelationshipDelta delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[1]}), FourPointLattice.TRU);
		ctx = ctx.applyChangesFromDelta(delta);
	}

	/**
	 * rho = A(0, 1)
	 * A(x, y) -> B(x, y)
	 * Query for A(0, 0)
	 */
	@Test
	public void testNoMatchForProduce() {
		InferenceEnvironment infEnv = new InferenceEnvironment();
		FusionEnvironment env = new FusionEnvironment(aliases, ctx, null, testH, infEnv);
		
		RelationshipPredicate trigger = new RelationshipPredicate(relations[0], new SpecVar[] {new SpecVar("a"), new SpecVar("b")});
		List<Effect> eff = new LinkedList<Effect>();
		eff.add(Effect.createAddEffect(relations[1], new SpecVar[] {new SpecVar("a"), new SpecVar("b")}));
		
		InferredRel inf = new InferredRel(trigger, eff);
		infEnv.addRule(inf);
		
		RelationshipPredicate find = new RelationshipPredicate(relations[0], new SpecVar[] {new SpecVar("a"), new SpecVar("a")});
		Substitution sub = new Substitution();
		sub = sub.addSub(new SpecVar("a"), labels[0]);
				
		assertNull(env.getInferredDelta(find, sub));
	}
	
	
	/**
	 * rho = A(0, 1)
	 * A(x, y) -> A(y, x)
	 * Query for A(0, 0)
	 */
	@Test
	public void testNoVariablesWork() {
		InferenceEnvironment infEnv = new InferenceEnvironment();
		FusionEnvironment env = new FusionEnvironment(aliases, ctx, null, testH, infEnv);
		
		RelationshipPredicate trigger = new RelationshipPredicate(relations[0], new SpecVar[] {new SpecVar("a"), new SpecVar("b")});
		List<Effect> eff = new LinkedList<Effect>();
		eff.add(Effect.createAddEffect(relations[0], new SpecVar[] {new SpecVar("b"), new SpecVar("a")}));
		
		InferredRel inf = new InferredRel(trigger, eff);
		infEnv.addRule(inf);
		
		RelationshipPredicate find = new RelationshipPredicate(relations[0], new SpecVar[] {new SpecVar("a"), new SpecVar("a")});
		Substitution sub = new Substitution();
		sub = sub.addSub(new SpecVar("a"), labels[0]);
		sub = sub.addSub(new SpecVar("b"), labels[1]);
		
		assertNull(env.getInferredDelta(find, sub));		
	}
	
	/**
	 * rho = A(0, 1)
	 * A(x, y) -> !A(x, x)
	 * Query for A(0, 0)
	 */
	@Test
	public void testWrongValue() {
		InferenceEnvironment infEnv = new InferenceEnvironment();
		FusionEnvironment env = new FusionEnvironment(aliases, ctx, null, testH, infEnv);
		
		RelationshipPredicate trigger = new RelationshipPredicate(relations[0], new SpecVar[] {new SpecVar("a"), new SpecVar("b")});
		List<Effect> eff = new LinkedList<Effect>();
		eff.add(Effect.createRemoveEffect(relations[0], new SpecVar[] {new SpecVar("a"), new SpecVar("a")}));
		
		InferredRel inf = new InferredRel(trigger, eff);
		infEnv.addRule(inf);
		
		RelationshipPredicate find = new RelationshipPredicate(relations[0], new SpecVar[] {new SpecVar("a"), new SpecVar("a")});
		Substitution sub = new Substitution();
		sub = sub.addSub(new SpecVar("a"), labels[0]);
		sub = sub.addSub(new SpecVar("b"), labels[1]);
		
		assertNull(env.getInferredDelta(find, sub));		
	}

	/**
	 * rho = A(0, 1)
	 * A(x, y) -> !A(x, y), B(x, x)
	 * Query for B(0, 0)
	 */
	@Test
	public void testThrashes() {
		InferenceEnvironment infEnv = new InferenceEnvironment();
		FusionEnvironment env = new FusionEnvironment(aliases, ctx, null, testH, infEnv);
		
		RelationshipPredicate trigger = new RelationshipPredicate(relations[0], new SpecVar[] {new SpecVar("x"), new SpecVar("y")});
		List<Effect> eff = new LinkedList<Effect>();
		eff.add(Effect.createRemoveEffect(relations[0], new SpecVar[] {new SpecVar("x"), new SpecVar("y")}));
		eff.add(Effect.createAddEffect(relations[1], new SpecVar[] {new SpecVar("x"), new SpecVar("x")}));
		
		InferredRel inf = new InferredRel(trigger, eff);
		infEnv.addRule(inf);
		
		RelationshipPredicate find = new RelationshipPredicate(relations[1], new SpecVar[] {new SpecVar("a"), new SpecVar("a")});
		Substitution sub = new Substitution();
		sub = sub.addSub(new SpecVar("a"), labels[0]);
		
		assertNull(env.getInferredDelta(find, sub));			
	}

	/**
	 * rho = A(0, 1)
	 * A(x, y) -> A(x, x)
	 * Query for A(0, 0)
	 */
	@Test
	public void testWorksSingleStepSame() {
		InferenceEnvironment infEnv = new InferenceEnvironment();
		FusionEnvironment env = new FusionEnvironment(aliases, ctx, null, testH, infEnv);
		
		RelationshipPredicate trigger = new RelationshipPredicate(relations[0], new SpecVar[] {new SpecVar("x"), new SpecVar("y")});
		List<Effect> eff = new LinkedList<Effect>();
		eff.add(Effect.createAddEffect(relations[0], new SpecVar[] {new SpecVar("x"), new SpecVar("x")}));
		
		InferredRel inf = new InferredRel(trigger, eff);
		infEnv.addRule(inf);
		
		RelationshipPredicate find = new RelationshipPredicate(relations[0], new SpecVar[] {new SpecVar("a"), new SpecVar("a")});
		Substitution sub = new Substitution();
		sub = sub.addSub(new SpecVar("a"), labels[0]);
		
		RelationshipDelta delta = env.getInferredDelta(find, sub);
		assertNotNull(delta);
		FourPointLattice val = delta.getValue(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[0]}));
		assertEquals(FourPointLattice.TRU, val);
		assertEquals(1, delta.numberOfChanges());	
	}

	/**
	 * rho = A(0, 1)
	 * A(x, y) -> B(x, x)
	 * Query for B(0, 0)
	 */
	@Test
	public void testWorksSingleStepDiff() {
		InferenceEnvironment infEnv = new InferenceEnvironment();
		FusionEnvironment env = new FusionEnvironment(aliases, ctx, null, testH, infEnv);
		
		RelationshipPredicate trigger = new RelationshipPredicate(relations[0], new SpecVar[] {new SpecVar("x"), new SpecVar("y")});
		List<Effect> eff = new LinkedList<Effect>();
		eff.add(Effect.createAddEffect(relations[1], new SpecVar[] {new SpecVar("x"), new SpecVar("x")}));
		
		InferredRel inf = new InferredRel(trigger, eff);
		infEnv.addRule(inf);
		
		RelationshipPredicate find = new RelationshipPredicate(relations[1], new SpecVar[] {new SpecVar("a"), new SpecVar("a")});
		Substitution sub = new Substitution();
		sub = sub.addSub(new SpecVar("a"), labels[0]);
		
		RelationshipDelta delta = env.getInferredDelta(find, sub);
		assertNotNull(delta);
		FourPointLattice val = delta.getValue(new Relationship(relations[1], new ObjectLabel[] {labels[0], labels[0]}));
		assertEquals(FourPointLattice.TRU, val);
		assertEquals(1, delta.numberOfChanges());		
	}

	/**
	 * rho = A(0, 1)
	 * A(x, y) -> B(x, x)
	 * B(x, x) -> A(x, x)
	 * Query for A(0, 0)
	 */
	@Test
	public void testWorksTwoSteps() {
		InferenceEnvironment infEnv = new InferenceEnvironment();
		FusionEnvironment env = new FusionEnvironment(aliases, ctx, null, testH, infEnv);
		
		RelationshipPredicate trigger = new RelationshipPredicate(relations[0], new SpecVar[] {new SpecVar("x"), new SpecVar("y")});
		List<Effect> eff = new LinkedList<Effect>();
		eff.add(Effect.createAddEffect(relations[1], new SpecVar[] {new SpecVar("x"), new SpecVar("x")}));
		
		infEnv.addRule(new InferredRel(trigger, eff));
		
		trigger = new RelationshipPredicate(relations[1], new SpecVar[] {new SpecVar("x"), new SpecVar("x")});
		eff = new LinkedList<Effect>();
		eff.add(Effect.createAddEffect(relations[0], new SpecVar[] {new SpecVar("x"), new SpecVar("x")}));
		
		infEnv.addRule(new InferredRel(trigger, eff));
		
		RelationshipPredicate find = new RelationshipPredicate(relations[1], new SpecVar[] {new SpecVar("a"), new SpecVar("a")});
		Substitution sub = new Substitution();
		sub = sub.addSub(new SpecVar("a"), labels[0]);
		
		RelationshipDelta delta = env.getInferredDelta(find, sub);
		assertNotNull(delta);
		FourPointLattice val = delta.getValue(new Relationship(relations[1], new ObjectLabel[] {labels[0], labels[0]}));
		assertEquals(FourPointLattice.TRU, val);
		assertEquals(1, delta.numberOfChanges());		
		
	}
}
