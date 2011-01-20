package edu.cmu.cs.fusion.relationship;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.LinkedList;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.FusionException;
import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.Variant;
import edu.cmu.cs.fusion.alias.ObjectLabel;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.Effect;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.constraint.Operation;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.constraint.operations.MethodInvocationOp;
import edu.cmu.cs.fusion.constraint.predicates.RelationshipPredicate;
import edu.cmu.cs.fusion.constraint.predicates.TruePredicate;
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
public class TestFullyBoundRestrictions extends ConstraintChecker {
	static Constraint cons;
	static TestUtils utils;
	private static TestAliasContext aliases;
	private static ObjectLabel[] labels;


	@BeforeClass
	static public void setup() {
		utils = new TestUtils();
		
		Operation op;
		Predicate trigger;
		Predicate rst;
		
		op = new MethodInvocationOp("methodName", "Foo", new SpecVar[] {utils.getVar(0), utils.getVar(1)}, new String[] {"Foo", "Bar"}, "Bar");
		trigger = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		rst = new RelationshipPredicate(utils.getRelation(1), new SpecVar[] {utils.getVar(1), utils.getVar(2)});
		cons = new Constraint("", op, trigger, rst, new TruePredicate(), new LinkedList<Effect>());
		
		
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

		aliases = new TestAliasContext();
		aliases.addAlias(vars[0], labels[0]);
		aliases.addAlias(vars[0], labels[2]);
		aliases.addAlias(vars[0], labels[6]);
		aliases.addAlias(vars[1], labels[1]);
	}
	
	public TestFullyBoundRestrictions() {
		super(null, null, Variant.SOUND_VARIANT, null);
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
	public void testTrueTrue() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), SevenPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[6]}), SevenPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[3]);

		FusionEnvironment<?> env = new FusionEnvironment<TestAliasContext>(aliases, rels, null, types, new InferenceEnvironment(null), variant);		
		Pair<RelationshipDelta, Substitution> deltas = runFullyBound(env, partialSub, cons);

		assertEquals(partialSub, deltas.snd());
	}
	
	@Test
	public void testTrueNoSubs() throws FusionException {
		Predicate rst = new RelationshipPredicate(new Relation("D", new String[] {"Bar", "NewType"}), new SpecVar[] {utils.getVar(1), utils.getVar(2)});
		Constraint noSubCons = new Constraint("", cons.getOp(), cons.getTrigger(), rst, new TruePredicate(), new LinkedList<Effect>());
	
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), SevenPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[3]);

		FusionEnvironment<?> env = new FusionEnvironment<TestAliasContext>(aliases, rels, null, types, new InferenceEnvironment(null), variant);		
		Pair<RelationshipDelta, Substitution> deltas = runFullyBound(env, partialSub, noSubCons);

		assertNull(deltas.snd());
	}

	@Test
	public void testTrueAll() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), SevenPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[1]}), SevenPointLattice.FAL);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[6]}), SevenPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[2]}), SevenPointLattice.UNK);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		FusionEnvironment<?> env = new FusionEnvironment<TestAliasContext>(aliases, rels, null, types, new InferenceEnvironment(null), variant);		
		Pair<RelationshipDelta, Substitution> deltas = runFullyBound(env, partialSub, cons);
		assertEquals(partialSub, deltas.snd());
	}
	
	@Test
	public void testTrueSomeFalse() throws FusionException {
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

		FusionEnvironment<?> env = new FusionEnvironment<TestAliasContext>(aliases, rels, null, types, new InferenceEnvironment(null), variant);		
		Pair<RelationshipDelta, Substitution> deltas = runFullyBound(env, partialSub, cons);
		assertEquals(partialSub, deltas.snd());
	}


	@Test
	public void testTrueFalse() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), SevenPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[1]}), SevenPointLattice.FAL);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[6]}), SevenPointLattice.FAL);
		startRels.setRelationship(new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[2]}), SevenPointLattice.FAL);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		FusionEnvironment<?> env = new FusionEnvironment<TestAliasContext>(aliases, rels, null, types, new InferenceEnvironment(null), variant);		
		Pair<RelationshipDelta, Substitution> deltas = runFullyBound(env, partialSub, cons);
		assertNull(deltas.snd());
	}
	

	@Test
	public void testTrueUnknown() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), SevenPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		FusionEnvironment<?> env = new FusionEnvironment<TestAliasContext>(aliases, rels, null, types, new InferenceEnvironment(null), variant);		
		Pair<RelationshipDelta, Substitution> deltas = runFullyBound(env, partialSub, cons);
		assertEquals(partialSub, deltas.snd());
	}

	@Test
	public void testTrueUnknownPragmatic() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), SevenPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		Variant vOld = this.variant;
		this.variant = Variant.PRAGMATIC_VARIANT;
		FusionEnvironment<?> env = new FusionEnvironment<TestAliasContext>(aliases, rels, null, types, new InferenceEnvironment(null), variant);		
		Pair<RelationshipDelta, Substitution> deltas = runFullyBound(env, partialSub, cons);
		this.variant = vOld;
		assertNull(deltas.snd());
	}

	@Test
	public void testUnknown() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[1]}), SevenPointLattice.UNK);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(utils.getVar(1), labels[1]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[6]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[1]);

		FusionEnvironment<?> env = new FusionEnvironment<TestAliasContext>(aliases, rels, null, types, new InferenceEnvironment(null), variant);		
		Pair<RelationshipDelta, Substitution> deltas = runFullyBound(env, partialSub, cons);

		assertEquals(partialSub, deltas.snd());
	}

	@Test
	public void testFalse() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{utils.getLabel(0), utils.getLabel(2)}), SevenPointLattice.FAL);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);
		
		Pair<RelationshipDelta, Substitution> deltas = runFullyBound(new TestEnvironment(rels, variant), utils.getSub(1), cons);

		assertEquals(utils.getSub(1), deltas.snd());
	}
}
