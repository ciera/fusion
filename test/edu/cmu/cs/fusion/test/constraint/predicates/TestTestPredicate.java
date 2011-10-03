package edu.cmu.cs.fusion.test.constraint.predicates;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.Variant;
import edu.cmu.cs.fusion.alias.ObjectLabel;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.predicates.RelationshipPredicate;
import edu.cmu.cs.fusion.constraint.predicates.TestPredicate;
import edu.cmu.cs.fusion.test.TestEnvironment;
import edu.cmu.cs.fusion.test.TestUtils;

public class TestTestPredicate {
	static TestUtils utils;
	
	@BeforeClass
	static public void setup() {
		utils = new TestUtils();
	}
	
	@Test
	public void testFreeVars() {
		FreeVars fv;
		RelationshipPredicate relPred = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		Predicate pred = new TestPredicate(relPred, utils.getVar(2));
		
		fv = pred.getFreeVariables();
		assertEquals(3, fv.size());

		String[] types = utils.getRelation(0).getFullyQualifiedTypes();
		assertEquals(FreeVars.BOOL_TYPE, fv.getType(utils.getVar(2)));		
		assertEquals(types[0], fv.getType(utils.getVar(0)));
		assertEquals(types[1], fv.getType(utils.getVar(1)));
	}
	
	@Test
	public void testTruthTrue1() {
		RelationshipPredicate relPred = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(4), utils.getVar(3)});
		Predicate pred = new TestPredicate(relPred, utils.getVar(0));
		Map<ObjectLabel, ThreeValue> tvs = new HashMap<ObjectLabel, ThreeValue>();
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), tvs, Variant.SOUND_VARIANT);		
		tvs.put(utils.getSub(0).getSub(utils.getVar(0)), ThreeValue.TRUE);
		assertEquals(ThreeValue.TRUE, pred.getTruth(env, utils.getSub(0)));
	}
	
	@Test
	public void testTruthTrue2() {
		RelationshipPredicate relPred = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(4), utils.getVar(2)});
		Predicate pred = new TestPredicate(relPred, utils.getVar(0));
		Map<ObjectLabel, ThreeValue> tvs = new HashMap<ObjectLabel, ThreeValue>();
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), tvs, Variant.SOUND_VARIANT);
		
		tvs.put(utils.getSub(0).getSub(utils.getVar(0)), ThreeValue.FALSE);
		assertEquals(ThreeValue.TRUE, pred.getTruth(env, utils.getSub(0)));
	}
	
	@Test
	public void testTruthNotTrue1() {
		RelationshipPredicate relPred = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(4), utils.getVar(3)});
		TestPredicate pred = new TestPredicate(relPred, utils.getVar(0));
		pred.setPositive(false);
		Map<ObjectLabel, ThreeValue> tvs = new HashMap<ObjectLabel, ThreeValue>();
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), tvs, Variant.SOUND_VARIANT);
		
		tvs.put(utils.getSub(0).getSub(utils.getVar(0)), ThreeValue.TRUE);
		assertEquals(ThreeValue.FALSE, pred.getTruth(env, utils.getSub(0)));
	}
	
	@Test
	public void testTruthNotTrue2() {
		RelationshipPredicate relPred = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(4), utils.getVar(2)});
		TestPredicate pred = new TestPredicate(relPred, utils.getVar(0));
		pred.setPositive(false);
		Map<ObjectLabel, ThreeValue> tvs = new HashMap<ObjectLabel, ThreeValue>();
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), tvs, Variant.SOUND_VARIANT);
		
		tvs.put(utils.getSub(0).getSub(utils.getVar(0)), ThreeValue.FALSE);
		assertEquals(ThreeValue.FALSE, pred.getTruth(env, utils.getSub(0)));
	}

	
	@Test
	public void testTruthFalse1() {
		RelationshipPredicate relPred = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(4), utils.getVar(3)});
		Predicate pred = new TestPredicate(relPred, utils.getVar(0));
		Map<ObjectLabel, ThreeValue> tvs = new HashMap<ObjectLabel, ThreeValue>();
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), tvs, Variant.SOUND_VARIANT);
		
		tvs.put(utils.getSub(0).getSub(utils.getVar(0)), ThreeValue.FALSE);
		assertEquals(ThreeValue.FALSE, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthFalse2() {
		RelationshipPredicate relPred = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(4), utils.getVar(2)});
		Predicate pred = new TestPredicate(relPred, utils.getVar(0));
		Map<ObjectLabel, ThreeValue> tvs = new HashMap<ObjectLabel, ThreeValue>();
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), tvs, Variant.SOUND_VARIANT);
		
		tvs.put(utils.getSub(0).getSub(utils.getVar(0)), ThreeValue.TRUE);
		assertEquals(ThreeValue.FALSE, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthNotFalse1() {
		RelationshipPredicate relPred = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(4), utils.getVar(3)});
		TestPredicate pred = new TestPredicate(relPred, utils.getVar(0));
		pred.setPositive(false);
		Map<ObjectLabel, ThreeValue> tvs = new HashMap<ObjectLabel, ThreeValue>();
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), tvs, Variant.SOUND_VARIANT);
		
		tvs.put(utils.getSub(0).getSub(utils.getVar(0)), ThreeValue.FALSE);
		assertEquals(ThreeValue.TRUE, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthNotFalse2() {
		RelationshipPredicate relPred = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(4), utils.getVar(2)});
		TestPredicate pred = new TestPredicate(relPred, utils.getVar(0));
		pred.setPositive(false);
		Map<ObjectLabel, ThreeValue> tvs = new HashMap<ObjectLabel, ThreeValue>();
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), tvs, Variant.SOUND_VARIANT);
		
		tvs.put(utils.getSub(0).getSub(utils.getVar(0)), ThreeValue.TRUE);
		assertEquals(ThreeValue.TRUE, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthUnknown1() {
		RelationshipPredicate relPred = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(4), utils.getVar(2)});
		Predicate pred = new TestPredicate(relPred, utils.getVar(0));
		Map<ObjectLabel, ThreeValue> tvs = new HashMap<ObjectLabel, ThreeValue>();
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), tvs, Variant.SOUND_VARIANT);
		
		tvs.put(utils.getSub(0).getSub(utils.getVar(0)), ThreeValue.UNKNOWN);
		assertEquals(ThreeValue.UNKNOWN, pred.getTruth(env, utils.getSub(0)));
	}
	
	@Test
	public void testTruthUnknown2() {
		RelationshipPredicate relPred = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(2), utils.getVar(3)});
		Predicate pred = new TestPredicate(relPred, utils.getVar(0));
		Map<ObjectLabel, ThreeValue> tvs = new HashMap<ObjectLabel, ThreeValue>();
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), tvs, Variant.SOUND_VARIANT);
		
		tvs.put(utils.getSub(0).getSub(utils.getVar(0)), ThreeValue.TRUE);
		assertEquals(ThreeValue.UNKNOWN, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthNotUnknown1() {
		RelationshipPredicate relPred = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(4), utils.getVar(2)});
		TestPredicate pred = new TestPredicate(relPred, utils.getVar(0));
		pred.setPositive(false);
		Map<ObjectLabel, ThreeValue> tvs = new HashMap<ObjectLabel, ThreeValue>();
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), tvs, Variant.SOUND_VARIANT);
		
		tvs.put(utils.getSub(0).getSub(utils.getVar(0)), ThreeValue.UNKNOWN);
		assertEquals(ThreeValue.UNKNOWN, pred.getTruth(env, utils.getSub(0)));
	}
	
	@Test
	public void testTruthNotUnknown2() {
		RelationshipPredicate relPred = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(2), utils.getVar(3)});
		TestPredicate pred = new TestPredicate(relPred, utils.getVar(0));
		pred.setPositive(false);
		Map<ObjectLabel, ThreeValue> tvs = new HashMap<ObjectLabel, ThreeValue>();
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), tvs, Variant.SOUND_VARIANT);
		
		tvs.put(utils.getSub(0).getSub(utils.getVar(0)), ThreeValue.TRUE);
		assertEquals(ThreeValue.UNKNOWN, pred.getTruth(env, utils.getSub(0)));
	}
}
