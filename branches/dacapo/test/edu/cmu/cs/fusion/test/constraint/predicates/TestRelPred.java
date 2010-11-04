package edu.cmu.cs.fusion.test.constraint.predicates;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.Variant;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.predicates.RelationshipPredicate;
import edu.cmu.cs.fusion.test.TestEnvironment;
import edu.cmu.cs.fusion.test.TestUtils;

public class TestRelPred {
	static TestUtils utils;
	
	@BeforeClass
	static public void setup() {
		utils = new TestUtils();
	}
	
	@Test
	public void testFreeVars() {
		FreeVars fv;
		RelationshipPredicate pred;
		String[] types = utils.getRelation(0).getFullyQualifiedTypes();
		
		pred = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		fv = pred.getFreeVariables();
	
		assertEquals(types[0], fv.getType(utils.getVar(0)));
		assertEquals(types[1], fv.getType(utils.getVar(1)));
		
		assertEquals(2, fv.size());
	}
	
	@Test
	public void testTruthFalse() {
		RelationshipPredicate pred = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(2)});
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), Variant.SOUND_VARIANT);
		
		assertEquals(ThreeValue.FALSE, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthTrue() {
		RelationshipPredicate pred = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), Variant.SOUND_VARIANT);
		
		assertEquals(ThreeValue.TRUE, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthUnknownNoInference() {
		RelationshipPredicate pred = new RelationshipPredicate(utils.getRelation(1), new SpecVar[] {utils.getVar(2), utils.getVar(1)});
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), Variant.SOUND_VARIANT);
		
		assertEquals(ThreeValue.UNKNOWN, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthNotFalse() {
		RelationshipPredicate pred = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(2)});
		pred.setPositive(false);
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), Variant.SOUND_VARIANT);
		
		assertEquals(ThreeValue.TRUE, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthNotTrue() {
		RelationshipPredicate pred = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		pred.setPositive(false);
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), Variant.SOUND_VARIANT);
		
		assertEquals(ThreeValue.FALSE, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthNotUnknownNoInference() {
		RelationshipPredicate pred = new RelationshipPredicate(utils.getRelation(1), new SpecVar[] {utils.getVar(2), utils.getVar(1)});
		pred.setPositive(false);
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), Variant.SOUND_VARIANT);
		
		assertEquals(ThreeValue.UNKNOWN, pred.getTruth(env, utils.getSub(0)));
	}
}
