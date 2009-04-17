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
		
		int size = 0;
		for (SpecVar var : fv)
			size++;
		assertEquals(1, size);
	}
	
	@Test
	public void testTruthFalse() {
		InstanceOfPredicate pred = new InstanceOfPredicate(utils.getVar(0), "Foo");
		Map<ObjectLabel, String> types = new HashMap<ObjectLabel, String>();
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), null, types);
		
		types.put(utils.getLabel(0), "Bar");
		assertEquals(ThreeValue.FALSE, pred.getTruth(env, utils.getSub(0)));
	}

	@Test
	public void testTruthTrue() {
		InstanceOfPredicate pred = new InstanceOfPredicate(utils.getVar(0), "Foo");
		Map<ObjectLabel, String> types = new HashMap<ObjectLabel, String>();
		FusionEnvironment env = new TestEnvironment(utils.getContext(0), null, types);
		
		types.put(utils.getLabel(0), "Foo");
		assertEquals(ThreeValue.TRUE, pred.getTruth(env, utils.getSub(0)));
	}
}
