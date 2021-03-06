package edu.cmu.cs.fusion.test.parsers.predicate;

import org.junit.Assert;
import org.junit.Test;

import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.Operation;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.operations.BeginOfMethodOp;
import edu.cmu.cs.fusion.constraint.operations.ConstructorOp;
import edu.cmu.cs.fusion.constraint.operations.EndOfMethodOp;
import edu.cmu.cs.fusion.constraint.operations.MethodInvocationOp;
import edu.cmu.cs.fusion.parsers.predicate.FPLParser;
import edu.cmu.cs.fusion.parsers.predicate.ParseException;
import edu.cmu.cs.fusion.test.StubIType;

public class ParseOperations {	
	@Test
	public void testEOMEmpty() throws ParseException {
		String string = "EOM";
		
		FPLParser parser = new FPLParser(string, null, new StubIType());
		Operation op = parser.operation();
		
		Assert.assertTrue("Parsed predicate should be a EOM, but is " + op.getClass().getCanonicalName(), op instanceof EndOfMethodOp);
		
		EndOfMethodOp invoke = (EndOfMethodOp)op;
				
		FreeVars vars = invoke.getFreeVariables();
		Assert.assertEquals("Wrong number of free vars", 0, vars.size());		
	}

	@Test
	public void testEOMNonStaticReturns() throws ParseException {
		String string = "EOM: Foo.bar(..) : Baz";
		
		FPLParser parser = new FPLParser(string, null, new StubIType());
		Operation op = parser.operation();
		
		Assert.assertTrue("Parsed predicate should be a EOM, but is " + op.getClass().getCanonicalName(), op instanceof EndOfMethodOp);
		
		EndOfMethodOp invoke = (EndOfMethodOp)op;
				
		FreeVars vars = invoke.getFreeVariables();
		Assert.assertEquals("Wrong number of free vars", 2, vars.size());		
		Assert.assertEquals("Wrong type", "Foo", vars.getType(Constraint.RECEIVER));		
		Assert.assertEquals("Wrong type", "Baz", vars.getType(Constraint.RESULT));		
	}

	@Test
	public void testEOMStaticReturns() throws ParseException {
		String string = "EOM: static Foo.bar(..) : Baz";
		
		FPLParser parser = new FPLParser(string, null, new StubIType());
		Operation op = parser.operation();
		
		Assert.assertTrue("Parsed predicate should be a EOM, but is " + op.getClass().getCanonicalName(), op instanceof EndOfMethodOp);
		
		EndOfMethodOp invoke = (EndOfMethodOp)op;
				
		FreeVars vars = invoke.getFreeVariables();
		Assert.assertEquals("Wrong number of free vars", 1, vars.size());			
		Assert.assertEquals("Wrong type", "Baz", vars.getType(Constraint.RESULT));		
	}
	
	@Test
	public void testBOMEmpty() throws ParseException {
		String string = "BOM";
		
		FPLParser parser = new FPLParser(string, null, new StubIType());
		Operation op = parser.operation();
		
		Assert.assertTrue("Parsed predicate should be a BOM, but is " + op.getClass().getCanonicalName(), op instanceof BeginOfMethodOp);
		
		BeginOfMethodOp invoke = (BeginOfMethodOp)op;
				
		FreeVars vars = invoke.getFreeVariables();
		Assert.assertEquals("Wrong number of free vars", 0, vars.size());			
	}

	@Test
	public void testBOMWCParams() throws ParseException {
		String string = "BOM: Foo.Bar(..)";
		
		FPLParser parser = new FPLParser(string, null, new StubIType());
		Operation op = parser.operation();
		
		Assert.assertTrue("Parsed predicate should be a BOM, but is " + op.getClass().getCanonicalName(), op instanceof BeginOfMethodOp);
		
		BeginOfMethodOp invoke = (BeginOfMethodOp)op;
				
		FreeVars vars = invoke.getFreeVariables();
		Assert.assertEquals("Wrong number of free vars", 1, vars.size());		
		Assert.assertEquals("Wrong type", "Foo", vars.getType(Constraint.RECEIVER));		
	}

	@Test
	public void testBOMWCName() throws ParseException {
		String string = "BOM: Foo.*()";
		
		FPLParser parser = new FPLParser(string, null, new StubIType());
		Operation op = parser.operation();
		
		Assert.assertTrue("Parsed predicate should be a BOM, but is " + op.getClass().getCanonicalName(), op instanceof BeginOfMethodOp);
		
		BeginOfMethodOp invoke = (BeginOfMethodOp)op;
				
		FreeVars vars = invoke.getFreeVariables();
		Assert.assertEquals("Wrong number of free vars", 1, vars.size());		
		Assert.assertEquals("Wrong type", "Foo", vars.getType(Constraint.RECEIVER));		
	}

	@Test
	public void testBOMWCReceiver() throws ParseException {
		String string = "BOM: *.Name(Bar bar, Baz baz)";
		
		FPLParser parser = new FPLParser(string, null, new StubIType());
		Operation op = parser.operation();
		
		Assert.assertTrue("Parsed predicate should be a BOM, but is " + op.getClass().getCanonicalName(), op instanceof BeginOfMethodOp);
		
		BeginOfMethodOp invoke = (BeginOfMethodOp)op;
				
		FreeVars vars = invoke.getFreeVariables();
		Assert.assertEquals("Wrong number of free vars", 3, vars.size());
		Assert.assertEquals("Wrong type", "java.lang.Object", vars.getType(Constraint.RECEIVER));		
		Assert.assertEquals("Wrong type", "Baz", vars.getType(new SpecVar("baz")));		
		Assert.assertEquals("Wrong type", "Bar", vars.getType(new SpecVar("bar")));		
	}

	@Test
	public void testBOMStatic() throws ParseException {
		String string = "BOM: static Foo.Name(Bar bar, Baz baz) : Baz";
		
		FPLParser parser = new FPLParser(string, null, new StubIType());
		Operation op = parser.operation();
		
		Assert.assertTrue("Parsed predicate should be a BOM, but is " + op.getClass().getCanonicalName(), op instanceof BeginOfMethodOp);
		
		BeginOfMethodOp invoke = (BeginOfMethodOp)op;
				
		FreeVars vars = invoke.getFreeVariables();
		Assert.assertEquals("Wrong number of free vars", 2, vars.size());		
		Assert.assertEquals("Wrong type", "Baz", vars.getType(new SpecVar("baz")));		
		Assert.assertEquals("Wrong type", "Bar", vars.getType(new SpecVar("bar")));		
	}

	@Test
	public void testMethodInvocation0() throws ParseException {
		String string = "Foo.mName() : Baz";
		
		FPLParser parser = new FPLParser(string, null, new StubIType());
		Operation op = parser.operation();
		
		Assert.assertTrue("Parsed predicate should be a MethodInvoke, but is " + op.getClass().getCanonicalName(), op instanceof MethodInvocationOp);
		
		MethodInvocationOp invoke = (MethodInvocationOp)op;
				
		FreeVars vars = invoke.getFreeVariables();
		Assert.assertEquals("Wrong number of free vars", 2, vars.size());
		Assert.assertEquals("Wrong type", "Foo", vars.getType(Constraint.RECEIVER));		
		Assert.assertEquals("Wrong type", "Baz", vars.getType(Constraint.RESULT));		
	}

	@Test
	public void testMethodInvocation1() throws ParseException {
		String string = "Foo.mName(Bar bar) : Baz";
		
		FPLParser parser = new FPLParser(string, null, new StubIType());
		Operation op = parser.operation();
		
		Assert.assertTrue("Parsed predicate should be a MethodInvoke, but is " + op.getClass().getCanonicalName(), op instanceof MethodInvocationOp);
		
		MethodInvocationOp invoke = (MethodInvocationOp)op;
				
		FreeVars vars = invoke.getFreeVariables();
		Assert.assertEquals("Wrong number of free vars", 3, vars.size());
		Assert.assertEquals("Wrong type", "Foo", vars.getType(Constraint.RECEIVER));		
		Assert.assertEquals("Wrong type", "Baz", vars.getType(Constraint.RESULT));		
		Assert.assertEquals("Wrong type", "Bar", vars.getType(new SpecVar("bar")));		
	}

	@Test
	public void testMethodInvocation2() throws ParseException {
		String string = "Foo.mName(Bar bar, Baz baz) : Baz";
		
		FPLParser parser = new FPLParser(string, null, new StubIType());
		Operation op = parser.operation();
		
		Assert.assertTrue("Parsed predicate should be a MethodInvoke, but is " + op.getClass().getCanonicalName(), op instanceof MethodInvocationOp);
		
		MethodInvocationOp invoke = (MethodInvocationOp)op;
				
		FreeVars vars = invoke.getFreeVariables();
		Assert.assertEquals("Wrong number of free vars", 4, vars.size());
		Assert.assertEquals("Wrong type", "Foo", vars.getType(Constraint.RECEIVER));		
		Assert.assertEquals("Wrong type", "Baz", vars.getType(Constraint.RESULT));		
		Assert.assertEquals("Wrong type", "Bar", vars.getType(new SpecVar("bar")));		
		Assert.assertEquals("Wrong type", "Baz", vars.getType(new SpecVar("baz")));		
	}

	@Test
	public void testConstructor0() throws ParseException {
		String string = "Foo()";
		
		FPLParser parser = new FPLParser(string, null, new StubIType());
		Operation op = parser.operation();
		
		Assert.assertTrue("Parsed predicate should be a ConstructorOp, but is " + op.getClass().getCanonicalName(), op instanceof ConstructorOp);
		
		ConstructorOp construct = (ConstructorOp)op;
				
		FreeVars vars = construct.getFreeVariables();
		Assert.assertEquals("Wrong number of free vars", 1, vars.size());
		Assert.assertEquals("Wrong type", "Foo", vars.getType(Constraint.RESULT));				
	}

	@Test
	public void testConstructor1() throws ParseException {
		String string = "Foo(Bar bar)";
		
		FPLParser parser = new FPLParser(string, null, new StubIType());
		Operation op = parser.operation();
		
		Assert.assertTrue("Parsed predicate should be a ConstructorOp, but is " + op.getClass().getCanonicalName(), op instanceof ConstructorOp);
		
		ConstructorOp construct = (ConstructorOp)op;
				
		FreeVars vars = construct.getFreeVariables();
		Assert.assertEquals("Wrong number of free vars", 2, vars.size());
		Assert.assertEquals("Wrong type", "Foo", vars.getType(Constraint.RESULT));				
		Assert.assertEquals("Wrong type", "Bar", vars.getType(new SpecVar("bar")));				
	}

	@Test
	public void testConstructor2() throws ParseException {
		String string = "Foo(Bar bar, Baz baz)";
		
		FPLParser parser = new FPLParser(string, null, new StubIType());
		Operation op = parser.operation();
		
		Assert.assertTrue("Parsed predicate should be a ConstructorOp, but is " + op.getClass().getCanonicalName(), op instanceof ConstructorOp);
		
		ConstructorOp construct = (ConstructorOp)op;
				
		FreeVars vars = construct.getFreeVariables();
		Assert.assertEquals("Wrong number of free vars", 3, vars.size());
		Assert.assertEquals("Wrong type", "Foo", vars.getType(Constraint.RESULT));				
		Assert.assertEquals("Wrong type", "Bar", vars.getType(new SpecVar("bar")));				
		Assert.assertEquals("Wrong type", "Baz", vars.getType(new SpecVar("baz")));				
	}
}
