package edu.cmu.cs.fusion.test.constraint.predicates;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.predicates.InstanceOfPredicate;
import edu.cmu.cs.fusion.constraint.predicates.ReferenceEqualityPredicate;
import edu.cmu.cs.fusion.test.TestEnvironment;
import edu.cmu.cs.fusion.test.TestUtils;

public class TestReferenceEqPred {
	static TestUtils utils;
	
	@BeforeClass
	static public void setup() {
		utils = new TestUtils();
	}
	
	@Test
	public void testFreeVars() {
		FreeVars fv;
		ReferenceEqualityPredicate pred;
		
		pred = new ReferenceEqualityPredicate(utils.getVar(0), utils.getVar(1));
		fv = pred.getFreeVariables();
	
		assertEquals(FreeVars.OBJECT_TYPE, fv.getType(utils.getVar(0)));
		assertEquals(FreeVars.OBJECT_TYPE, fv.getType(utils.getVar(1)));
		
		int size = 0;
		for (SpecVar var : fv)
			size++;
		assertEquals(2, size);
	}
	
	@Test
	public void testTruthFalse() {
		ReferenceEqualityPredicate pred = new ReferenceEqualityPredicate(utils.getVar(0), utils.getVar(1));
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), null, null);
		
		assertEquals(ThreeValue.FALSE, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthTrue() {
		ReferenceEqualityPredicate pred = new ReferenceEqualityPredicate(utils.getVar(1), utils.getVar(3));
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), null, null);
		
		assertEquals(ThreeValue.TRUE, pred.getTruth(env, utils.getSub(0)));
	}
}
