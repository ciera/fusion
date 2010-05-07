package edu.cmu.cs.fusion.test.constraint.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.crystal.tac.model.MethodCallInstruction;
import edu.cmu.cs.crystal.tac.model.NewObjectInstruction;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.fusion.Binding;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.operations.MethodInvocationOp;
import edu.cmu.cs.fusion.test.EqualityOnlyTypeHierarchy;
import edu.cmu.cs.fusion.test.TestUtils;
import edu.cmu.cs.fusion.xml.NamedTypeBinding;

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
		
		assertEquals("Foo", fv.getType(Constraint.RECEIVER));
		assertEquals("Bar", fv.getType(Constraint.RESULT));
		assertEquals("Foo", fv.getType(utils.getVar(0)));
		assertEquals("Bar", fv.getType(utils.getVar(1)));
		
		assertEquals(4, fv.size());
	}
	
	@Test
	public void testMatchWrongInstr() {
		NamedTypeBinding[] vBindings = new NamedTypeBinding[] {new NamedTypeBinding("Bar"), new NamedTypeBinding("Baz")};
		List<StubVariable> params = new ArrayList<StubVariable>();
		params.add(new StubVariable());
		params.add(new StubVariable());
		
		NewObjectInstruction instr = new StubNewObjectInstruction(params, new StubMethodBinding(new NamedTypeBinding("Foo"), vBindings), new StubVariable());	
		SpecVar[] vars = new SpecVar[] {utils.getVar(0), utils.getVar(1)};
		String[] vTypes = new String[] {"Bar", "Baz"};
		MethodInvocationOp op = new MethodInvocationOp("testtesttest", "Foo", vars, vTypes, "void");
		
		ConsList<Binding> map = op.matches(new EqualityOnlyTypeHierarchy(), instr);

		assertTrue(map == null);
	}
	
	@Test
	public void testMatchWrongMethodName() {
		List<StubVariable> params = new ArrayList<StubVariable>();
		params.add(new StubVariable());
		params.add(new StubVariable());

		MethodCallInstruction instr = getMCI(new StubVariable(), params, new StubVariable());
		SpecVar[] vars = new SpecVar[] {utils.getVar(0), utils.getVar(1)};
		String[] vTypes = new String[] {"Bar", "Baz"};
		MethodInvocationOp op = new MethodInvocationOp("testtesttest", "Foo", vars, vTypes, "Bazaz");
		
		ConsList<Binding> map = op.matches(new EqualityOnlyTypeHierarchy(), instr);
		
		assertTrue(map == null);
	}
	
	@Test
	public void testMatchWrongReceiverType() {
		List<StubVariable> params = new ArrayList<StubVariable>();
		params.add(new StubVariable());
		params.add(new StubVariable());

		MethodCallInstruction instr = getMCI(new StubVariable(), params, new StubVariable());
		SpecVar[] vars = new SpecVar[] {utils.getVar(0), utils.getVar(1)};
		String[] vTypes = new String[] {"Bar", "Baz"};
		MethodInvocationOp op = new MethodInvocationOp("mName", "Foo2", vars, vTypes, "Bazaz");
		
		ConsList<Binding> map = op.matches(new EqualityOnlyTypeHierarchy(), instr);
		
		assertTrue(map == null);
	}

	@Test
	public void testMatchWrongParamType() {
		List<StubVariable> params = new ArrayList<StubVariable>();
		params.add(new StubVariable());
		params.add(new StubVariable());

		MethodCallInstruction instr = getMCI(new StubVariable(), params, new StubVariable());
		SpecVar[] vars = new SpecVar[] {utils.getVar(0), utils.getVar(1)};
		String[] vTypes = new String[] {"Bar", "Baz2"};
		MethodInvocationOp op = new MethodInvocationOp("mName", "Foo", vars, vTypes, "Bazaz");
		
		ConsList<Binding> map = op.matches(new EqualityOnlyTypeHierarchy(), instr);
		
		assertTrue(map == null);	
	}
	
	@Test
	public void testMatchWrongParamNums() {
		List<StubVariable> params = new ArrayList<StubVariable>();
		params.add(new StubVariable());
		params.add(new StubVariable());
		params.add(new StubVariable());

		MethodCallInstruction instr = getMCI(new StubVariable(), params, new StubVariable());
		SpecVar[] vars = new SpecVar[] {utils.getVar(0), utils.getVar(1)};
		String[] vTypes = new String[] {"Bar", "Baz", "blah"};
		MethodInvocationOp op = new MethodInvocationOp("mName", "Foo", vars, vTypes, "Bazaz");
		
		ConsList<Binding> map = op.matches(new EqualityOnlyTypeHierarchy(), instr);
		
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

		MethodCallInstruction instr = getMCI(rVar, params, tVar);
		SpecVar[] vars = new SpecVar[] {utils.getVar(0), utils.getVar(1)};
		String[] vTypes = new String[] {"Bar", "Baz"};
		MethodInvocationOp op = new MethodInvocationOp("mName", "Foo", vars, vTypes, "Bazaz");
		
		ConsList<Binding> list = op.matches(new EqualityOnlyTypeHierarchy(), instr);
		
		assertTrue(list != null);
		assertTrue(list.contains(new Binding(Constraint.RESULT, tVar)));
		assertTrue(list.contains(new Binding(Constraint.RECEIVER, rVar)));
		assertTrue(list.contains(new Binding(utils.getVar(0), p1)));
		assertTrue(list.contains(new Binding(utils.getVar(1), p2)));
		
		assertEquals(4, list.size());
	}	

	@Test
	public void testMatchCorrectAndMultiple() {
		StubVariable rVar = new StubVariable();
		StubVariable tVar = new StubVariable();
		List<StubVariable> params = new ArrayList<StubVariable>();
		params.add(rVar);
		params.add(tVar);

		MethodCallInstruction instr = getMCI(rVar, params, tVar);
		SpecVar[] vars = new SpecVar[] {utils.getVar(0), utils.getVar(1)};
		String[] vTypes = new String[] {"Bar", "Baz"};
		MethodInvocationOp op = new MethodInvocationOp("mName", "Foo", vars, vTypes, "Bazaz");
		
		ConsList<Binding> list = op.matches(new EqualityOnlyTypeHierarchy(), instr);
		
		assertTrue(list != null);
		assertTrue(list.contains(new Binding(Constraint.RESULT, tVar)));
		assertTrue(list.contains(new Binding(Constraint.RECEIVER, rVar)));
		assertTrue(list.contains(new Binding(utils.getVar(0), rVar)));
		assertTrue(list.contains(new Binding(utils.getVar(1), tVar)));
		
		assertEquals(4, list.size());

	}

	private MethodCallInstruction getMCI(StubVariable resVar, List<StubVariable> params, StubVariable tarVar) {
		NamedTypeBinding rBinding = new NamedTypeBinding("Foo");		
		NamedTypeBinding[] vBindings = new NamedTypeBinding[] {new NamedTypeBinding("Bar"), new NamedTypeBinding("Baz")};
		
		return new StubMethodCallInstruction("mName", resVar, params, new StubMethodBinding(rBinding, vBindings), tarVar);	
	}
}
