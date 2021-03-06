package edu.cmu.cs.fusion.relationship;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.FusionException;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.Variant;
import edu.cmu.cs.fusion.alias.AliasDelta;
import edu.cmu.cs.fusion.alias.ObjectLabel;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.Effect;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.constraint.Operation;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.RelEffect;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.operations.ConstructorOp;
import edu.cmu.cs.fusion.constraint.operations.MethodInvocationOp;
import edu.cmu.cs.fusion.constraint.predicates.TruePredicate;
import edu.cmu.cs.fusion.test.TestAliasContext;
import edu.cmu.cs.fusion.test.TestUtils;
import edu.cmu.cs.fusion.test.constraint.operations.StubMethodBinding;
import edu.cmu.cs.fusion.test.constraint.operations.StubMethodCallInstruction;
import edu.cmu.cs.fusion.test.constraint.operations.StubNewObjectInstruction;
import edu.cmu.cs.fusion.test.constraint.operations.StubVariable;
import edu.cmu.cs.fusion.test.lattice.AbstractObjectLabel;
import edu.cmu.cs.fusion.xml.NamedTypeBinding;

public class TestSingleConstraint extends ConstraintChecker {
	static Constraint cons;
	static TestUtils utils;
	private static ObjectLabel[] labels;

	@BeforeClass
	static public void setup() {
		utils = new TestUtils();
		
		Operation op;
		Predicate trigger;
		Predicate req;
		List<Effect> effects = new LinkedList<Effect>();
		
		op = new MethodInvocationOp("methodName", "Foo", new SpecVar[] {utils.getVar(0)}, new String[] {"Bar"}, "Foo", false);
		trigger = new TruePredicate();
		req = new TruePredicate();
		effects.add(RelEffect.createRemoveEffect(utils.getRelation(0), new SpecVar[] {Constraint.RECEIVER, utils.getVar(0)}));
		effects.add(RelEffect.createAddEffect(utils.getRelation(0), new SpecVar[] {Constraint.RESULT, utils.getVar(0)}));
		
		cons = new Constraint("", op, trigger, new TruePredicate(), req, effects);
		
		labels = new ObjectLabel[7];
		labels[0] = new AbstractObjectLabel("0", "Foo");
		labels[1] = new AbstractObjectLabel("1", "Bar");
		labels[2] = new AbstractObjectLabel("2", "Baz");
		labels[3] = new AbstractObjectLabel("3", "Foo");
		labels[4] = new AbstractObjectLabel("4", "SnaFu");
		labels[5] = new AbstractObjectLabel("5", "Bar");
		labels[6] = new AbstractObjectLabel("6", "Bazar");
		
	}
	
	public TestSingleConstraint() {
		super(null, null, Variant.PRAGMATIC_VARIANT, null);
		types = new TypeHierarchy() {
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
	}

	
	@Test
	/**
	 * Notice that the only way for there to be absolutely no matches is for the types to not match. If the types match, then
	 * there has to be some aliasing possible because at runtime, those variables are going to bind to at least one possible
	 * aliasing configuration!
	 */
	public void testNoMatches() throws FusionException {
		RelationshipContext rels = new RelationshipContext(false);
		
		StubMethodCallInstruction instr = getMCI();
		
		//op has type Foo.methodName(Foo) : Bar
		//instr has type Foo.m(Foo) : Bar
		
		TestAliasContext aliases = new TestAliasContext();
		aliases.addAlias(instr.getReceiverOperand(), labels[5]);
		aliases.addAlias(instr.getTarget(), labels[0]);
		aliases.addAlias(instr.getArgOperands().get(0), labels[3]);

		FusionEnvironment<?> env = new FusionEnvironment<TestAliasContext>(aliases, rels, null, types, new InferenceEnvironment(null), variant);		
		Pair<RelationshipDelta, AliasDelta> deltas = runSingleConstraint(env, cons, instr);
		FusionErrorReport error = checkSingleConstraint(env, cons, instr);
		
		assertEquals(0, deltas.fst().numberOfChanges());	
		assertEquals(new AliasDelta(), deltas.snd());
		assertNull(error);
	}
	
	@Test
	public void testDefConstructor() throws FusionException {
		RelationshipContext rels = new RelationshipContext(false);

		List<StubVariable> vars = new LinkedList<StubVariable>();
		vars.add(new StubVariable("p0", "Bar"));
		
		StubNewObjectInstruction instr = new StubNewObjectInstruction(vars,
				 new StubMethodBinding(new NamedTypeBinding("Foo"), new NamedTypeBinding[]{new NamedTypeBinding("Bar")}), new StubVariable("tVar", "Foo"));
		
		TestAliasContext aliases = new TestAliasContext();
		aliases.addAlias(instr.getTarget(), labels[3]);
		aliases.addAlias(instr.getArgOperands().get(0), labels[5]);
		
		AliasDelta expected = new AliasDelta();
		expected.addChange(instr.getTarget(), labels[3]);
		expected.addChange(instr.getArgOperands().get(0), labels[5]);	
		
		Operation op = new ConstructorOp("Foo", new SpecVar[] {utils.getVar(0)}, new String[] {"Bar"});
		List<Effect> effects = new LinkedList<Effect>();
		effects.add(RelEffect.createRemoveEffect(utils.getRelation(1), new SpecVar[] {utils.getVar(0), utils.getVar(0)}));
		effects.add(RelEffect.createAddEffect(utils.getRelation(0), new SpecVar[] {Constraint.RESULT, utils.getVar(0)}));
		
		Constraint cons = new Constraint("", op, new TruePredicate(), new TruePredicate(), new TruePredicate(), effects);

		FusionEnvironment<?> env = new FusionEnvironment<TestAliasContext>(aliases, rels, null, types, new InferenceEnvironment(null), variant);		
		Pair<RelationshipDelta, AliasDelta> deltas = runSingleConstraint(env, cons, instr);
		FusionErrorReport error = checkSingleConstraint(env, cons, instr);
		Relationship eff1 = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[5], labels[5]});
		Relationship eff2 = new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[3], labels[5]});

		assertEquals(2, deltas.fst().numberOfChanges());
		assertEquals(SevenPointLattice.FAL, deltas.fst().getValue(eff1));
		assertEquals(SevenPointLattice.TRU, deltas.fst().getValue(eff2));
		
		assertEquals(expected, deltas.snd());

		assertNull(error);			
	}

	
	@Test
	public void testDefOnly() throws FusionException {
		RelationshipContext rels = new RelationshipContext(false);

		StubMethodCallInstruction instr = getMCI();
		
		TestAliasContext aliases = new TestAliasContext();
		aliases.addAlias(instr.getReceiverOperand(), labels[0]);
		aliases.addAlias(instr.getTarget(), labels[3]);
		aliases.addAlias(instr.getArgOperands().get(0), labels[5]);
		
		AliasDelta expected = new AliasDelta();
		expected.addChange(instr.getReceiverOperand(), labels[0]);
		expected.addChange(instr.getTarget(), labels[3]);
		expected.addChange(instr.getArgOperands().get(0), labels[5]);	


		FusionEnvironment<?> env = new FusionEnvironment<TestAliasContext>(aliases, rels, null, types, new InferenceEnvironment(null), variant);		
		Pair<RelationshipDelta, AliasDelta> deltas = runSingleConstraint(env, cons, instr);
		FusionErrorReport error = checkSingleConstraint(env, cons, instr);
		Relationship eff1 = new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[5]});
		Relationship eff2 = new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[3], labels[5]});

		assertEquals(2, deltas.fst().numberOfChanges());
		assertEquals(SevenPointLattice.FAL, deltas.fst().getValue(eff1));
		assertEquals(SevenPointLattice.TRU, deltas.fst().getValue(eff2));
		
		assertEquals(expected, deltas.snd());
		
		assertNull(error);			
	}
	
	@Test
	public void testPartialOnly() throws FusionException {
		RelationshipContext rels = new RelationshipContext(false);

		StubMethodCallInstruction instr = getMCI();
		
		TestAliasContext aliases = new TestAliasContext();
		aliases.addAlias(instr.getReceiverOperand(), labels[0]);
		aliases.addAlias(instr.getTarget(), labels[3]);
		aliases.addAlias(instr.getArgOperands().get(0), labels[2]);
		
		AliasDelta expected = new AliasDelta();
		expected.addChange(instr.getReceiverOperand(), labels[0]);
		expected.addChange(instr.getTarget(), labels[3]);
		expected.addChange(instr.getArgOperands().get(0), labels[2]);	

		FusionEnvironment<?> env = new FusionEnvironment<TestAliasContext>(aliases, rels, null, types, new InferenceEnvironment(null), variant);		
		Pair<RelationshipDelta, AliasDelta> deltas = runSingleConstraint(env, cons, instr);
		FusionErrorReport error = checkSingleConstraint(env, cons, instr);
		Relationship eff1 = new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[2]});
		Relationship eff2 = new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[3], labels[2]});

		assertEquals(2, deltas.fst().numberOfChanges());
		assertEquals(SevenPointLattice.FAL, deltas.fst().getValue(eff1));
		assertEquals(SevenPointLattice.TRU, deltas.fst().getValue(eff2));
		assertEquals(expected, deltas.snd());
	
		assertNull(error);			
	}
	
	@Test
	public void testSeveralDef() throws FusionException {
		RelationshipContext rels = new RelationshipContext(false);

		StubMethodCallInstruction instr = getMCI();
		
		TestAliasContext aliases = new TestAliasContext();
		aliases.addAlias(instr.getReceiverOperand(), labels[0]);
		aliases.addAlias(instr.getTarget(), labels[3]);
		aliases.addAlias(instr.getTarget(), labels[4]);
		aliases.addAlias(instr.getArgOperands().get(0), labels[5]);
		
		AliasDelta expected = new AliasDelta();
		expected.addChange(instr.getReceiverOperand(), labels[0]);
		expected.addChange(instr.getTarget(), labels[3]);
		expected.addChange(instr.getTarget(), labels[4]);
		expected.addChange(instr.getArgOperands().get(0), labels[5]);	

		FusionEnvironment<?> env = new FusionEnvironment<TestAliasContext>(aliases, rels, null, types, new InferenceEnvironment(null), variant);		
		Pair<RelationshipDelta, AliasDelta> deltas = runSingleConstraint(env, cons, instr);
		FusionErrorReport error = checkSingleConstraint(env, cons, instr);
		Relationship eff1 = new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[5]});
		Relationship eff2 = new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[3], labels[5]});
		Relationship eff3 = new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[4], labels[5]});

		assertEquals(3, deltas.fst().numberOfChanges());
		assertEquals(SevenPointLattice.FAL, deltas.fst().getValue(eff1));
		assertEquals(SevenPointLattice.TRU_STAR, deltas.fst().getValue(eff2));
		assertEquals(SevenPointLattice.TRU_STAR, deltas.fst().getValue(eff3));
		assertEquals(expected, deltas.snd());

		assertNull(error);			
	}


	@Test
	public void testCombined() throws FusionException {
		RelationshipContext rels = new RelationshipContext(false);

		StubMethodCallInstruction instr = getMCI();
		
		TestAliasContext aliases = new TestAliasContext();
		aliases.addAlias(instr.getReceiverOperand(), labels[0]);
		aliases.addAlias(instr.getTarget(), labels[3]);
		aliases.addAlias(instr.getArgOperands().get(0), labels[1]);
		aliases.addAlias(instr.getArgOperands().get(0), labels[2]);

		AliasDelta expected = new AliasDelta();
		expected.addChange(instr.getReceiverOperand(), labels[0]);
		expected.addChange(instr.getTarget(), labels[3]);
		expected.addChange(instr.getArgOperands().get(0), labels[1]);	
		expected.addChange(instr.getArgOperands().get(0), labels[2]);	

		FusionEnvironment<?> env = new FusionEnvironment<TestAliasContext>(aliases, rels, null, types, new InferenceEnvironment(null), variant);		
		Pair<RelationshipDelta, AliasDelta> deltas = runSingleConstraint(env, cons, instr);
		FusionErrorReport error = checkSingleConstraint(env, cons, instr);
		Relationship eff1 = new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]});
		Relationship eff2 = new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[3], labels[1]});
		Relationship eff3 = new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[2]});
		Relationship eff4 = new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[3], labels[2]});

		assertEquals(4, deltas.fst().numberOfChanges());
		assertEquals(SevenPointLattice.FAL_STAR, deltas.fst().getValue(eff1));
		assertEquals(SevenPointLattice.TRU_STAR, deltas.fst().getValue(eff2));
		assertEquals(SevenPointLattice.FAL_STAR, deltas.fst().getValue(eff3));
		assertEquals(SevenPointLattice.TRU_STAR, deltas.fst().getValue(eff4));
		assertEquals(expected, deltas.snd());

		assertNull(error);			
	}
	
	private StubMethodCallInstruction getMCI() {
		List<StubVariable> vars = new LinkedList<StubVariable>();
		vars.add(new StubVariable("p0",  "Bar"));
		
		StubMethodCallInstruction instr = new StubMethodCallInstruction("methodName", new StubVariable("rVar", "Foo"), vars,
				 new StubMethodBinding(new NamedTypeBinding("Foo"), new NamedTypeBinding[]{new NamedTypeBinding("Bar")}), new StubVariable("tVar", "java.lang.Object"));
		return instr;
	}

}
