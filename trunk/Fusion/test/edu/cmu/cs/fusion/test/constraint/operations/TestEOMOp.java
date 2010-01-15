package edu.cmu.cs.fusion.test.constraint.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.crystal.tac.model.ReturnInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.operations.EndOfMethodOp;
import edu.cmu.cs.fusion.test.EqualityOnlyTypeHierarchy;
import edu.cmu.cs.fusion.test.TestUtils;
import edu.cmu.cs.fusion.test.constraint.DefaultReturnInstruction;

public class TestEOMOp {
	static TestUtils utils;
	
	@BeforeClass
	static public void setup() {
		utils = new TestUtils();
	}

	@Test
	public void testFreeVars() {
		EndOfMethodOp construct = new EndOfMethodOp();
		FreeVars fv = construct.getFreeVariables();
		
		assertEquals(0, fv.size());
	}
	
	@Test
	public void testMatchWrongInstr() {
		StubTypeBinding[] vBindings = new StubTypeBinding[] {new StubTypeBinding("Bar"), new StubTypeBinding("Baz")};
		List<StubVariable> params = new ArrayList<StubVariable>();
		params.add(new StubVariable());
		params.add(new StubVariable());
		
		StubTypeBinding rBinding = new StubTypeBinding("Foo");		
		
		StubMethodCallInstruction instr = new StubMethodCallInstruction("mName", new StubVariable(), params, new StubMethodBinding(rBinding, vBindings), new StubVariable());	
		
		EndOfMethodOp op = new EndOfMethodOp();
		
		ConsList<Pair<SpecVar, Variable>> map = op.matches(new EqualityOnlyTypeHierarchy(), instr);

		assertTrue(map == null);
	}
	
	@Test
	public void testMatchCorrect() {
		ReturnInstruction instr = new DefaultReturnInstruction();
		EndOfMethodOp op = new EndOfMethodOp();

		ConsList<Pair<SpecVar,Variable>> list = op.matches(new EqualityOnlyTypeHierarchy(), instr);
		
		assertTrue(list.isEmpty());
	}	
}
