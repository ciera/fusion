package edu.cmu.cs.fusion.relationship;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.FusionException;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.Variant;
import edu.cmu.cs.fusion.alias.ObjectLabel;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.Effect;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.constraint.Operation;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.RelEffect;
import edu.cmu.cs.fusion.constraint.SpecDelta;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.constraint.operations.MethodInvocationOp;
import edu.cmu.cs.fusion.constraint.predicates.RelationshipPredicate;
import edu.cmu.cs.fusion.test.TestAliasContext;
import edu.cmu.cs.fusion.test.TestEnvironment;
import edu.cmu.cs.fusion.test.TestUtils;
import edu.cmu.cs.fusion.test.constraint.operations.StubVariable;
import edu.cmu.cs.fusion.test.lattice.AbstractObjectLabel;

/**
 * A test of RelationshipTransferFunction.checkFullyBound().
 * 
 * @author ciera
 *
 */
public class TestFullyBound extends ConstraintChecker {
	static Constraint cons;
	static Constraint possCons;
	static TestUtils utils;
	private static TestAliasContext aliases;
	private static ObjectLabel[] labels;


	@BeforeClass
	static public void setup() {
		utils = new TestUtils();
		
		Operation op;
		Predicate trigger;
		Predicate req;
		List<Effect> effects = new LinkedList<Effect>();
		
		op = new MethodInvocationOp("methodName", "Foo", new SpecVar[] {utils.getVar(0), utils.getVar(1)}, new String[] {"Foo", "Bar"}, "Bar");
		trigger = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		req = new RelationshipPredicate(utils.getRelation(1), new SpecVar[] {utils.getVar(1), utils.getVar(2)});
		effects.add(RelEffect.createRemoveEffect(utils.getRelation(1), new SpecVar[] {Constraint.RESULT, utils.getVar(1)}));
		
		cons = new Constraint(op, trigger, req, effects);
		
		op = new MethodInvocationOp("methodName", "Foo", new SpecVar[] {utils.getVar(0), utils.getVar(1)}, new String[] {"Foo", "Bar"}, "Bar");
		trigger = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		req = new RelationshipPredicate(utils.getRelation(2), new SpecVar[] {utils.getVar(1), utils.getVar(2)});
		effects.add(RelEffect.createRemoveEffect(utils.getRelation(1), new SpecVar[] {Constraint.RESULT, utils.getVar(1)}));
		
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
	
	public TestFullyBound() {
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
			public void sendToXML(Document doc) {
			}
		};
	}

	
	@Test
	public void testNoEffects() throws FusionException {
		Operation op;
		Predicate trigger;
		Predicate req;
		List<Effect> effects = new LinkedList<Effect>();
		
		op = new MethodInvocationOp("methodName", "Foo", new SpecVar[] {utils.getVar(0), utils.getVar(1)}, new String[] {"Foo", "Bar"}, "Bar");
		trigger = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		req = new RelationshipPredicate(utils.getRelation(1), new SpecVar[] {utils.getVar(1), utils.getVar(2)});
		
		//Foo.m(Foo, Bar) : Bar
		//R0(Foo, Bar) -> R1(Bar, Bar)
		//target.(v0, v2) : result
		//R0(v0, v1) -> R1(v1, v2)
		
		Constraint noEffectCons = new Constraint(op, trigger, req, effects);

		
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), SevenPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[6]}), SevenPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);
		
		//R0(0, 1) R1(1, 6)

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[1]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[3]);

		this.variant = Variant.SOUND_VARIANT;
		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, types, new InferenceEnvironment(), variant);		
		Pair<RelationshipDelta, SpecDelta> deltas = runFullyBound(env, partialSub, noEffectCons);
		
		assertEquals(0, deltas.fst().numberOfChanges());
		assertFalse(checkFullyBound(env, partialSub, noEffectCons));
		assertEquals(SpecDelta.createSubstitutionSpecDelta(partialSub), deltas.snd());
	}

	@Test
	public void testSeveralEffectsNotOverlap() throws FusionException {
		Operation op;
		Predicate trigger;
		Predicate req;
		List<Effect> effects = new LinkedList<Effect>();
		
		op = new MethodInvocationOp("methodName", "Foo", new SpecVar[] {utils.getVar(0), utils.getVar(1)}, new String[] {"Foo", "Bar"}, "Bar");
		trigger = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		req = new RelationshipPredicate(utils.getRelation(1), new SpecVar[] {utils.getVar(1), utils.getVar(2)});
		effects.add(RelEffect.createRemoveEffect(utils.getRelation(1), new SpecVar[] {Constraint.RESULT, utils.getVar(1)}));
		effects.add(RelEffect.createAddEffect(utils.getRelation(1), new SpecVar[] {utils.getVar(1), utils.getVar(1)}));

		Constraint severalEffectCons = new Constraint(op, trigger, req, effects);
		
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), SevenPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[6]}), SevenPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[3]);

		this.variant = Variant.PRAGMATIC_VARIANT;
		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, types, new InferenceEnvironment(), variant);		
		Pair<RelationshipDelta, SpecDelta> deltas = runFullyBound(env, partialSub, severalEffectCons);

		Relationship eRel1 = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});
		Relationship eRel2 = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[1]});

		assertEquals(2, deltas.fst().numberOfChanges());
		assertEquals(SevenPointLattice.FAL, deltas.fst().getValue(eRel1));
		assertEquals(SevenPointLattice.TRU, deltas.fst().getValue(eRel2));
		assertEquals(SpecDelta.createSubstitutionSpecDelta(partialSub), deltas.snd());
	
		assertFalse(checkFullyBound(env, partialSub, severalEffectCons));
	}

	@Test
	public void testSeveralEffectsSame() throws FusionException {
		Operation op;
		Predicate trigger;
		Predicate req;
		List<Effect> effects = new LinkedList<Effect>();
		
		op = new MethodInvocationOp("methodName", "Foo", new SpecVar[] {utils.getVar(0), utils.getVar(1)}, new String[] {"Foo", "Bar"}, "Bar");
		trigger = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		req = new RelationshipPredicate(utils.getRelation(1), new SpecVar[] {utils.getVar(1), utils.getVar(2)});
		effects.add(RelEffect.createRemoveEffect(utils.getRelation(1), new SpecVar[] {Constraint.RESULT, utils.getVar(1)}));
		effects.add(RelEffect.createAddEffect(utils.getRelation(1), new SpecVar[] {utils.getVar(1), utils.getVar(1)}));

		Constraint severalEffectCons = new Constraint(op, trigger, req, effects);

		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), SevenPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[1]}), SevenPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[1]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[3]);

		this.variant = Variant.PRAGMATIC_VARIANT;
		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, types, new InferenceEnvironment(), variant);		
		Pair<RelationshipDelta, SpecDelta> deltas = runFullyBound(env, partialSub, severalEffectCons);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[1]});

		assertEquals(1, deltas.fst().numberOfChanges());
		assertEquals(SevenPointLattice.TRU, deltas.fst().getValue(eRel));
		assertEquals(SpecDelta.createSubstitutionSpecDelta(partialSub), deltas.snd());
	
		assertFalse(checkFullyBound(env, partialSub, severalEffectCons));
	}
	
	@Test
	public void testPragmaticTrueTrue() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), SevenPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[6]}), SevenPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[3]);

		this.variant = Variant.PRAGMATIC_VARIANT;
		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, types, new InferenceEnvironment(), variant);		
		Pair<RelationshipDelta, SpecDelta> deltas = runFullyBound(env, partialSub, cons);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		assertEquals(1, deltas.fst().numberOfChanges());
		assertEquals(SevenPointLattice.FAL, deltas.fst().getValue(eRel));
		assertEquals(SpecDelta.createSubstitutionSpecDelta(partialSub), deltas.snd());
		
		assertFalse(checkFullyBound(env, partialSub, cons));
	}

	@Test
	public void testPragmaticTrueErrorU() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), SevenPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		this.variant = Variant.PRAGMATIC_VARIANT;
		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, types, new InferenceEnvironment(), variant);		
		Pair<RelationshipDelta, SpecDelta> deltas = runFullyBound(env, partialSub, cons);

		assertEquals(1, deltas.fst().numberOfChanges());
		assertEquals(SevenPointLattice.FAL, deltas.fst().getValue(eRel));
		assertEquals(SpecDelta.createSubstitutionSpecDelta(partialSub), deltas.snd());
		
		assertTrue(checkFullyBound(env, partialSub, cons));
	}

	@Test
	public void testPragmaticTrueErrorF() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), SevenPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[1]}), SevenPointLattice.FAL);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[6]}), SevenPointLattice.FAL);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		this.variant = Variant.PRAGMATIC_VARIANT;
		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, types, new InferenceEnvironment(), variant);		
		Pair<RelationshipDelta, SpecDelta> deltas = runFullyBound(env, partialSub, cons);

		assertEquals(1, deltas.fst().numberOfChanges());
		assertEquals(SevenPointLattice.FAL, deltas.fst().getValue(eRel));
		assertEquals(SpecDelta.createSubstitutionSpecDelta(partialSub), deltas.snd());
		
		assertTrue(checkFullyBound(env, partialSub, cons));
	}

	@Test
	public void testPragmaticFalse() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{utils.getLabel(0), utils.getLabel(2)}), SevenPointLattice.FAL);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);
		
		this.variant = Variant.PRAGMATIC_VARIANT;
		Pair<RelationshipDelta, SpecDelta> deltas = runFullyBound(new TestEnvironment(rels, variant), utils.getSub(1), cons);

		assertEquals(0, deltas.fst().numberOfChanges());
		assertFalse(checkFullyBound(new TestEnvironment(rels, variant), utils.getSub(1), cons));
		assertEquals(SpecDelta.createBottomSpecDelta(utils.getSub(1)), deltas.snd());
	}

	@Test
	public void testPragmaticUnknown() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), SevenPointLattice.UNK);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		this.variant = Variant.PRAGMATIC_VARIANT;
		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, types, new InferenceEnvironment(), variant);		
		Pair<RelationshipDelta, SpecDelta> deltas = runFullyBound(env, partialSub, cons);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		assertEquals(1, deltas.fst().numberOfChanges());
		assertEquals(SevenPointLattice.FAL_STAR, deltas.fst().getValue(eRel));
		assertEquals(SpecDelta.createBottomSpecDelta(partialSub), deltas.snd());
	
		assertFalse(checkFullyBound(env, partialSub, cons));
	}

	@Test
	public void testSoundTrueTrue() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), SevenPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[6]}), SevenPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		this.variant = Variant.SOUND_VARIANT;
		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, types, new InferenceEnvironment(), variant);		
		Pair<RelationshipDelta, SpecDelta> deltas = runFullyBound(env, partialSub, cons);
		
		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		assertEquals(1, deltas.fst().numberOfChanges());
		assertEquals(SevenPointLattice.FAL, deltas.fst().getValue(eRel));
		assertEquals(SpecDelta.createSubstitutionSpecDelta(partialSub), deltas.snd());

		assertFalse(checkFullyBound(env, partialSub, cons));
	}

	@Test
	public void testSoundTrueErrorU() throws FusionException {	
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), SevenPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		this.variant = Variant.SOUND_VARIANT;
		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, types, new InferenceEnvironment(), variant);		
		Pair<RelationshipDelta, SpecDelta> deltas = runFullyBound(env, partialSub, cons);

		assertEquals(1, deltas.fst().numberOfChanges());
		assertEquals(SevenPointLattice.FAL, deltas.fst().getValue(eRel));
		assertEquals(SpecDelta.createSubstitutionSpecDelta(partialSub), deltas.snd());

		assertTrue(checkFullyBound(env, partialSub, cons));
	}

	@Test
	public void testSoundTrueErrorF() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), SevenPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[1]}), SevenPointLattice.FAL);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[6]}), SevenPointLattice.FAL);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		this.variant = Variant.SOUND_VARIANT;
		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, types, new InferenceEnvironment(), variant);		
		Pair<RelationshipDelta, SpecDelta> deltas = runFullyBound(env, partialSub, cons);

		assertEquals(1, deltas.fst().numberOfChanges());
		assertEquals(SevenPointLattice.FAL, deltas.fst().getValue(eRel));
		assertEquals(SpecDelta.createSubstitutionSpecDelta(partialSub), deltas.snd());
		
		assertTrue(checkFullyBound(env, partialSub, cons));
	}

	@Test
	public void testSoundFalse() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{utils.getLabel(0), utils.getLabel(2)}), SevenPointLattice.FAL);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);
		
		this.variant = Variant.SOUND_VARIANT;
		Pair<RelationshipDelta, SpecDelta> deltas = runFullyBound(new TestEnvironment(rels, variant), utils.getSub(1), cons);

		assertEquals(0, deltas.fst().numberOfChanges());
		assertFalse(checkFullyBound(new TestEnvironment(rels, variant), utils.getSub(1), cons));
		assertEquals(SpecDelta.createBottomSpecDelta(utils.getSub(1)), deltas.snd());
	}

	@Test
	public void testSoundUnknownTrue() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[6]}), SevenPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		this.variant = Variant.SOUND_VARIANT;
		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, types, new InferenceEnvironment(), variant);		
		Pair<RelationshipDelta, SpecDelta> deltas = runFullyBound(env, partialSub, cons);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		assertEquals(1, deltas.fst().numberOfChanges());
		assertEquals(SevenPointLattice.FAL_STAR, deltas.fst().getValue(eRel));
		assertEquals(SpecDelta.createTopSpecDelta(partialSub), deltas.snd());

		assertFalse(checkFullyBound(env, partialSub, cons));
	}

	@Test
	public void testSoundUnknownErrorU() throws FusionException {
		RelationshipContext rels = new RelationshipContext(false);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		this.variant = Variant.SOUND_VARIANT;
		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, types, new InferenceEnvironment(), variant);		
		Pair<RelationshipDelta, SpecDelta> deltas = runFullyBound(env, partialSub, cons);

		assertEquals(1, deltas.fst().numberOfChanges());
		assertEquals(SevenPointLattice.FAL_STAR, deltas.fst().getValue(eRel));
		assertEquals(SpecDelta.createTopSpecDelta(partialSub), deltas.snd());

		assertTrue(checkFullyBound(env, partialSub, cons));
	}

	@Test
	public void testSoundUnknownErrorF() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[1]}), SevenPointLattice.FAL);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[6]}), SevenPointLattice.FAL);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		this.variant = Variant.SOUND_VARIANT;
		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, types, new InferenceEnvironment(), variant);		
		Pair<RelationshipDelta, SpecDelta> deltas = runFullyBound(env, partialSub, cons);

		assertEquals(1, deltas.fst().numberOfChanges());
		assertEquals(SevenPointLattice.FAL_STAR, deltas.fst().getValue(eRel));
		assertEquals(SpecDelta.createTopSpecDelta(partialSub), deltas.snd());

		assertTrue(checkFullyBound(env, partialSub, cons));
	}

	
	@Test
	public void testCompleteTrueTrueDefinite() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), SevenPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(2), new ObjectLabel[]{labels[1], labels[6]}), SevenPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		this.variant = Variant.COMPLETE_VARIANT;
		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, types, new InferenceEnvironment(), variant);		
		Pair<RelationshipDelta, SpecDelta> deltas = runFullyBound(env, partialSub, possCons);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		assertEquals(1, deltas.fst().numberOfChanges());
		assertEquals(SevenPointLattice.FAL, deltas.fst().getValue(eRel));
		assertEquals(SpecDelta.createSubstitutionSpecDelta(partialSub), deltas.snd());

		assertFalse(checkFullyBound(env, partialSub, possCons));
	}

	@Test
	public void testCompleteTrueTruePossible() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), SevenPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(2), new ObjectLabel[]{labels[1], labels[2]}), SevenPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		this.variant = Variant.COMPLETE_VARIANT;
		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, types, new InferenceEnvironment(), variant);		
		Pair<RelationshipDelta, SpecDelta> deltas = runFullyBound(env, partialSub, possCons);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		assertEquals(1, deltas.fst().numberOfChanges());
		assertEquals(SevenPointLattice.FAL, deltas.fst().getValue(eRel));
		assertEquals(SpecDelta.createSubstitutionSpecDelta(partialSub), deltas.snd());

		assertFalse(checkFullyBound(env, partialSub, possCons));
	}

	@Test
	public void testCompleteTrueUnkown() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), SevenPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		this.variant = Variant.COMPLETE_VARIANT;
		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, types, new InferenceEnvironment(), variant);		
		Pair<RelationshipDelta, SpecDelta> deltas = runFullyBound(env, partialSub, possCons);

		assertEquals(1, deltas.fst().numberOfChanges());
		assertEquals(SevenPointLattice.FAL, deltas.fst().getValue(eRel));
		assertEquals(SpecDelta.createSubstitutionSpecDelta(partialSub), deltas.snd());

		assertFalse(checkFullyBound(env, partialSub, possCons));
	}
	
	@Test
	public void testCompleteTrueSomeFail() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), SevenPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(2), new ObjectLabel[]{labels[1], labels[1]}), SevenPointLattice.FAL);
		startRels.setRelationship(new Relationship(utils.getRelation(2), new ObjectLabel[]{labels[1], labels[2]}), SevenPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(2), new ObjectLabel[]{labels[1], labels[6]}), SevenPointLattice.FAL);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		this.variant = Variant.COMPLETE_VARIANT;
		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, types, new InferenceEnvironment(), variant);		
		Pair<RelationshipDelta, SpecDelta> deltas = runFullyBound(env, partialSub, possCons);

		assertEquals(1, deltas.fst().numberOfChanges());
		assertEquals(SevenPointLattice.FAL, deltas.fst().getValue(eRel));
		assertEquals(SpecDelta.createSubstitutionSpecDelta(partialSub), deltas.snd());

		assertFalse(checkFullyBound(env, partialSub, possCons));
	}


	@Test
	public void testCompleteTrueErrorF() throws FusionException {	
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), SevenPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(2), new ObjectLabel[]{labels[1], labels[1]}), SevenPointLattice.FAL);
		startRels.setRelationship(new Relationship(utils.getRelation(2), new ObjectLabel[]{labels[1], labels[2]}), SevenPointLattice.FAL);
		startRels.setRelationship(new Relationship(utils.getRelation(2), new ObjectLabel[]{labels[1], labels[6]}), SevenPointLattice.FAL);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		this.variant = Variant.COMPLETE_VARIANT;
		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, types, new InferenceEnvironment(), variant);		
		Pair<RelationshipDelta, SpecDelta> deltas = runFullyBound(env, partialSub, possCons);

		assertEquals(1, deltas.fst().numberOfChanges());
		assertEquals(SevenPointLattice.FAL, deltas.fst().getValue(eRel));
		assertEquals(SpecDelta.createSubstitutionSpecDelta(partialSub), deltas.snd());

		assertTrue(checkFullyBound(env, partialSub, possCons));
	}

	@Test
	public void testCompleteFalse() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{utils.getLabel(0), utils.getLabel(2)}), SevenPointLattice.FAL);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);
		
		this.variant = Variant.COMPLETE_VARIANT;
		Pair<RelationshipDelta, SpecDelta> deltas = runFullyBound(new TestEnvironment(rels, variant), utils.getSub(1), cons);

		assertEquals(0, deltas.fst().numberOfChanges());
		assertFalse(checkFullyBound(new TestEnvironment(rels, variant), utils.getSub(1), cons));
		assertEquals(SpecDelta.createBottomSpecDelta(utils.getSub(1)), deltas.snd());

	}

	@Test
	public void testCompleteUnknown() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), SevenPointLattice.UNK);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		this.variant = Variant.COMPLETE_VARIANT;
		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, types, new InferenceEnvironment(), variant);		
		Pair<RelationshipDelta, SpecDelta> deltas = runFullyBound(env, partialSub, cons);

		Relationship eRel = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[6], labels[1]});

		assertEquals(1, deltas.fst().numberOfChanges());
		assertEquals(SevenPointLattice.FAL_STAR, deltas.fst().getValue(eRel));
		assertEquals(SpecDelta.createBottomSpecDelta(partialSub), deltas.snd());

		assertFalse(checkFullyBound(env, partialSub, cons));
	}
}
