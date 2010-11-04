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
import edu.cmu.cs.fusion.constraint.predicates.BooleanValue;
import edu.cmu.cs.fusion.test.TestEnvironment;
import edu.cmu.cs.fusion.test.TestUtils;

public class TestBooleanValue {
	static TestUtils utils;
	
	@BeforeClass
	static public void setup() {
		utils = new TestUtils();
	}
	
	@Test
	public void testFreeVars() {
		FreeVars fv;
		Predicate bPred = new BooleanValue(utils.getVar(0));
		
		fv = bPred.getFreeVariables();
		assertEquals(1, fv.size());
		assertEquals(fv.getType(utils.getVar(0)), FreeVars.BOOL_TYPE);
	}
	
	@Test
	public void testTruthFalse() {
		Predicate bv = new BooleanValue(utils.getVar(0));
		Map<ObjectLabel, ThreeValue> tvs = new HashMap<ObjectLabel, ThreeValue>();
		tvs.put(utils.getSub(0).getSub(utils.getVar(0)), ThreeValue.FALSE);
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), tvs, Variant.SOUND_VARIANT);
		
		assertEquals(ThreeValue.FALSE, bv.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthTrue() {
		Predicate bv = new BooleanValue(utils.getVar(0));
		Map<ObjectLabel, ThreeValue> tvs = new HashMap<ObjectLabel, ThreeValue>();
		tvs.put(utils.getSub(0).getSub(utils.getVar(0)), ThreeValue.TRUE);
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), tvs, Variant.SOUND_VARIANT);
		
		assertEquals(ThreeValue.TRUE, bv.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthUnknown() {
		Predicate bv = new BooleanValue(utils.getVar(0));
		Map<ObjectLabel, ThreeValue> tvs = new HashMap<ObjectLabel, ThreeValue>();
		tvs.put(utils.getSub(0).getSub(utils.getVar(0)), ThreeValue.UNKNOWN);
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), tvs, Variant.SOUND_VARIANT);
		
		assertEquals(ThreeValue.UNKNOWN, bv.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthNotFalse() {
		BooleanValue bv = new BooleanValue(utils.getVar(0));
		bv.setPositive(false);
		Map<ObjectLabel, ThreeValue> tvs = new HashMap<ObjectLabel, ThreeValue>();
		tvs.put(utils.getSub(0).getSub(utils.getVar(0)), ThreeValue.FALSE);
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), tvs, Variant.SOUND_VARIANT);
		
		assertEquals(ThreeValue.TRUE, bv.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthNotTrue() {
		BooleanValue bv = new BooleanValue(utils.getVar(0));
		bv.setPositive(false);
		Map<ObjectLabel, ThreeValue> tvs = new HashMap<ObjectLabel, ThreeValue>();
		tvs.put(utils.getSub(0).getSub(utils.getVar(0)), ThreeValue.TRUE);
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), tvs, Variant.SOUND_VARIANT);
		
		assertEquals(ThreeValue.FALSE, bv.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthNotUnknown() {
		BooleanValue bv = new BooleanValue(utils.getVar(0));
		bv.setPositive(false);
		Map<ObjectLabel, ThreeValue> tvs = new HashMap<ObjectLabel, ThreeValue>();
		tvs.put(utils.getSub(0).getSub(utils.getVar(0)), ThreeValue.UNKNOWN);
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), tvs, Variant.SOUND_VARIANT);
		
		assertEquals(ThreeValue.UNKNOWN, bv.getTruth(env, utils.getSub(0)));
	}
}
