package edu.cmu.cs.fusion.test.constraint.predicates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.Variant;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.predicates.FalsePredicate;
import edu.cmu.cs.fusion.constraint.predicates.TruePredicate;
import edu.cmu.cs.fusion.test.TestEnvironment;
import edu.cmu.cs.fusion.test.TestUtils;

public class TestSimplePred {
	static TestUtils utils;
	
	@BeforeClass
	static public void setup() {
		utils = new TestUtils();
	}
	
	@Test
	public void testFreeVars() {
		Predicate tPred = new TruePredicate();
		Predicate fPred = new FalsePredicate();
		
		assertTrue(tPred.getFreeVariables().isEmpty());
		assertTrue(fPred.getFreeVariables().isEmpty());
	}
	
	@Test
	public void testTruthFalse() {
		Predicate fPred = new FalsePredicate();
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), Variant.SOUND_VARIANT);
		
		assertEquals(fPred.getTruth(env, utils.getSub(0)), ThreeValue.FALSE);
	}

	@Test
	public void testTruthTrue() {
		Predicate tPred = new TruePredicate();
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), Variant.SOUND_VARIANT);
		
		assertEquals(tPred.getTruth(env, utils.getSub(0)), ThreeValue.TRUE);
	}
}
