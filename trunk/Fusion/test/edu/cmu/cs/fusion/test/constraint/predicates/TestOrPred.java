package edu.cmu.cs.fusion.test.constraint.predicates;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.Variant;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.predicates.OrPredicate;
import edu.cmu.cs.fusion.constraint.predicates.RelationshipPredicate;
import edu.cmu.cs.fusion.test.TestEnvironment;
import edu.cmu.cs.fusion.test.TestUtils;

public class TestOrPred {
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
		OrPredicate pred = new OrPredicate(lP, rP);
		fv = pred.getFreeVariables();
	
		assertEquals(types[0], fv.getType(utils.getVar(0)));
		assertEquals(types[1], fv.getType(utils.getVar(1)));
		assertEquals(types[1], fv.getType(utils.getVar(2)));
		
		assertEquals(3, fv.size());
	}
	
	@Test
	public void testTruthTrue1() {
		//Test T or T
		RelationshipPredicate lP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(1)});
		RelationshipPredicate rP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(4), utils.getVar(3)});
		OrPredicate pred = new OrPredicate(lP, rP);

		FusionEnvironment env = new TestEnvironment(utils.getContext(0), Variant.SOUND_VARIANT);
		
		assertEquals(ThreeValue.TRUE, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthTrue2() {
		//Test U or T
		RelationshipPredicate lP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(2), utils.getVar(3)});
		RelationshipPredicate rP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(4), utils.getVar(3)});
		OrPredicate pred = new OrPredicate(lP, rP);

		FusionEnvironment env = new TestEnvironment(utils.getContext(0), Variant.SOUND_VARIANT);
		
		assertEquals(ThreeValue.TRUE, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthTrue3() {
		//Test T or F
		RelationshipPredicate lP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(4), utils.getVar(3)});
		RelationshipPredicate rP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(2)});
		OrPredicate pred = new OrPredicate(lP, rP);

		FusionEnvironment env = new TestEnvironment(utils.getContext(0), Variant.SOUND_VARIANT);
		
		assertEquals(ThreeValue.TRUE, pred.getTruth(env, utils.getSub(0)));
	}
	
	@Test
	public void testTruthFalse() {
		//Test F or F
		RelationshipPredicate lP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(2)});
		RelationshipPredicate rP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(4), utils.getVar(2)});
		OrPredicate pred = new OrPredicate(lP, rP);

		FusionEnvironment env = new TestEnvironment(utils.getContext(0), Variant.SOUND_VARIANT);
		
		assertEquals(ThreeValue.FALSE, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthUnknown1() {
		//Test U or F
		RelationshipPredicate lP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(2), utils.getVar(3)});
		RelationshipPredicate rP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(0), utils.getVar(2)});
		OrPredicate pred = new OrPredicate(lP, rP);

		FusionEnvironment env = new TestEnvironment(utils.getContext(0), Variant.SOUND_VARIANT);
		
		assertEquals(ThreeValue.UNKNOWN, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthUnknown2() {
		//Test U or U
		RelationshipPredicate lP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(2), utils.getVar(3)});
		RelationshipPredicate rP = new RelationshipPredicate(utils.getRelation(0), new SpecVar[] {utils.getVar(3), utils.getVar(2)});
		OrPredicate pred = new OrPredicate(lP, rP);

		FusionEnvironment env = new TestEnvironment(utils.getContext(1), Variant.SOUND_VARIANT);
		
		assertEquals(ThreeValue.UNKNOWN, pred.getTruth(env, utils.getSub(0)));
	}
}
