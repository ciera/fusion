package edu.cmu.cs.fusion.relationship;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.FusionException;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.Variant;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.Effect;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.constraint.Operation;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.operations.MethodInvocationOp;
import edu.cmu.cs.fusion.constraint.predicates.TruePredicate;
import edu.cmu.cs.fusion.test.StubFusionAnalysis;
import edu.cmu.cs.fusion.test.StubFusionErrorStorage;
import edu.cmu.cs.fusion.test.TestAliasContext;
import edu.cmu.cs.fusion.test.TestRelationshipTransferFunction;
import edu.cmu.cs.fusion.test.TestUtils;
import edu.cmu.cs.fusion.test.constraint.operations.StubMethodBinding;
import edu.cmu.cs.fusion.test.constraint.operations.StubMethodCallInstruction;
import edu.cmu.cs.fusion.test.constraint.operations.StubTypeBinding;
import edu.cmu.cs.fusion.test.constraint.operations.StubVariable;
import edu.cmu.cs.fusion.test.lattice.AbstractObjectLabel;

public class TestSingleConstraint {
	static Constraint cons;
	static TestUtils utils;
	private static ObjectLabel[] labels;
	
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
		utils = new TestUtils();
		
		Operation op;
		Predicate trigger;
		Predicate req;
		List<Effect> effects = new LinkedList<Effect>();
		
		op = new MethodInvocationOp("methodName", "Foo", new SpecVar[] {utils.getVar(0)}, new String[] {"Bar"}, "Foo");
		trigger = new TruePredicate();
		req = new TruePredicate();
		effects.add(Effect.createRemoveEffect(utils.getRelation(0), new SpecVar[] {Constraint.RECEIVER, utils.getVar(0)}));
		effects.add(Effect.createAddEffect(utils.getRelation(0), new SpecVar[] {Constraint.RESULT, utils.getVar(0)}));
		
		cons = new Constraint(op, trigger, req, effects);
		
		labels = new ObjectLabel[7];
		labels[0] = new AbstractObjectLabel("0", "Foo");
		labels[1] = new AbstractObjectLabel("1", "Bar");
		labels[2] = new AbstractObjectLabel("2", "Baz");
		labels[3] = new AbstractObjectLabel("3", "Foo");
		labels[4] = new AbstractObjectLabel("4", "SnaFu");
		labels[5] = new AbstractObjectLabel("5", "Bar");
		labels[6] = new AbstractObjectLabel("6", "Bazar");
		
	}

	
	@Test
	/**
	 * Notice that the only way for there to be absolutely no matches is for the types to not match. If the types match, then
	 * there has to be some aliasing possible because at runtime, those varaibles are going to bind to at least one possible
	 * aliasing configuration!
	 */
	public void testNoMatches() throws FusionException {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		StubFusionErrorStorage errors = new StubFusionErrorStorage();
		RelationshipTransferFunction tf = new TestRelationshipTransferFunction(stubAnalysis, errors);

		RelationshipContext rels = new RelationshipContext(false);
		
		List<StubVariable> vars = new LinkedList<StubVariable>();
		vars.add(new StubVariable());
		
		StubMethodCallInstruction instr = new StubMethodCallInstruction("m", new StubVariable(), vars,
				 new StubMethodBinding(new StubTypeBinding("Foo"), new StubTypeBinding[]{new StubTypeBinding("Foo")}), new StubVariable());
		
		//op has type Foo.methodName(Foo) : Bar
		//instr has type Foo.m(Foo) : Bar
		
		TestAliasContext aliases = new TestAliasContext();
		aliases.addAlias(instr.getReceiverOperand(), labels[5]);
		aliases.addAlias(instr.getTarget(), labels[0]);
		aliases.addAlias(instr.getArgOperands().get(0), labels[3]);

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkSingleConstraint(env, cons, instr);

		assertEquals(0, delta.numberOfChanges());	
		assertEquals(0, errors.getError(Variant.PRAGMATIC_VARIANT, cons).size());		
	}
	
	@Test
	public void testDefOnly() throws FusionException {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		StubFusionErrorStorage errors = new StubFusionErrorStorage();
		RelationshipTransferFunction tf = new TestRelationshipTransferFunction(stubAnalysis, errors);

		RelationshipContext rels = new RelationshipContext(false);

		List<StubVariable> vars = new LinkedList<StubVariable>();
		vars.add(new StubVariable("p0"));
		
		StubMethodCallInstruction instr = new StubMethodCallInstruction("methodName", new StubVariable("rVar"), vars,
				 new StubMethodBinding(new StubTypeBinding("Foo"), new StubTypeBinding[]{new StubTypeBinding("Bar")}), new StubVariable("tVar"));
		
		TestAliasContext aliases = new TestAliasContext();
		aliases.addAlias(instr.getReceiverOperand(), labels[0]);
		aliases.addAlias(instr.getTarget(), labels[3]);
		aliases.addAlias(instr.getArgOperands().get(0), labels[5]);

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkSingleConstraint(env, cons, instr);
		Relationship eff1 = new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[5]});
		Relationship eff2 = new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[3], labels[5]});

		assertEquals(2, delta.numberOfChanges());
		assertEquals(FourPointLattice.FAL, delta.getValue(eff1));
		assertEquals(FourPointLattice.TRU, delta.getValue(eff2));
		
		assertEquals(0, errors.getError(Variant.PRAGMATIC_VARIANT, cons).size());			
	}
	
	@Test
	public void testPartialOnly() throws FusionException {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		StubFusionErrorStorage errors = new StubFusionErrorStorage();
		RelationshipTransferFunction tf = new TestRelationshipTransferFunction(stubAnalysis, errors);

		RelationshipContext rels = new RelationshipContext(false);

		List<StubVariable> vars = new LinkedList<StubVariable>();
		vars.add(new StubVariable("p0"));
		
		StubMethodCallInstruction instr = new StubMethodCallInstruction("methodName", new StubVariable("rVar"), vars,
				 new StubMethodBinding(new StubTypeBinding("Foo"), new StubTypeBinding[]{new StubTypeBinding("Bar")}), new StubVariable("tVar"));
		
		TestAliasContext aliases = new TestAliasContext();
		aliases.addAlias(instr.getReceiverOperand(), labels[0]);
		aliases.addAlias(instr.getTarget(), labels[3]);
		aliases.addAlias(instr.getArgOperands().get(0), labels[2]);

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkSingleConstraint(env, cons, instr);
		Relationship eff1 = new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[2]});
		Relationship eff2 = new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[3], labels[2]});

		assertEquals(2, delta.numberOfChanges());
		assertEquals(FourPointLattice.UNK, delta.getValue(eff1));
		assertEquals(FourPointLattice.UNK, delta.getValue(eff2));
		
		assertEquals(0, errors.getError(Variant.PRAGMATIC_VARIANT, cons).size());			
	}
	
	@Test
	public void testSeveralDef() throws FusionException {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		StubFusionErrorStorage errors = new StubFusionErrorStorage();
		RelationshipTransferFunction tf = new TestRelationshipTransferFunction(stubAnalysis, errors);

		RelationshipContext rels = new RelationshipContext(false);

		List<StubVariable> vars = new LinkedList<StubVariable>();
		vars.add(new StubVariable("p0"));
		
		StubMethodCallInstruction instr = new StubMethodCallInstruction("methodName", new StubVariable("rVar"), vars,
				 new StubMethodBinding(new StubTypeBinding("Foo"), new StubTypeBinding[]{new StubTypeBinding("Bar")}), new StubVariable("tVar"));
		
		TestAliasContext aliases = new TestAliasContext();
		aliases.addAlias(instr.getReceiverOperand(), labels[0]);
		aliases.addAlias(instr.getTarget(), labels[3]);
		aliases.addAlias(instr.getTarget(), labels[4]);
		aliases.addAlias(instr.getArgOperands().get(0), labels[5]);

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkSingleConstraint(env, cons, instr);
		Relationship eff1 = new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[5]});
		Relationship eff2 = new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[3], labels[5]});
		Relationship eff3 = new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[4], labels[5]});

		assertEquals(3, delta.numberOfChanges());
		assertEquals(FourPointLattice.FAL, delta.getValue(eff1));
		assertEquals(FourPointLattice.UNK, delta.getValue(eff2));
		assertEquals(FourPointLattice.UNK, delta.getValue(eff3));
		
		assertEquals(0, errors.getError(Variant.PRAGMATIC_VARIANT, cons).size());			
	}

	@Test
	public void testCombined() throws FusionException {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		StubFusionErrorStorage errors = new StubFusionErrorStorage();
		RelationshipTransferFunction tf = new TestRelationshipTransferFunction(stubAnalysis, errors);

		RelationshipContext rels = new RelationshipContext(false);

		List<StubVariable> vars = new LinkedList<StubVariable>();
		vars.add(new StubVariable("p0"));
		
		StubMethodCallInstruction instr = new StubMethodCallInstruction("methodName", new StubVariable("rVar"), vars,
				 new StubMethodBinding(new StubTypeBinding("Foo"), new StubTypeBinding[]{new StubTypeBinding("Bar")}), new StubVariable("tVar"));
		
		TestAliasContext aliases = new TestAliasContext();
		aliases.addAlias(instr.getReceiverOperand(), labels[0]);
		aliases.addAlias(instr.getTarget(), labels[3]);
		aliases.addAlias(instr.getArgOperands().get(0), labels[1]);
		aliases.addAlias(instr.getArgOperands().get(0), labels[2]);

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkSingleConstraint(env, cons, instr);
		Relationship eff1 = new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]});
		Relationship eff2 = new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[3], labels[1]});
		Relationship eff3 = new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[2]});
		Relationship eff4 = new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[3], labels[2]});

		assertEquals(4, delta.numberOfChanges());
		assertEquals(FourPointLattice.UNK, delta.getValue(eff1));
		assertEquals(FourPointLattice.UNK, delta.getValue(eff2));
		assertEquals(FourPointLattice.UNK, delta.getValue(eff3));
		assertEquals(FourPointLattice.UNK, delta.getValue(eff4));
		
		assertEquals(0, errors.getError(Variant.PRAGMATIC_VARIANT, cons).size());			
	}
}
