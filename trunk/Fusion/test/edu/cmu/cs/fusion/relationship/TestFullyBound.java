package edu.cmu.cs.fusion.relationship;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.TypeHierarchy;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.Effect;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.constraint.Operation;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.constraint.operations.MethodInvocationOp;
import edu.cmu.cs.fusion.constraint.predicates.RelationshipPredicate;
import edu.cmu.cs.fusion.relationship.RelationshipTransferFunction.Variant;
import edu.cmu.cs.fusion.test.StubFusionAnalysis;
import edu.cmu.cs.fusion.test.TestAliasContext;
import edu.cmu.cs.fusion.test.TestEnvironment;
import edu.cmu.cs.fusion.test.TestUtils;
import edu.cmu.cs.fusion.test.constraint.operations.StubMethodCallInstruction;
import edu.cmu.cs.fusion.test.constraint.operations.StubVariable;
import edu.cmu.cs.fusion.test.lattice.AbstractObjectLabel;

/**
 * A test of RelationshipTransferFunction.checkFullyBound().
 * 
 * @author ciera
 *
 */
public class TestFullyBound {
	static Constraint cons;
	static Constraint possCons;
	static TestUtils utils;
	private static TestAliasContext aliases;
	private static ObjectLabel[] labels;

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
		utils = new TestUtils();
		
		Operation op;
		Predicate trigger;
		Predicate req;
		List<Effect> effects = new LinkedList<Effect>();
		
		op = new MethodInvocationOp("methodName", "Foo", new SpecVar[] {utils.getVar(0), utils.getVar(2)}, new String[] {"Foo", "Foo"}, "Bar");
		trigger = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		req = new RelationshipPredicate(utils.getRelation(1), new SpecVar[] {utils.getVar(1), utils.getVar(2)});
		effects.add(Effect.createRemoveEffect(utils.getRelation(1), new SpecVar[] {Constraint.RESULT, utils.getVar(1)}));
		
		cons = new Constraint(op, trigger, req, effects);
		
		op = new MethodInvocationOp("methodName", "Foo", new SpecVar[] {utils.getVar(0), utils.getVar(2)}, new String[] {"Foo", "Foo"}, "Bar");
		trigger = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		req = new RelationshipPredicate(utils.getRelation(2), new SpecVar[] {utils.getVar(1), utils.getVar(2)});
		effects.add(Effect.createRemoveEffect(utils.getRelation(1), new SpecVar[] {Constraint.RESULT, utils.getVar(1)}));
		
		possCons = new Constraint(op, trigger, req, effects);
		
		aliases = new TestAliasContext();
		
		labels = new ObjectLabel[7];
		labels[0] = new AbstractObjectLabel("0", "Foo");
		labels[1] = new AbstractObjectLabel("1", "Bar");
		labels[2] = new AbstractObjectLabel("2", "Baz");
		labels[3] = new AbstractObjectLabel("3", "Foo");
		labels[4] = new AbstractObjectLabel("4", "SnaFu");
		labels[5] = new AbstractObjectLabel("5", "Bar");
		labels[6] = new AbstractObjectLabel("6", "Bazar");
		
		StubVariable[] vars = new StubVariable[5];
		vars[0] = new StubVariable();
		vars[1] = new StubVariable();
		vars[2] = new StubVariable();
		vars[3] = new StubVariable();
		vars[4] = new StubVariable();

		aliases.addAlias(vars[0], labels[0]);
		aliases.addAlias(vars[0], labels[2]);
		aliases.addAlias(vars[0], labels[6]);
		aliases.addAlias(vars[1], labels[1]);
	}
	
	@Test
	public void testNoEffects() {
		Operation op;
		Predicate trigger;
		Predicate req;
		List<Effect> effects = new LinkedList<Effect>();
		
		op = new MethodInvocationOp("methodName", "Foo", new SpecVar[] {utils.getVar(0), utils.getVar(2)}, new String[] {"Foo", "Foo"}, "Bar");
		trigger = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		req = new RelationshipPredicate(utils.getRelation(1), new SpecVar[] {utils.getVar(1), utils.getVar(2)});
		
		Constraint noEffectCons = new Constraint(op, trigger, req, effects);

		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		RelationshipTransferFunction tf = new RelationshipTransferFunction(stubAnalysis, null, null, Variant.PRAGMATIC);
		
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), FourPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[6]}), FourPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkFullyBound(env, partialSub, noEffectCons, new StubMethodCallInstruction());
		
		assertEquals(0, delta.numberOfChanges());
		assertEquals(0, stubAnalysis.getError(Variant.PRAGMATIC, noEffectCons).size());
	}

	@Test
	public void testSeveralEffectsNotOverlap() {
		Operation op;
		Predicate trigger;
		Predicate req;
		List<Effect> effects = new LinkedList<Effect>();
		
		op = new MethodInvocationOp("methodName", "Foo", new SpecVar[] {utils.getVar(0), utils.getVar(2)}, new String[] {"Foo", "Foo"}, "Bar");
		trigger = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		req = new RelationshipPredicate(utils.getRelation(1), new SpecVar[] {utils.getVar(1), utils.getVar(2)});
		effects.add(Effect.createRemoveEffect(utils.getRelation(1), new SpecVar[] {Constraint.RESULT, utils.getVar(1)}));
		effects.add(Effect.createAddEffect(utils.getRelation(1), new SpecVar[] {utils.getVar(1), utils.getVar(1)}));

		Constraint severalEffectCons = new Constraint(op, trigger, req, effects);

		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		RelationshipTransferFunction tf = new RelationshipTransferFunction(stubAnalysis, null, null,Variant.PRAGMATIC);
		
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), FourPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[6]}), FourPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkFullyBound(env, partialSub, severalEffectCons, new StubMethodCallInstruction());
		
		Relationship eRel1 = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});
		Relationship eRel2 = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[1]});

		assertEquals(2, delta.numberOfChanges());
		assertEquals(FourPointLattice.FAL, delta.getValue(eRel1));
		assertEquals(FourPointLattice.TRU, delta.getValue(eRel2));
	
		assertEquals(0, stubAnalysis.getError(Variant.PRAGMATIC, severalEffectCons).size());		
	}

	@Test
	public void testSeveralEffectsSame() {
		Operation op;
		Predicate trigger;
		Predicate req;
		List<Effect> effects = new LinkedList<Effect>();
		
		op = new MethodInvocationOp("methodName", "Foo", new SpecVar[] {utils.getVar(0), utils.getVar(2)}, new String[] {"Foo", "Foo"}, "Bar");
		trigger = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		req = new RelationshipPredicate(utils.getRelation(1), new SpecVar[] {utils.getVar(1), utils.getVar(2)});
		effects.add(Effect.createRemoveEffect(utils.getRelation(1), new SpecVar[] {Constraint.RESULT, utils.getVar(1)}));
		effects.add(Effect.createAddEffect(utils.getRelation(1), new SpecVar[] {utils.getVar(1), utils.getVar(1)}));

		Constraint severalEffectCons = new Constraint(op, trigger, req, effects);

		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		RelationshipTransferFunction tf = new RelationshipTransferFunction(stubAnalysis, null, null,Variant.PRAGMATIC);
		
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), FourPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[1]}), FourPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[1]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkFullyBound(env, partialSub, severalEffectCons, new StubMethodCallInstruction());
		
		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[1]});

		assertEquals(1, delta.numberOfChanges());
		assertEquals(FourPointLattice.UNK, delta.getValue(eRel));
	
		assertEquals(0, stubAnalysis.getError(Variant.PRAGMATIC, cons).size());		
	}
	
	@Test
	public void testPragmaticTrueTrue() {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		RelationshipTransferFunction tf = new RelationshipTransferFunction(stubAnalysis, null, null, Variant.PRAGMATIC);
		
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), FourPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[6]}), FourPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkFullyBound(env, partialSub, cons, new StubMethodCallInstruction());
		
		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		assertEquals(1, delta.numberOfChanges());
		assertEquals(FourPointLattice.FAL, delta.getValue(eRel));
		
		assertEquals(0, stubAnalysis.getError(Variant.PRAGMATIC, cons).size());
	}

	@Test
	public void testPragmaticTrueErrorU() {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		RelationshipTransferFunction tf = new RelationshipTransferFunction(stubAnalysis, null, null, Variant.PRAGMATIC);
		
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), FourPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkFullyBound(env, partialSub, cons, new StubMethodCallInstruction());
		
		assertEquals(1, delta.numberOfChanges());
		assertEquals(FourPointLattice.FAL, delta.getValue(eRel));
		
		assertEquals(1, stubAnalysis.getError(Variant.PRAGMATIC, cons).size());
	}

	@Test
	public void testPragmaticTrueErrorF() {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		RelationshipTransferFunction tf = new RelationshipTransferFunction(stubAnalysis, null, null, Variant.PRAGMATIC);
		
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), FourPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[1]}), FourPointLattice.FAL);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[6]}), FourPointLattice.FAL);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkFullyBound(env, partialSub, cons, new StubMethodCallInstruction());
		
		assertEquals(1, delta.numberOfChanges());
		assertEquals(FourPointLattice.FAL, delta.getValue(eRel));
		
		assertEquals(1, stubAnalysis.getError(Variant.PRAGMATIC, cons).size());
	}

	@Test
	public void testPragmaticFalse() {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		RelationshipTransferFunction tf = new RelationshipTransferFunction(stubAnalysis, null, null, Variant.PRAGMATIC);
		
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{utils.getLabel(0), utils.getLabel(2)}), FourPointLattice.FAL);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);
		
		RelationshipDelta delta = tf.checkFullyBound(new TestEnvironment(rels), utils.getSub(1), cons, new StubMethodCallInstruction());
		
		assertEquals(0, delta.numberOfChanges());
		assertEquals(0, stubAnalysis.getError(Variant.PRAGMATIC, cons).size());
	}

	@Test
	public void testPragmaticUnknown() {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		RelationshipTransferFunction tf = new RelationshipTransferFunction(stubAnalysis, null, null, Variant.PRAGMATIC);
		
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), FourPointLattice.UNK);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkFullyBound(env, partialSub, cons, new StubMethodCallInstruction());
		
		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		assertEquals(1, delta.numberOfChanges());
		assertEquals(FourPointLattice.UNK, delta.getValue(eRel));
		
		assertEquals(0, stubAnalysis.getError(Variant.PRAGMATIC, cons).size());
	}

	@Test
	public void testSoundTrueTrue() {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		RelationshipTransferFunction tf = new RelationshipTransferFunction(stubAnalysis, null, null, Variant.SOUND);
		
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), FourPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[6]}), FourPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkFullyBound(env, partialSub, cons, new StubMethodCallInstruction());
		
		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		assertEquals(1, delta.numberOfChanges());
		assertEquals(FourPointLattice.FAL, delta.getValue(eRel));
		
		assertEquals(0, stubAnalysis.getError(Variant.SOUND, cons).size());
	}

	@Test
	public void testSoundTrueErrorU() {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		RelationshipTransferFunction tf = new RelationshipTransferFunction(stubAnalysis, null, null, Variant.SOUND);
		
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), FourPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkFullyBound(env, partialSub, cons, new StubMethodCallInstruction());
		
		assertEquals(1, delta.numberOfChanges());
		assertEquals(FourPointLattice.FAL, delta.getValue(eRel));
		
		assertEquals(1, stubAnalysis.getError(Variant.SOUND, cons).size());
	}

	@Test
	public void testSoundTrueErrorF() {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		RelationshipTransferFunction tf = new RelationshipTransferFunction(stubAnalysis, null, null, Variant.SOUND);
		
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), FourPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[1]}), FourPointLattice.FAL);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[6]}), FourPointLattice.FAL);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkFullyBound(env, partialSub, cons, new StubMethodCallInstruction());
		
		assertEquals(1, delta.numberOfChanges());
		assertEquals(FourPointLattice.FAL, delta.getValue(eRel));
		
		assertEquals(1, stubAnalysis.getError(Variant.SOUND, cons).size());
	}

	@Test
	public void testSoundFalse() {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		RelationshipTransferFunction tf = new RelationshipTransferFunction(stubAnalysis, null, null, Variant.SOUND);
		
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{utils.getLabel(0), utils.getLabel(2)}), FourPointLattice.FAL);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);
		
		RelationshipDelta delta = tf.checkFullyBound(new TestEnvironment(rels), utils.getSub(1), cons, new StubMethodCallInstruction());
		
		assertEquals(0, delta.numberOfChanges());
		assertEquals(0, stubAnalysis.getError(Variant.SOUND, cons).size());		
	}

	@Test
	public void testSoundUnknownTrue() {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		RelationshipTransferFunction tf = new RelationshipTransferFunction(stubAnalysis, null, null, Variant.SOUND);
		
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[6]}), FourPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkFullyBound(env, partialSub, cons, new StubMethodCallInstruction());
		
		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		assertEquals(1, delta.numberOfChanges());
		assertEquals(FourPointLattice.UNK, delta.getValue(eRel));
		
		assertEquals(0, stubAnalysis.getError(Variant.SOUND, cons).size());
	}

	@Test
	public void testSoundUnknownErrorU() {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		RelationshipTransferFunction tf = new RelationshipTransferFunction(stubAnalysis, null, null, Variant.SOUND);
		
		RelationshipContext rels = new RelationshipContext(false);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkFullyBound(env, partialSub, cons, new StubMethodCallInstruction());
		
		assertEquals(1, delta.numberOfChanges());
		assertEquals(FourPointLattice.UNK, delta.getValue(eRel));
		
		assertEquals(1, stubAnalysis.getError(Variant.SOUND, cons).size());
	}

	@Test
	public void testSoundUnknownErrorF() {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		RelationshipTransferFunction tf = new RelationshipTransferFunction(stubAnalysis, null, null, Variant.SOUND);
		
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[1]}), FourPointLattice.FAL);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[6]}), FourPointLattice.FAL);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkFullyBound(env, partialSub, cons, new StubMethodCallInstruction());
		
		assertEquals(1, delta.numberOfChanges());
		assertEquals(FourPointLattice.UNK, delta.getValue(eRel));
		
		assertEquals(1, stubAnalysis.getError(Variant.SOUND, cons).size());
	}

	
	@Test
	public void testCompleteTrueTrueDefinite() {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		RelationshipTransferFunction tf = new RelationshipTransferFunction(stubAnalysis, null, null, Variant.COMPLETE);
		
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), FourPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(2), new ObjectLabel[]{labels[1], labels[6]}), FourPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkFullyBound(env, partialSub, possCons, new StubMethodCallInstruction());
		
		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		assertEquals(1, delta.numberOfChanges());
		assertEquals(FourPointLattice.FAL, delta.getValue(eRel));
		
		assertEquals(0, stubAnalysis.getError(Variant.COMPLETE, possCons).size());
	}

	@Test
	public void testCompleteTrueTruePossible() {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		RelationshipTransferFunction tf = new RelationshipTransferFunction(stubAnalysis, null, null, Variant.COMPLETE);
		
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), FourPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(2), new ObjectLabel[]{labels[1], labels[2]}), FourPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkFullyBound(env, partialSub, possCons, new StubMethodCallInstruction());
		
		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		assertEquals(1, delta.numberOfChanges());
		assertEquals(FourPointLattice.FAL, delta.getValue(eRel));
		
		assertEquals(0, stubAnalysis.getError(Variant.COMPLETE, possCons).size());
	}

	@Test
	public void testCompleteTrueUnkown() {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		RelationshipTransferFunction tf = new RelationshipTransferFunction(stubAnalysis, null, null, Variant.COMPLETE);
		
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), FourPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkFullyBound(env, partialSub, possCons, new StubMethodCallInstruction());
		
		assertEquals(1, delta.numberOfChanges());
		assertEquals(FourPointLattice.FAL, delta.getValue(eRel));
		
		assertEquals(0, stubAnalysis.getError(Variant.COMPLETE, possCons).size());
	}
	
	@Test
	public void testCompleteTrueSomeFail() {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		RelationshipTransferFunction tf = new RelationshipTransferFunction(stubAnalysis, null, null, Variant.COMPLETE);
		
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), FourPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(2), new ObjectLabel[]{labels[1], labels[1]}), FourPointLattice.FAL);
		startRels.setRelationship(new Relationship(utils.getRelation(2), new ObjectLabel[]{labels[1], labels[2]}), FourPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(2), new ObjectLabel[]{labels[1], labels[6]}), FourPointLattice.FAL);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkFullyBound(env, partialSub, possCons, new StubMethodCallInstruction());
		
		assertEquals(1, delta.numberOfChanges());
		assertEquals(FourPointLattice.FAL, delta.getValue(eRel));
		
		assertEquals(0, stubAnalysis.getError(Variant.COMPLETE, possCons).size());
	}


	@Test
	public void testCompleteTrueErrorF() {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		RelationshipTransferFunction tf = new RelationshipTransferFunction(stubAnalysis, null, null, Variant.COMPLETE);
		
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), FourPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(2), new ObjectLabel[]{labels[1], labels[1]}), FourPointLattice.FAL);
		startRels.setRelationship(new Relationship(utils.getRelation(2), new ObjectLabel[]{labels[1], labels[2]}), FourPointLattice.FAL);
		startRels.setRelationship(new Relationship(utils.getRelation(2), new ObjectLabel[]{labels[1], labels[6]}), FourPointLattice.FAL);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkFullyBound(env, partialSub, possCons, new StubMethodCallInstruction());
		
		assertEquals(1, delta.numberOfChanges());
		assertEquals(FourPointLattice.FAL, delta.getValue(eRel));
		
		assertEquals(1, stubAnalysis.getError(Variant.COMPLETE, possCons).size());
	}

	@Test
	public void testCompleteFalse() {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		RelationshipTransferFunction tf = new RelationshipTransferFunction(stubAnalysis, null, null, Variant.COMPLETE);
		
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{utils.getLabel(0), utils.getLabel(2)}), FourPointLattice.FAL);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);
		
		RelationshipDelta delta = tf.checkFullyBound(new TestEnvironment(rels), utils.getSub(1), cons, new StubMethodCallInstruction());
		
		assertEquals(0, delta.numberOfChanges());
		assertEquals(0, stubAnalysis.getError(Variant.COMPLETE, cons).size());		
	}

	@Test
	public void testCompleteUnknown() {
		StubFusionAnalysis stubAnalysis = new StubFusionAnalysis();
		RelationshipTransferFunction tf = new RelationshipTransferFunction(stubAnalysis, null, null, Variant.COMPLETE);
		
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), FourPointLattice.UNK);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, testH, new InferenceEnvironment());		
		RelationshipDelta delta = tf.checkFullyBound(env, partialSub, cons, new StubMethodCallInstruction());
		
		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		assertEquals(1, delta.numberOfChanges());
		assertEquals(FourPointLattice.UNK, delta.getValue(eRel));
		
		assertEquals(0, stubAnalysis.getError(Variant.COMPLETE, cons).size());		
	}
}
