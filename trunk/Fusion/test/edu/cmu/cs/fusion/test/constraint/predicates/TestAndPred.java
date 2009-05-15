package edu.cmu.cs.fusion.test.constraint.predicates;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.predicates.AndPredicate;
import edu.cmu.cs.fusion.constraint.predicates.RelationshipPredicate;
import edu.cmu.cs.fusion.test.TestEnvironment;
import edu.cmu.cs.fusion.test.TestUtils;

public class TestAndPred {
	static TestUtils utils;
	
	@BeforeClass
	static public void setup() {
		utils = new TestUtils();
	}
	
	@Test
	public void testFreeVars() {
		FreeVars fv;
		String[] types = utils.getRelation(0).getFullyQualifiedTypes();
		
		RelationshipPredicate lP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(2)});
		RelationshipPredicate rP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		AndPredicate pred = new AndPredicate(lP, rP);
		fv = pred.getFreeVariables();
	
		assertEquals(types[0], fv.getType(utils.getVar(0)));
		assertEquals(types[1], fv.getType(utils.getVar(1)));
		assertEquals(types[1], fv.getType(utils.getVar(2)));

		assertEquals(3, fv.size());
	}
	
	@Test
	public void testTruthTrue() {
		RelationshipPredicate lP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		RelationshipPredicate rP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(4), utils.getVar(3)});
		AndPredicate pred = new AndPredicate(lP, rP);

		FusionEnvironment env = new TestEnvironment(utils.getContext(0));
		
		assertEquals(ThreeValue.TRUE, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthFalse1() {
		RelationshipPredicate lP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(2)});
		RelationshipPredicate rP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(4), utils.getVar(3)});
		AndPredicate pred = new AndPredicate(lP, rP);

		FusionEnvironment env = new TestEnvironment(utils.getContext(0));
		
		assertEquals(ThreeValue.FALSE, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthFalse2() {
		RelationshipPredicate lP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(2), utils.getVar(3)});
		RelationshipPredicate rP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(2)});
		AndPredicate pred = new AndPredicate(lP, rP);

		FusionEnvironment env = new TestEnvironment(utils.getContext(0));
		
		assertEquals(ThreeValue.FALSE, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthUnknown1() {
		RelationshipPredicate lP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		RelationshipPredicate rP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(2), utils.getVar(3)});
		AndPredicate pred = new AndPredicate(lP, rP);

		FusionEnvironment env = new TestEnvironment(utils.getContext(0));
		
		assertEquals(ThreeValue.UNKNOWN, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthUnknown2() {
		RelationshipPredicate lP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(2), utils.getVar(3)});
		RelationshipPredicate rP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		AndPredicate pred = new AndPredicate(lP, rP);

		FusionEnvironment env = new TestEnvironment(utils.getContext(0));
		
		assertEquals(ThreeValue.UNKNOWN, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthUnknown3() {
		RelationshipPredicate lP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(2), utils.getVar(3)});
		RelationshipPredicate rP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(3), utils.getVar(2)});
		AndPredicate pred = new AndPredicate(lP, rP);

		FusionEnvironment env = new TestEnvironment(utils.getContext(1));
		
		assertEquals(ThreeValue.UNKNOWN, pred.getTruth(env, utils.getSub(0)));
	}
}
