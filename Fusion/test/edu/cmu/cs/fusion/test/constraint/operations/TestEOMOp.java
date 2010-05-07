package edu.cmu.cs.fusion.test.constraint.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.crystal.tac.model.ReturnInstruction;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.fusion.Binding;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.operations.EndOfMethodOp;
import edu.cmu.cs.fusion.test.EqualityOnlyTypeHierarchy;
import edu.cmu.cs.fusion.test.TestUtils;
import edu.cmu.cs.fusion.test.constraint.DefaultReturnInstruction;
import edu.cmu.cs.fusion.xml.NamedTypeBinding;

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
		NamedTypeBinding[] vBindings = new NamedTypeBinding[] {new NamedTypeBinding("Bar"), new NamedTypeBinding("Baz")};
		List<StubVariable> params = new ArrayList<StubVariable>();
		params.add(new StubVariable());
		params.add(new StubVariable());
		
		NamedTypeBinding rBinding = new NamedTypeBinding("Foo");		
		
		StubMethodCallInstruction instr = new StubMethodCallInstruction("mName", new StubVariable(), params, new StubMethodBinding(rBinding, vBindings), new StubVariable());	
		
		EndOfMethodOp op = new EndOfMethodOp();
		
		ConsList<Binding> map = op.matches(new EqualityOnlyTypeHierarchy(), instr);

		assertTrue(map == null);
	}
	
	@Test
	public void testMatchCorrect() {
		ReturnInstruction instr = new DefaultReturnInstruction();
		EndOfMethodOp op = new EndOfMethodOp();

		ConsList<Binding> list = op.matches(new EqualityOnlyTypeHierarchy(), instr);
		
		assertTrue(list.isEmpty());
	}	
}
