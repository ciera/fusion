package edu.cmu.cs.fusion.test.constraint.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.crystal.tac.model.NewObjectInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.operations.ConstructorOp;
import edu.cmu.cs.fusion.test.EqualityOnlyTypeHierarchy;
import edu.cmu.cs.fusion.test.TestUtils;
import edu.cmu.cs.fusion.xml.NamedTypeBinding;

public class TestNewObjectInstructionOp {
	static TestUtils utils;
	
	@BeforeClass
	static public void setup() {
		utils = new TestUtils();
	}

	@Test
	public void testFreeVars() {
		SpecVar[] vars = new SpecVar[] {utils.getVar(1)};
		ConstructorOp construct = new ConstructorOp("Foo", vars, new String[] {"Bar"});
		FreeVars fv = construct.getFreeVariables();
		
		assertEquals("Foo", fv.getType(Constraint.RESULT));
		assertEquals("Bar", fv.getType(utils.getVar(1)));
		
		assertEquals(2, fv.size());
	}
	
	@Test
	public void testMatchWrongInstr() {
		NamedTypeBinding[] vBindings = new NamedTypeBinding[] {new NamedTypeBinding("Bar"), new NamedTypeBinding("Baz")};
		List<StubVariable> params = new ArrayList<StubVariable>();
		params.add(new StubVariable());
		params.add(new StubVariable());
		
		NamedTypeBinding rBinding = new NamedTypeBinding("Foo");		
		
		StubMethodCallInstruction instr = new StubMethodCallInstruction("mName", new StubVariable(), params, new StubMethodBinding(rBinding, vBindings), new StubVariable());	
		
		SpecVar[] vars = new SpecVar[] {utils.getVar(0)};
		String[] vTypes = new String[] {"Bar"};
		ConstructorOp op = new ConstructorOp("Foo", vars, vTypes);
		
		ConsList<Pair<SpecVar, Variable>> map = op.matches(new EqualityOnlyTypeHierarchy(), instr);

		assertTrue(map == null);
	}
	
	@Test
	public void testMatchWrongType() {
		NewObjectInstruction instr = getNOI(new StubVariable(), new StubVariable());
		
		ConstructorOp op = new ConstructorOp("Baz", new SpecVar[] {utils.getVar(1)}, new String[] {"Bar"});
		
		ConsList<Pair<SpecVar, Variable>> map = op.matches(new EqualityOnlyTypeHierarchy(), instr);
		
		assertTrue(map == null);
	}

	@Test
	public void testMatchWrongParamType() {
		NewObjectInstruction instr = getNOI(new StubVariable(), new StubVariable());

		ConstructorOp op = new ConstructorOp("Foo", new SpecVar[] {utils.getVar(1)}, new String[] {"Baz"});
		
		ConsList<Pair<SpecVar,Variable>> map = op.matches(new EqualityOnlyTypeHierarchy(), instr);
		
		assertTrue(map == null);	
	}
	
	@Test
	public void testMatchWrongParamNums() {
		NewObjectInstruction instr = getNOI(new StubVariable(), new StubVariable());

		SpecVar[] vars = new SpecVar[] {utils.getVar(0), utils.getVar(1)};
		String[] vTypes = new String[] {"Bar", "Baz"};
		ConstructorOp op = new ConstructorOp("Foo", vars, vTypes);
		
		ConsList<Pair<SpecVar,Variable>> map = op.matches(new EqualityOnlyTypeHierarchy(), instr);
		
		assertTrue(map == null);
		
	}	
	
	@Test
	public void testMatchCorrect() {
		StubVariable target = new StubVariable();
		StubVariable param = new StubVariable();

		NewObjectInstruction instr = getNOI(target, param);
		SpecVar[] vars = new SpecVar[] {utils.getVar(0)};
		String[] vTypes = new String[] {"Bar"};
		ConstructorOp op = new ConstructorOp("Foo", vars, vTypes);
		
		ConsList<Pair<SpecVar,Variable>> list = op.matches(new EqualityOnlyTypeHierarchy(), instr);
		
		assertTrue(list != null);
		assertTrue(list.contains(new Pair<SpecVar, Variable>(Constraint.RESULT, target)));
		assertTrue(list.contains(new Pair<SpecVar, Variable>(utils.getVar(0), param)));
		
		assertEquals(2, list.size());
	}	

	@Test
	public void testMatchCorrectAndMultiple() {
		StubVariable target = new StubVariable();

		NewObjectInstruction instr = getNOI(target, target);
		SpecVar[] vars = new SpecVar[] {utils.getVar(0)};
		String[] vTypes = new String[] {"Bar"};
		ConstructorOp op = new ConstructorOp("Foo", vars, vTypes);
		
		ConsList<Pair<SpecVar,Variable>> list = op.matches(new EqualityOnlyTypeHierarchy(), instr);
		
		assertTrue(list != null);
		assertTrue(list.contains(new Pair<SpecVar, Variable>(Constraint.RESULT, target)));
		assertTrue(list.contains(new Pair<SpecVar, Variable>(utils.getVar(0), target)));
		
		assertEquals(2, list.size());

	}

	private NewObjectInstruction getNOI(StubVariable tarVar, StubVariable param) {
		List<StubVariable> params = new LinkedList<StubVariable>();
		params.add(param);
		NamedTypeBinding rBinding = new NamedTypeBinding("Foo");		
		NamedTypeBinding[] vBindings = new NamedTypeBinding[] {new NamedTypeBinding("Bar")};
		
		return new StubNewObjectInstruction(params, new StubMethodBinding(rBinding, vBindings), tarVar);	
	}
}
