package edu.cmu.cs.fusion.relationship;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.util.Pair;
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
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.constraint.operations.MethodInvocationOp;
import edu.cmu.cs.fusion.constraint.predicates.RelationshipPredicate;
import edu.cmu.cs.fusion.test.TestAliasContext;
import edu.cmu.cs.fusion.test.TestUtils;
import edu.cmu.cs.fusion.test.constraint.operations.StubMethodCallInstruction;
import edu.cmu.cs.fusion.test.constraint.operations.StubVariable;
import edu.cmu.cs.fusion.test.lattice.AbstractObjectLabel;

public class TestPartialBound extends ConstraintChecker{
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
		
		op = new MethodInvocationOp("methodName", "Foo", new SpecVar[] {utils.getVar(0)}, new String[] {"Foo"}, "Bar");
		trigger = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		req = new RelationshipPredicate(utils.getRelation(2), new SpecVar[] {utils.getVar(1), utils.getVar(2)});
		effects.add(Effect.createRemoveEffect(utils.getRelation(1), new SpecVar[] {Constraint.RESULT, utils.getVar(0)}));
		effects.add(Effect.createAddEffect(utils.getRelation(1), new SpecVar[] {utils.getVar(0), utils.getVar(0)}));
		
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

	public TestPartialBound() {
		super(null, null);
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
	public void testNoMatches() throws FusionException {
		RelationshipContext rels = new RelationshipContext(false);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[1]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[0]);
		
		TestAliasContext aliases = new TestAliasContext();
		aliases.addAlias(new StubVariable(), labels[0]);
		aliases.addAlias(new StubVariable(), labels[3]);
		aliases.addAlias(new StubVariable(), labels[4]);

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, types, new InferenceEnvironment());		
		RelationshipDelta delta = runPartialBound(env, partialSub, cons, new StubMethodCallInstruction());
		Pair<Variant, List<Substitution>> errs = checkPartialBound(env, partialSub, cons, new StubMethodCallInstruction());

		assertEquals(0, delta.numberOfChanges());	
		assertTrue(errs.fst().noError());			
	}
	
	@Test
	public void testDefOnly() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[5]}), FourPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[6]}), FourPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(2), new ObjectLabel[]{labels[5], labels[6]}), FourPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(2), new ObjectLabel[]{labels[6], labels[6]}), FourPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[1]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[0]);
		
		TestAliasContext aliases = new TestAliasContext();
		aliases.addAlias(new StubVariable(), labels[0]);
		aliases.addAlias(new StubVariable(), labels[3]);
		aliases.addAlias(new StubVariable(), labels[4]);
		aliases.addAlias(new StubVariable(), labels[5]);
		aliases.addAlias(new StubVariable(), labels[6]);

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, types, new InferenceEnvironment());		
		RelationshipDelta delta = runPartialBound(env, partialSub, cons, new StubMethodCallInstruction());
		Pair<Variant, List<Substitution>> errs = checkPartialBound(env, partialSub, cons, new StubMethodCallInstruction());
		Relationship eff1 = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[0]});
		Relationship eff2 = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[0], labels[0]});

		assertEquals(2, delta.numberOfChanges());
		assertEquals(FourPointLattice.FAL, delta.getValue(eff1));
		assertEquals(FourPointLattice.TRU, delta.getValue(eff2));
		
		assertTrue(!errs.fst().isPragmatic());			
	}
	
	@Test
	public void testPartialOnly() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[2]}), FourPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(2), new ObjectLabel[]{labels[2], labels[2]}), FourPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[1]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[0]);
		
		TestAliasContext aliases = new TestAliasContext();
		aliases.addAlias(new StubVariable(), labels[0]);
		aliases.addAlias(new StubVariable(), labels[3]);
		aliases.addAlias(new StubVariable(), labels[4]);
		aliases.addAlias(new StubVariable(), labels[2]);

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, types, new InferenceEnvironment());		
		RelationshipDelta delta = runPartialBound(env, partialSub, cons, new StubMethodCallInstruction());
		Pair<Variant, List<Substitution>> errs = checkPartialBound(env, partialSub, cons, new StubMethodCallInstruction());
		Relationship eff1 = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[0]});
		Relationship eff2 = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[0], labels[0]});

		assertEquals(2, delta.numberOfChanges());
		assertEquals(FourPointLattice.UNK, delta.getValue(eff1));
		assertEquals(FourPointLattice.UNK, delta.getValue(eff2));
		
		assertTrue(!errs.fst().isComplete());			
	}
	
	@Test
	public void testCombined() throws FusionException {
		RelationshipDelta startRels = new RelationshipDelta();
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[2]}), FourPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(2), new ObjectLabel[]{labels[2], labels[2]}), FourPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(0), new ObjectLabel[]{labels[0], labels[6]}), FourPointLattice.TRU);
		startRels.setRelationship(new Relationship(utils.getRelation(2), new ObjectLabel[]{labels[2], labels[2]}), FourPointLattice.TRU);
		RelationshipContext rels = new RelationshipContext(false).applyChangesFromDelta(startRels);

		Substitution partialSub = new Substitution();
		partialSub = partialSub.addSub(utils.getVar(0), labels[0]);
		partialSub = partialSub.addSub(Constraint.RESULT, labels[1]);
		partialSub = partialSub.addSub(Constraint.RECEIVER, labels[0]);
		
		TestAliasContext aliases = new TestAliasContext();
		aliases.addAlias(new StubVariable(), labels[0]);
		aliases.addAlias(new StubVariable(), labels[3]);
		aliases.addAlias(new StubVariable(), labels[6]);
		aliases.addAlias(new StubVariable(), labels[2]);

		FusionEnvironment env = new FusionEnvironment(aliases, rels, null, types, new InferenceEnvironment());		
		RelationshipDelta delta = runPartialBound(env, partialSub, cons, new StubMethodCallInstruction());
		Pair<Variant, List<Substitution>> errs = checkPartialBound(env, partialSub, cons, new StubMethodCallInstruction());
		Relationship eff1 = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[1], labels[0]});
		Relationship eff2 = new Relationship(utils.getRelation(1), new ObjectLabel[]{labels[0], labels[0]});

		assertEquals(2, delta.numberOfChanges());
		assertEquals(FourPointLattice.UNK, delta.getValue(eff1));
		assertEquals(FourPointLattice.UNK, delta.getValue(eff2));
		
		assertTrue(!errs.fst().isComplete());			
	}
}
