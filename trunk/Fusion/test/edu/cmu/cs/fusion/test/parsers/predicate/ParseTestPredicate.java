package edu.cmu.cs.fusion.test.parsers.predicate;

import org.junit.Assert;
import org.junit.Test;

import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.RelationsEnvironment;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.predicates.TestPredicate;
import edu.cmu.cs.fusion.parsers.predicate.FPLParser;
import edu.cmu.cs.fusion.parsers.predicate.ParseException;
import edu.cmu.cs.fusion.test.StubIType;

public class ParseTestPredicate {	
	@Test
	public void testTestPosPosPred() throws ParseException {
		String string = "?Foo(a, b) : c";
		RelationsEnvironment env = new RelationsEnvironment();
		env.addRelation(new Relation("Foo", new String[] {"Bar", "Baz"}));
		
		FPLParser parser = new FPLParser(string, env, new StubIType());
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a TestPredicate, but is " + pred.getClass().getCanonicalName(), pred instanceof TestPredicate);
		
		TestPredicate val = (TestPredicate)pred;
		Assert.assertTrue("Parsed predicate should be positive", val.isPositive());
		Assert.assertTrue("Parsed inner predicate should be positive", val.getRelationship().isPositive());
		Assert.assertEquals("Inner relationship incorrect", "Foo", val.getRelationship().getRelation().getName());
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain three free variables", 3, vars.size());
		Assert.assertEquals("c should have a boolean type", "boolean", vars.getType(new SpecVar("c")));		
		Assert.assertEquals("a should have a Bar type", "Bar", vars.getType(new SpecVar("a")));		
		Assert.assertEquals("b should have a Baz type", "Baz", vars.getType(new SpecVar("b")));		
	}


	@Test
	public void testTestNegPosPred() throws ParseException {
		String string = "?Foo(a, b) : !c";
		RelationsEnvironment env = new RelationsEnvironment();
		env.addRelation(new Relation("Foo", new String[] {"Bar", "Baz"}));
		
		FPLParser parser = new FPLParser(string, env, new StubIType());
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a TestPredicate, but is " + pred.getClass().getCanonicalName(), pred instanceof TestPredicate);
		
		TestPredicate val = (TestPredicate)pred;
		Assert.assertTrue("Parsed predicate should be negative", !val.isPositive());
		Assert.assertTrue("Parsed inner predicate should be positive", val.getRelationship().isPositive());
		Assert.assertEquals("Inner relationship incorrect", "Foo", val.getRelationship().getRelation().getName());
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain three free variables", 3, vars.size());
		Assert.assertEquals("c should have a boolean type", "boolean", vars.getType(new SpecVar("c")));		
		Assert.assertEquals("a should have a Bar type", "Bar", vars.getType(new SpecVar("a")));		
		Assert.assertEquals("b should have a Baz type", "Baz", vars.getType(new SpecVar("b")));		
	}
	
	@Test
	public void testTestPosNegPred() throws ParseException {
		String string = "?!Foo(a, b) : c";
		RelationsEnvironment env = new RelationsEnvironment();
		env.addRelation(new Relation("Foo", new String[] {"Bar", "Baz"}));
		
		FPLParser parser = new FPLParser(string, env, new StubIType());
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a TestPredicate, but is " + pred.getClass().getCanonicalName(), pred instanceof TestPredicate);
		
		TestPredicate val = (TestPredicate)pred;
		Assert.assertTrue("Parsed predicate should be positive", val.isPositive());
		Assert.assertTrue("Parsed inner predicate should be negative", !val.getRelationship().isPositive());
		Assert.assertEquals("Inner relationship incorrect", "Foo", val.getRelationship().getRelation().getName());
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain three free variables", 3, vars.size());
		Assert.assertEquals("c should have a boolean type", "boolean", vars.getType(new SpecVar("c")));		
		Assert.assertEquals("a should have a Bar type", "Bar", vars.getType(new SpecVar("a")));		
		Assert.assertEquals("b should have a Baz type", "Baz", vars.getType(new SpecVar("b")));		
	}

	@Test
	public void testTestNegNegPred() throws ParseException {
		String string = "?!Foo(a, b) : !c";
		RelationsEnvironment env = new RelationsEnvironment();
		env.addRelation(new Relation("Foo", new String[] {"Bar", "Baz"}));
		
		FPLParser parser = new FPLParser(string, env, new StubIType());
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a TestPredicate, but is " + pred.getClass().getCanonicalName(), pred instanceof TestPredicate);
		
		TestPredicate val = (TestPredicate)pred;
		Assert.assertTrue("Parsed predicate should be negative", !val.isPositive());
		Assert.assertTrue("Parsed inner predicate should be negative", !val.getRelationship().isPositive());
		Assert.assertEquals("Inner relationship incorrect", "Foo", val.getRelationship().getRelation().getName());
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain three free variables", 3, vars.size());
		Assert.assertEquals("c should have a boolean type", "boolean", vars.getType(new SpecVar("c")));		
		Assert.assertEquals("a should have a Bar type", "Bar", vars.getType(new SpecVar("a")));		
		Assert.assertEquals("b should have a Baz type", "Baz", vars.getType(new SpecVar("b")));		
	}
}
