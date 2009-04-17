package edu.cmu.cs.fusion.test.constraint.predicates;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.predicates.FalsePredicate;
import edu.cmu.cs.fusion.constraint.predicates.NotPredicate;
import edu.cmu.cs.fusion.constraint.predicates.RelationshipPredicate;
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
		FreeVars fv;
		Predicate tPred = new TruePredicate();
		Predicate fPred = new FalsePredicate();
		String[] types = utils.getRelation(0).getFullyQualifiedTypes();
		int size = 0;
		
		fv = tPred.getFreeVariables();
		size = 0;
		for (SpecVar var : fv)
			size++;
		assertEquals(size, 0);
		
		fv = fPred.getFreeVariables();
		size = 0;
		for (SpecVar var : fv)
			size++;
		assertEquals(size, 0);
	}
	
	@Test
	public void testTruthFalse() {
		Predicate fPred = new FalsePredicate();
		FusionEnvironment env = new TestEnvironment(utils.getContext(0));
		
		assertEquals(fPred.getTruth(env, utils.getSub(0)), ThreeValue.FALSE);
	}

	@Test
	public void testTruthTrue() {
		Predicate tPred = new TruePredicate();
		FusionEnvironment env = new TestEnvironment(utils.getContext(0));
		
		assertEquals(tPred.getTruth(env, utils.getSub(0)), ThreeValue.TRUE);
	}
}
