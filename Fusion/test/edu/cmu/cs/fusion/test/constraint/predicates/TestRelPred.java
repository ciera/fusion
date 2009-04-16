package edu.cmu.cs.fusion.test.constraint.predicates;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.predicates.RelationshipPredicate;
import edu.cmu.cs.fusion.test.RelsOnlyTestEnvironment;
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
		
		int size = 0;
		for (SpecVar var : fv)
			size++;
		assertEquals(size, 2);
	}
	
	@Test
	public void testTruthFalse() {
		RelationshipPredicate pred = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(2)});
		FusionEnvironment env = new RelsOnlyTestEnvironment(utils.getContext(0));
		
		assertEquals(pred.getTruth(env, utils.getSub(0)), ThreeValue.FALSE);
	}

	@Test
	public void testTruthTrue() {
		RelationshipPredicate pred = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		FusionEnvironment env = new RelsOnlyTestEnvironment(utils.getContext(0));
		
		assertEquals(pred.getTruth(env, utils.getSub(0)), ThreeValue.TRUE);
	}

	@Test
	public void testTruthUnknownNoInference() {
		RelationshipPredicate pred = new RelationshipPredicate(utils.getRelation(1), new SpecVar[] {utils.getVar(2), utils.getVar(1)});
		FusionEnvironment env = new RelsOnlyTestEnvironment(utils.getContext(0));
		
		assertEquals(pred.getTruth(env, utils.getSub(0)), ThreeValue.UNKNOWN);
	}
}
