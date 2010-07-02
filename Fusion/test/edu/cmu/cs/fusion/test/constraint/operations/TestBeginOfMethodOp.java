package edu.cmu.cs.fusion.test.constraint.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.crystal.tac.model.NewObjectInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.fusion.Binding;
import edu.cmu.cs.fusion.Method;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.operations.BeginOfMethodOp;
import edu.cmu.cs.fusion.relationship.EntryInstruction;
import edu.cmu.cs.fusion.test.EqualityOnlyTypeHierarchy;
import edu.cmu.cs.fusion.test.TestUtils;
import edu.cmu.cs.fusion.xml.NamedTypeBinding;

public class TestBeginOfMethodOp {
	static TestUtils utils;
	
	@BeforeClass
	static public void setup() {
		utils = new TestUtils();
	}

	@Test
	public void testFreeVarsWild() {
		BeginOfMethodOp begin = new BeginOfMethodOp(null, null, null, null);
		FreeVars fv = begin.getFreeVariables();
		
		assertEquals("java.lang.Object", fv.getType(Constraint.RECEIVER));		
		assertEquals(1, fv.size());
	}
	
	@Test
	public void testFreeVarsBoundedReceiver() {
		BeginOfMethodOp begin = new BeginOfMethodOp("Foo", null, null, null);
		FreeVars fv = begin.getFreeVariables();
		
		assertEquals("Foo", fv.getType(Constraint.RECEIVER));		
		assertEquals(1, fv.size());
	}
	
	@Test
	public void testFreeVarsBoundedParams() {
		SpecVar[] vars = new SpecVar[] {utils.getVar(0), utils.getVar(1)};
		BeginOfMethodOp begin = new BeginOfMethodOp(null, null, vars, new String[] {"Bar", "Baz"});
		FreeVars fv = begin.getFreeVariables();
		
		assertEquals("java.lang.Object", fv.getType(Constraint.RECEIVER));		
		assertEquals("Bar", fv.getType(utils.getVar(0)));		
		assertEquals("Baz", fv.getType(utils.getVar(1)));		
		assertEquals(3, fv.size());
	}
	
	@Test
	public void testMatchWrongInstr() {
		NamedTypeBinding[] vBindings = new NamedTypeBinding[] {new NamedTypeBinding("Bar"), new NamedTypeBinding("Baz")};
		StubVariable v1 = new StubVariable();
		StubVariable v2 = new StubVariable();
		
		List<StubVariable> params = new ArrayList<StubVariable>();
		params.add(v1);
		params.add(v2);
		
		StubMethodBinding binding = new StubMethodBinding(new NamedTypeBinding("Foo"), vBindings);
		Method method = new Method(new Variable[] {v1, v2}, null, binding);
		
		NewObjectInstruction instr = new StubNewObjectInstruction(params, binding, new StubVariable());	
		BeginOfMethodOp op = new BeginOfMethodOp(null, null, null, null);
		
		ConsList<Binding> map = op.matches(new EqualityOnlyTypeHierarchy(), method, instr);

		assertTrue(map == null);
	}
	
	@Test
	public void testMatchWrongMethodName() {
		List<Variable> params = new ArrayList<Variable>();
		params.add(new StubVariable());
		params.add(new StubVariable());

		Pair<EntryInstruction, Method> pair = getEntryAndMethod(new StubVariable(), params);
		BeginOfMethodOp op = new BeginOfMethodOp(null, "OtherName", null, null);
		
		ConsList<Binding> map = op.matches(new EqualityOnlyTypeHierarchy(), pair.snd(), pair.fst());
		
		assertTrue(map == null);
	}

	@Test
	public void testMatchRightMethodName() {
		List<Variable> params = new ArrayList<Variable>();
		params.add(new StubVariable());
		params.add(new StubVariable());
		StubVariable var = new StubVariable();

		Pair<EntryInstruction, Method> pair = getEntryAndMethod(var, params);
		BeginOfMethodOp op = new BeginOfMethodOp(null, "name", null, null);
		
		ConsList<Binding> map = op.matches(new EqualityOnlyTypeHierarchy(), pair.snd(), pair.fst());
		
		assertTrue(map != null);
		assertEquals(1, map.size());
		assertTrue(map.contains(new Binding(Constraint.RECEIVER, var)));
	}

	@Test
	public void testMatchWrongReceiverType() {
		List<Variable> params = new ArrayList<Variable>();
		params.add(new StubVariable());
		params.add(new StubVariable());

		Pair<EntryInstruction, Method> pair = getEntryAndMethod(new StubVariable(), params);
		BeginOfMethodOp op = new BeginOfMethodOp("Bar", null, null, null);
		
		ConsList<Binding> map = op.matches(new EqualityOnlyTypeHierarchy(), pair.snd(), pair.fst());
		
		assertTrue(map == null);
	}

	@Test
	public void testMatchRightReceiverType() {
		List<Variable> params = new ArrayList<Variable>();
		params.add(new StubVariable());
		params.add(new StubVariable());
		StubVariable var = new StubVariable();

		Pair<EntryInstruction, Method> pair = getEntryAndMethod(var, params);
		BeginOfMethodOp op = new BeginOfMethodOp("Foo", null, null, null);
		
		ConsList<Binding> map = op.matches(new EqualityOnlyTypeHierarchy(), pair.snd(), pair.fst());
		
		assertTrue(map != null);
		assertEquals(1, map.size());
		assertTrue(map.contains(new Binding(Constraint.RECEIVER, var)));
	}

	@Test
	public void testMatchWrongParamNums() {
		List<Variable> params = new ArrayList<Variable>();
		params.add(new StubVariable());
		params.add(new StubVariable());
		params.add(new StubVariable());

		Pair<EntryInstruction, Method> pair = getEntryAndMethod(new StubVariable(), params);
		SpecVar[] vars = new SpecVar[] {utils.getVar(0), utils.getVar(1)};
		String[] vTypes = new String[] {"Bar", "Baz", "blah"};
		BeginOfMethodOp op = new BeginOfMethodOp(null, null, vars, vTypes);
		
		ConsList<Binding> list = op.matches(new EqualityOnlyTypeHierarchy(), pair.snd(), pair.fst());
		
		assertTrue(list == null);
	}

	@Test
	public void testMatchWrongParamType() {
		List<Variable> params = new ArrayList<Variable>();
		params.add(new StubVariable());
		params.add(new StubVariable());

		Pair<EntryInstruction, Method> pair = getEntryAndMethod(new StubVariable(), params);
		SpecVar[] vars = new SpecVar[] {utils.getVar(0), utils.getVar(1)};
		String[] vTypes = new String[] {"Bar", "Baz2"};
		BeginOfMethodOp op = new BeginOfMethodOp(null, null, vars, vTypes);
		
		ConsList<Binding> list = op.matches(new EqualityOnlyTypeHierarchy(), pair.snd(), pair.fst());
		
		assertTrue(list == null);	
	}
	
	
	@Test
	public void testMatchCorrectAll() {
		StubVariable tVar = new StubVariable();
		StubVariable p1 = new StubVariable();
		StubVariable p2 = new StubVariable();
		List<Variable> params = new ArrayList<Variable>();
		params.add(p1);
		params.add(p2);

		Pair<EntryInstruction, Method> pair = getEntryAndMethod(tVar, params);
		SpecVar[] vars = new SpecVar[] {utils.getVar(0), utils.getVar(1)};
		String[] vTypes = new String[] {"Bar", "Baz"};
		BeginOfMethodOp op = new BeginOfMethodOp("Foo", "name", vars, vTypes);
		
		ConsList<Binding> list = op.matches(new EqualityOnlyTypeHierarchy(), pair.snd(), pair.fst());
		
		assertTrue(list != null);
		assertTrue(list.contains(new Binding(Constraint.RECEIVER, tVar)));
		assertTrue(list.contains(new Binding(utils.getVar(0), p1)));
		assertTrue(list.contains(new Binding(utils.getVar(1), p2)));
		
		assertEquals(3, list.size());
	}	

	@Test
	public void testMatchCorrectAndMultiple() {
		StubVariable tVar = new StubVariable();
		List<Variable> params = new ArrayList<Variable>();
		params.add(tVar);
		params.add(tVar);

		Pair<EntryInstruction, Method> pair = getEntryAndMethod(tVar, params);
		SpecVar[] vars = new SpecVar[] {utils.getVar(0), utils.getVar(1)};
		String[] vTypes = new String[] {"Bar", "Baz"};
		BeginOfMethodOp op = new BeginOfMethodOp(null, null, vars, vTypes);
		
		ConsList<Binding> list = op.matches(new EqualityOnlyTypeHierarchy(), pair.snd(), pair.fst());
		
		assertTrue(list != null);
		assertTrue(list.contains(new Binding(Constraint.RECEIVER, tVar)));
		assertTrue(list.contains(new Binding(utils.getVar(0), tVar)));
		assertTrue(list.contains(new Binding(utils.getVar(1), tVar)));
		
		assertEquals(3, list.size());

	}

	private Pair<EntryInstruction, Method> getEntryAndMethod(StubVariable tarVar, List<Variable> params) {
		NamedTypeBinding rBinding = new NamedTypeBinding("Foo");		
		NamedTypeBinding[] vBindings = new NamedTypeBinding[] {new NamedTypeBinding("Bar"), new NamedTypeBinding("Baz")};
		IMethodBinding methodBinding = new StubMethodBinding("name", rBinding, vBindings);
		
		Variable[] vArr = new Variable[params.size()];
		int ndx = 0;
		for (Variable var : params) {
			vArr[ndx] = var;
			ndx++;
		}
		                              
		
		return new Pair<EntryInstruction, Method>(new EntryInstruction(tarVar, params, methodBinding), new Method(vArr, tarVar, methodBinding));	
	}
}
