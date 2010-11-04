package edu.cmu.cs.fusion.test.constraint.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.crystal.tac.model.ReturnInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.fusion.Binding;
import edu.cmu.cs.fusion.Method;
import edu.cmu.cs.fusion.constraint.Constraint;
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
		EndOfMethodOp construct = new EndOfMethodOp(null, null, null, null, null);
		FreeVars fv = construct.getFreeVariables();
		
		assertEquals(1, fv.size());
	}
	
	@Test
	public void testMatchWrongInstr() {
		NamedTypeBinding[] vBindings = new NamedTypeBinding[] {new NamedTypeBinding("Bar"), new NamedTypeBinding("Baz")};
		List<StubVariable> params = new ArrayList<StubVariable>();
		params.add(new StubVariable());
		params.add(new StubVariable());
		
		NamedTypeBinding rBinding = new NamedTypeBinding("Foo");		
		
		StubMethodCallInstruction instr = new StubMethodCallInstruction("mName", new StubVariable(), params, new StubMethodBinding(rBinding, vBindings), new StubVariable());	
		
		EndOfMethodOp op = new EndOfMethodOp(null, null, null, null, null);
		
		ConsList<Binding> map = op.matches(new EqualityOnlyTypeHierarchy(), null, instr);

		assertTrue(map == null);
	}
	
	@Test
	public void testMatchCorrect() {
		List<Variable> params = new ArrayList<Variable>();
		params.add(new StubVariable());
		params.add(new StubVariable());
		StubVariable var = new StubVariable();
	
		Pair<ReturnInstruction, Method> pair = getRetAndMethod(var, params, new StubVariable());
		
		EndOfMethodOp op = new EndOfMethodOp(null, null, null, null, null);

		ConsList<Binding> map = op.matches(new EqualityOnlyTypeHierarchy(), pair.snd(), pair.fst());
		
		assertTrue(map != null);
		assertEquals(1, map.size());
		assertTrue(map.contains(new Binding(Constraint.RECEIVER, var)));
	}	
	
	private Pair<ReturnInstruction, Method> getRetAndMethod(StubVariable tarVar, List<Variable> params, StubVariable rVar) {
		NamedTypeBinding rBinding = new NamedTypeBinding("Foo");		
		NamedTypeBinding[] vBindings = new NamedTypeBinding[] {new NamedTypeBinding("Bar"), new NamedTypeBinding("Baz")};
		IMethodBinding methodBinding = new StubMethodBinding("name", rBinding, vBindings);

		Variable[] vArr = new Variable[params.size()];
		int ndx = 0;
		for (Variable var : params) {
			vArr[ndx] = var;
			ndx++;
		}

		return new Pair<ReturnInstruction, Method>(new DefaultReturnInstruction(), new Method(vArr, tarVar, methodBinding));	
	}

}
