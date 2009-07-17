package edu.cmu.cs.fusion.test.constraint.predicates;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.predicates.InstanceOfPredicate;
import edu.cmu.cs.fusion.test.TestEnvironment;
import edu.cmu.cs.fusion.test.TestUtils;

public class TestInstanceOfPred {
	static TestUtils utils;
	
	@BeforeClass
	static public void setup() {
		utils = new TestUtils();
	}
	
	@Test
	public void testFreeVars() {
		FreeVars fv;
		InstanceOfPredicate pred;
		
		pred = new InstanceOfPredicate(utils.getVar(0), "Foo");
		fv = pred.getFreeVariables();
	
		assertEquals(FreeVars.OBJECT_TYPE, fv.getType(utils.getVar(0)));
		
		assertEquals(1, fv.size());
	}
	
	@Test
	public void testTruthFalse() {
		InstanceOfPredicate pred = new InstanceOfPredicate(utils.getVar(0), "Foo");
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), null);
		
		assertEquals(ThreeValue.FALSE, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthTrue() {
		InstanceOfPredicate pred = new InstanceOfPredicate(utils.getVar(1), "Foo");
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), null);
		
		assertEquals(ThreeValue.TRUE, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthNotFalse() {
		InstanceOfPredicate pred = new InstanceOfPredicate(utils.getVar(0), "Foo");
		pred.setPositive(false);
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), null);
		
		assertEquals(ThreeValue.TRUE, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthNotTrue() {
		InstanceOfPredicate pred = new InstanceOfPredicate(utils.getVar(1), "Foo");
		pred.setPositive(false);
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), null);
		
		assertEquals(ThreeValue.FALSE, pred.getTruth(env, utils.getSub(0)));
	}
}
