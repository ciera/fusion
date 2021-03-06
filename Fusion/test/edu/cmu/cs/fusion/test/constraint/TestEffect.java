package edu.cmu.cs.fusion.test.constraint;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.Variant;
import edu.cmu.cs.fusion.alias.ObjectLabel;
import edu.cmu.cs.fusion.constraint.Effect;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.RelEffect;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.relationship.SevenPointLattice;
import edu.cmu.cs.fusion.relationship.RelationshipDelta;
import edu.cmu.cs.fusion.test.TestEnvironment;
import edu.cmu.cs.fusion.test.TestUtils;

public class TestEffect {
	static TestUtils utils;
	
	@BeforeClass
	static public void setup() {
		utils = new TestUtils();
	}

	@Test
	public void testFreeVarsNormal() {
		Effect eff = RelEffect.createAddEffect(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		String[] types = utils.getRelation(0).getFullyQualifiedTypes();
		FreeVars fv = eff.getFreeVariables();
		
		assertEquals(types[0], fv.getType(utils.getVar(0)));
		assertEquals(types[1], fv.getType(utils.getVar(1)));

		assertEquals(2, fv.size());
	}
	
	@Test
	public void testFreeVarsTest() {
		Effect eff = RelEffect.createNegatedTestEffect(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)}, utils.getVar(2));
		String[] types = utils.getRelation(0).getFullyQualifiedTypes();
		FreeVars fv = eff.getFreeVariables();
		
		assertEquals(types[0], fv.getType(utils.getVar(0)));
		assertEquals(types[1], fv.getType(utils.getVar(1)));
		assertEquals(FreeVars.BOOL_TYPE, fv.getType(utils.getVar(2)));

		assertEquals(3, fv.size());
	}
	
	@Test
	public void testMakeEffectsAdd() {
		Effect eff = RelEffect.createAddEffect(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		TestEnvironment env = new TestEnvironment(utils.getContext(0), Variant.SOUND_VARIANT);
		
		RelationshipDelta delta = eff.makeEffects(env, utils.getSub(0));
		ObjectLabel[] labels = new ObjectLabel[] {utils.getSub(0).getSub(utils.getVar(0)), utils.getSub(0).getSub(utils.getVar(1))};
		SevenPointLattice val = delta.getValue(new Relationship(utils.getRelation(0), labels));
		assertEquals(SevenPointLattice.TRU, val);
		
		assertEquals(1, delta.numberOfChanges());
	}
	
	@Test
	public void testMakeEffectsRemove() {
		Effect eff = RelEffect.createRemoveEffect(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		TestEnvironment env = new TestEnvironment(utils.getContext(0), Variant.SOUND_VARIANT);
		
		RelationshipDelta delta = eff.makeEffects(env, utils.getSub(0));
		ObjectLabel[] labels = new ObjectLabel[] {utils.getSub(0).getSub(utils.getVar(0)), utils.getSub(0).getSub(utils.getVar(1))};
		SevenPointLattice val = delta.getValue(new Relationship(utils.getRelation(0), labels));
		assertEquals(SevenPointLattice.FAL, val);
		
		assertEquals(1, delta.numberOfChanges());
	}
	
	@Test
	public void testMakeEffectsTestTrue() {
		Effect eff = RelEffect.createTestEffect(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)}, utils.getVar(2));
		Map<ObjectLabel, ThreeValue> map = new HashMap<ObjectLabel, ThreeValue>();
		TestEnvironment env = new TestEnvironment(utils.getContext(0), map, Variant.SOUND_VARIANT);
		
		map.put(utils.getSub(0).getSub(utils.getVar(2)), ThreeValue.TRUE);
		
		RelationshipDelta delta = eff.makeEffects(env, utils.getSub(0));
		ObjectLabel[] labels = new ObjectLabel[] {utils.getSub(0).getSub(utils.getVar(0)), utils.getSub(0).getSub(utils.getVar(1))};
		SevenPointLattice val = delta.getValue(new Relationship(utils.getRelation(0), labels));
		assertEquals(SevenPointLattice.TRU, val);
		
		assertEquals(1, delta.numberOfChanges());
	}
	
	@Test
	public void testMakeEffectsTestFalse() {
		Effect eff = RelEffect.createTestEffect(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)}, utils.getVar(2));
		Map<ObjectLabel, ThreeValue> map = new HashMap<ObjectLabel, ThreeValue>();
		TestEnvironment env = new TestEnvironment(utils.getContext(0), map, Variant.SOUND_VARIANT);
		
		map.put(utils.getSub(0).getSub(utils.getVar(2)), ThreeValue.FALSE);
		
		RelationshipDelta delta = eff.makeEffects(env, utils.getSub(0));
		ObjectLabel[] labels = new ObjectLabel[] {utils.getSub(0).getSub(utils.getVar(0)), utils.getSub(0).getSub(utils.getVar(1))};
		SevenPointLattice val = delta.getValue(new Relationship(utils.getRelation(0), labels));
		assertEquals(SevenPointLattice.FAL, val);
		
		assertEquals(1, delta.numberOfChanges());
	}
	
	@Test
	public void testMakeEffectsTestUnk() {
		Effect eff = RelEffect.createTestEffect(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)}, utils.getVar(2));
		Map<ObjectLabel, ThreeValue> map = new HashMap<ObjectLabel, ThreeValue>();
		TestEnvironment env = new TestEnvironment(utils.getContext(0), map, Variant.SOUND_VARIANT);
		
		map.put(utils.getSub(0).getSub(utils.getVar(2)), ThreeValue.UNKNOWN);
		
		RelationshipDelta delta = eff.makeEffects(env, utils.getSub(0));
		ObjectLabel[] labels = new ObjectLabel[] {utils.getSub(0).getSub(utils.getVar(0)), utils.getSub(0).getSub(utils.getVar(1))};
		SevenPointLattice val = delta.getValue(new Relationship(utils.getRelation(0), labels));
		assertEquals(SevenPointLattice.UNK, val);
		
		assertEquals(1, delta.numberOfChanges());
	}

	@Test
	public void testMakeEffectsTestNegFalse() {
		Effect eff = RelEffect.createNegatedTestEffect(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)}, utils.getVar(2));
		Map<ObjectLabel, ThreeValue> map = new HashMap<ObjectLabel, ThreeValue>();
		TestEnvironment env = new TestEnvironment(utils.getContext(0), map, Variant.SOUND_VARIANT);
		
		map.put(utils.getSub(0).getSub(utils.getVar(2)), ThreeValue.TRUE);
		
		RelationshipDelta delta = eff.makeEffects(env, utils.getSub(0));
		ObjectLabel[] labels = new ObjectLabel[] {utils.getSub(0).getSub(utils.getVar(0)), utils.getSub(0).getSub(utils.getVar(1))};
		SevenPointLattice val = delta.getValue(new Relationship(utils.getRelation(0), labels));
		assertEquals(SevenPointLattice.FAL, val);
		
		assertEquals(1, delta.numberOfChanges());
	}

	@Test
	public void testMakeEffectsTestNegTrue() {
		Effect eff = RelEffect.createNegatedTestEffect(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)}, utils.getVar(2));
		Map<ObjectLabel, ThreeValue> map = new HashMap<ObjectLabel, ThreeValue>();
		TestEnvironment env = new TestEnvironment(utils.getContext(0), map, Variant.SOUND_VARIANT);
		
		map.put(utils.getSub(0).getSub(utils.getVar(2)), ThreeValue.FALSE);
		
		RelationshipDelta delta = eff.makeEffects(env, utils.getSub(0));
		ObjectLabel[] labels = new ObjectLabel[] {utils.getSub(0).getSub(utils.getVar(0)), utils.getSub(0).getSub(utils.getVar(1))};
		SevenPointLattice val = delta.getValue(new Relationship(utils.getRelation(0), labels));
		assertEquals(SevenPointLattice.TRU, val);
		
		assertEquals(1, delta.numberOfChanges());
	}

	@Test
	public void testMakeEffectsTestNegUnk() {
		Effect eff = RelEffect.createNegatedTestEffect(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)}, utils.getVar(2));
		Map<ObjectLabel, ThreeValue> map = new HashMap<ObjectLabel, ThreeValue>();
		TestEnvironment env = new TestEnvironment(utils.getContext(0), map, Variant.SOUND_VARIANT);
		
		map.put(utils.getSub(0).getSub(utils.getVar(2)), ThreeValue.UNKNOWN);
		
		RelationshipDelta delta = eff.makeEffects(env, utils.getSub(0));
		ObjectLabel[] labels = new ObjectLabel[] {utils.getSub(0).getSub(utils.getVar(0)), utils.getSub(0).getSub(utils.getVar(1))};
		SevenPointLattice val = delta.getValue(new Relationship(utils.getRelation(0), labels));
		assertEquals(SevenPointLattice.UNK, val);
		
		assertEquals(1, delta.numberOfChanges());
	
	}
}
