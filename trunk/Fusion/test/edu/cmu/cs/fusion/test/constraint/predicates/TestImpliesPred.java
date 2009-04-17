package edu.cmu.cs.fusion.test.constraint.predicates;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.predicates.ImpliesPredicate;
import edu.cmu.cs.fusion.constraint.predicates.RelationshipPredicate;
import edu.cmu.cs.fusion.test.TestEnvironment;
import edu.cmu.cs.fusion.test.TestUtils;

public class TestImpliesPred {
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
		ImpliesPredicate pred = new ImpliesPredicate(lP, rP);
		fv = pred.getFreeVariables();
	
		assertEquals(types[0], fv.getType(utils.getVar(0)));
		assertEquals(types[1], fv.getType(utils.getVar(1)));
		assertEquals(types[1], fv.getType(utils.getVar(2)));
		
		int size = 0;
		for (SpecVar var : fv)
			size++;
		assertEquals(size, 3);
	}
	
	@Test
	public void testTruthTrue1() {
		//Test T -> T
		RelationshipPredicate lP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		RelationshipPredicate rP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(4), utils.getVar(3)});
		ImpliesPredicate pred = new ImpliesPredicate(lP, rP);

		FusionEnvironment env = new TestEnvironment(utils.getContext(0));
		
		assertEquals(ThreeValue.TRUE, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthTrue2() {
		//Test F -> F
		RelationshipPredicate lP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(2)});
		RelationshipPredicate rP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(4), utils.getVar(2)});
		ImpliesPredicate pred = new ImpliesPredicate(lP, rP);

		FusionEnvironment env = new TestEnvironment(utils.getContext(0));
		
		assertEquals(ThreeValue.TRUE, pred.getTruth(env, utils.getSub(0)));
	}
	
	@Test
	public void testTruthFalse() {
		//Test T -> F
		RelationshipPredicate lP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(4), utils.getVar(3)});
		RelationshipPredicate rP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(2)});
		ImpliesPredicate pred = new ImpliesPredicate(lP, rP);

		FusionEnvironment env = new TestEnvironment(utils.getContext(0));
		
		assertEquals(ThreeValue.FALSE, pred.getTruth(env, utils.getSub(0)));
	}


	@Test
	public void testTruthUnknown1() {
		//Test T -> U
		RelationshipPredicate lP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(4), utils.getVar(3)});
		RelationshipPredicate rP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(2), utils.getVar(3)});
		ImpliesPredicate pred = new ImpliesPredicate(lP, rP);

		FusionEnvironment env = new TestEnvironment(utils.getContext(0));
		
		assertEquals(ThreeValue.UNKNOWN, pred.getTruth(env, utils.getSub(0)));
	}
	
	@Test
	public void testTruthUnknown2() {
		//Test U -> F
		RelationshipPredicate lP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(2), utils.getVar(3)});
		RelationshipPredicate rP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(2)});
		ImpliesPredicate pred = new ImpliesPredicate(lP, rP);

		FusionEnvironment env = new TestEnvironment(utils.getContext(0));
		
		assertEquals(ThreeValue.UNKNOWN, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthUnknown3() {
		//Test U -> U
		RelationshipPredicate lP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(2), utils.getVar(3)});
		RelationshipPredicate rP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(3), utils.getVar(2)});
		ImpliesPredicate pred = new ImpliesPredicate(lP, rP);

		FusionEnvironment env = new TestEnvironment(utils.getContext(1));
		
		assertEquals(ThreeValue.UNKNOWN, pred.getTruth(env, utils.getSub(0)));
	}
}
