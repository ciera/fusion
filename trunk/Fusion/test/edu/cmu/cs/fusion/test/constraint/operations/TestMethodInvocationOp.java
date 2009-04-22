package edu.cmu.cs.fusion.test.constraint.operations;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import edu.cmu.cs.crystal.util.Pair;

import edu.cmu.cs.crystal.tac.MethodCallInstruction;
import edu.cmu.cs.crystal.tac.Variable;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.operations.MethodInvocationOp;
import edu.cmu.cs.fusion.test.TestUtils;

public class TestMethodInvocationOp {
	static TestUtils utils;
	
	@BeforeClass
	static public void setup() {
		utils = new TestUtils();
	}

	@Test
	public void testFreeVars() {
		SpecVar[] vars = new SpecVar[] {utils.getVar(0), utils.getVar(1)};
		MethodInvocationOp invoke = new MethodInvocationOp("mName", "Foo", vars, new String[] {"Foo", "Bar"}, "Bar");
		FreeVars fv = invoke.getFreeVariables();
		
		assertEquals("Foo", fv.getType(new SpecVar(Constraint.TARGET)));
		assertEquals("Bar", fv.getType(new SpecVar(Constraint.RESULT)));
		assertEquals("Foo", fv.getType(utils.getVar(0)));
		assertEquals("Bar", fv.getType(utils.getVar(1)));
		
		int size = 0;
		for (SpecVar var : fv)
			size++;
		assertEquals(4, size);
	}
	
	@Test
	@Ignore
	public void testMatchWrongInstr() {
	}
	
	@Test
	public void testMatchWrongMethodName() {
		List<StubVariable> params = new ArrayList<StubVariable>();
		params.add(new StubVariable());
		params.add(new StubVariable());

		MethodCallInstruction instr = getMCI(new StubVariable(), new StubVariable(), params);
		SpecVar[] vars = new SpecVar[] {utils.getVar(0), utils.getVar(1)};
		String[] vTypes = new String[] {"Bar", "Baz"};
		MethodInvocationOp op = new MethodInvocationOp("testtesttest", "Foo", vars, vTypes, "Bazaz");
		
		ConsList<Pair<SpecVar, Variable>> map = op.matches(instr);
		
		assertTrue(map == null);
	}
	
	@Test
	public void testMatchWrongReceiverType() {
		List<StubVariable> params = new ArrayList<StubVariable>();
		params.add(new StubVariable());
		params.add(new StubVariable());

		MethodCallInstruction instr = getMCI(new StubVariable(), new StubVariable(), params);
		SpecVar[] vars = new SpecVar[] {utils.getVar(0), utils.getVar(1)};
		String[] vTypes = new String[] {"Bar", "Baz"};
		MethodInvocationOp op = new MethodInvocationOp("mName", "Foo2", vars, vTypes, "Bazaz");
		
		ConsList<Pair<SpecVar, Variable>> map = op.matches(instr);
		
		assertTrue(map == null);
	}

	@Test
	public void testMatchWrongParamType() {
		List<StubVariable> params = new ArrayList<StubVariable>();
		params.add(new StubVariable());
		params.add(new StubVariable());

		MethodCallInstruction instr = getMCI(new StubVariable(), new StubVariable(), params);
		SpecVar[] vars = new SpecVar[] {utils.getVar(0), utils.getVar(1)};
		String[] vTypes = new String[] {"Bar", "Baz2"};
		MethodInvocationOp op = new MethodInvocationOp("mName", "Foo", vars, vTypes, "Bazaz");
		
		ConsList<Pair<SpecVar,Variable>> map = op.matches(instr);
		
		assertTrue(map == null);	
	}
	
	@Test
	public void testMatchWrongParamNums() {
		List<StubVariable> params = new ArrayList<StubVariable>();
		params.add(new StubVariable());
		params.add(new StubVariable());
		params.add(new StubVariable());

		MethodCallInstruction instr = getMCI(new StubVariable(), new StubVariable(), params);
		SpecVar[] vars = new SpecVar[] {utils.getVar(0), utils.getVar(1)};
		String[] vTypes = new String[] {"Bar", "Baz", "blah"};
		MethodInvocationOp op = new MethodInvocationOp("mName", "Foo", vars, vTypes, "Bazaz");
		
		ConsList<Pair<SpecVar,Variable>> map = op.matches(instr);
		
		assertTrue(map == null);
		
	}
	
	//how many things did it verify?
	
	@Test
	public void testMatchCorrect() {
		StubVariable rVar = new StubVariable();
		StubVariable tVar = new StubVariable();
		StubVariable p1 = new StubVariable();
		StubVariable p2 = new StubVariable();
		List<StubVariable> params = new ArrayList<StubVariable>();
		params.add(p1);
		params.add(p2);

		MethodCallInstruction instr = getMCI(rVar, tVar, params);
		SpecVar[] vars = new SpecVar[] {utils.getVar(0), utils.getVar(1)};
		String[] vTypes = new String[] {"Bar", "Baz"};
		MethodInvocationOp op = new MethodInvocationOp("mName", "Foo", vars, vTypes, "Bazaz");
		
		ConsList<Pair<SpecVar,Variable>> list = op.matches(instr);
		
		assertTrue(list != null);
		assertTrue(list.contains(new Pair<SpecVar, Variable>(new SpecVar(Constraint.RESULT), rVar)));
		assertTrue(list.contains(new Pair<SpecVar, Variable>(new SpecVar(Constraint.TARGET), tVar)));
		assertTrue(list.contains(new Pair<SpecVar, Variable>(utils.getVar(0), p1)));
		assertTrue(list.contains(new Pair<SpecVar, Variable>(utils.getVar(1), p2)));
		
		assertEquals(4, list.size());
	}	

	@Test
	public void testMatchCorrectAndAliased() {
		StubVariable rVar = new StubVariable();
		StubVariable tVar = new StubVariable();
		List<StubVariable> params = new ArrayList<StubVariable>();
		params.add(rVar);
		params.add(tVar);

		MethodCallInstruction instr = getMCI(rVar, tVar, params);
		SpecVar[] vars = new SpecVar[] {utils.getVar(0), utils.getVar(1)};
		String[] vTypes = new String[] {"Bar", "Baz"};
		MethodInvocationOp op = new MethodInvocationOp("mName", "Foo", vars, vTypes, "Bazaz");
		
		ConsList<Pair<SpecVar,Variable>> list = op.matches(instr);
		
		assertTrue(list != null);
		assertTrue(list.contains(new Pair<SpecVar, Variable>(new SpecVar(Constraint.RESULT), rVar)));
		assertTrue(list.contains(new Pair<SpecVar, Variable>(new SpecVar(Constraint.TARGET), tVar)));
		assertTrue(list.contains(new Pair<SpecVar, Variable>(utils.getVar(0), rVar)));
		assertTrue(list.contains(new Pair<SpecVar, Variable>(utils.getVar(1), tVar)));
		
		assertEquals(4, list.size());

	}
	
	private MethodCallInstruction getMCI(StubVariable resVar, StubVariable tarVar, List<StubVariable> params) {
		StubTypeBinding rBinding = new StubTypeBinding("Foo");		
		StubTypeBinding[] vBindings = new StubTypeBinding[] {new StubTypeBinding("Bar"), new StubTypeBinding("Baz")};
		
		return new StubMethodCallInstruction("mName", resVar, tarVar, params, new StubMethodBinding(rBinding, vBindings));	
	}
}
