package edu.cmu.cs.fusion.test.parsers.predicate;

import org.junit.Assert;
import org.junit.Test;

import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.predicates.InstanceOfPredicate;
import edu.cmu.cs.fusion.constraint.predicates.ReferenceEqualityPredicate;
import edu.cmu.cs.fusion.parsers.predicate.FPLParser;
import edu.cmu.cs.fusion.parsers.predicate.ParseException;
import edu.cmu.cs.fusion.test.StubIType;

public class ParseBuiltInRels {
	
	
	@Test
	public void testInstanceOfTrue() throws ParseException {
		String string = "foo instanceof Foo";
		FPLParser parser = new FPLParser(string, null, new StubIType());
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a InstanceOfPredicate, but is " + pred.getClass().getCanonicalName(), pred instanceof InstanceOfPredicate);
		
		InstanceOfPredicate val = (InstanceOfPredicate)pred;
		Assert.assertTrue("Parsed predicate should be positive", val.isPositive());
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain one free variable", 1, vars.size());
		Assert.assertEquals("Foo should have a Foo type", "Foo", vars.getType(new SpecVar("foo")));		
	}
	
	@Test
	public void testNegInstanceOfTrue() throws ParseException {
		String string = "bar !instanceof Foo";
		FPLParser parser = new FPLParser(string, null, new StubIType());
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a InstanceOfPredicate, but is " + pred.getClass().getCanonicalName(), pred instanceof InstanceOfPredicate);
		
		InstanceOfPredicate val = (InstanceOfPredicate)pred;
		Assert.assertTrue("Parsed predicate should be negative", !val.isPositive());
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain one free variable", 1, vars.size());
		Assert.assertEquals("Bar should have a Object type, has type " + vars.getType(new SpecVar("bar")), "java.lang.Object", vars.getType(new SpecVar("bar")));				
	}

	@Test
	public void testRefEquality() throws ParseException {
		String string = "foo == bar";
		FPLParser parser = new FPLParser(string, null, null);
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a RefEquality, but is " + pred.getClass().getCanonicalName(), pred instanceof ReferenceEqualityPredicate);
		
		ReferenceEqualityPredicate val = (ReferenceEqualityPredicate)pred;
		Assert.assertTrue("Parsed predicate should be positive", val.isPositive());
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain two free variable", 2, vars.size());
		Assert.assertEquals("Foo should have a Object type", "java.lang.Object", vars.getType(new SpecVar("foo")));		
		Assert.assertEquals("Bar should have a Object type", "java.lang.Object", vars.getType(new SpecVar("bar")));		
	}
	
	@Test
	public void testRefInequality() throws ParseException {
		String string = "foo != bar";
		FPLParser parser = new FPLParser(string, null, null);
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a RefEquality, but is " + pred.getClass().getCanonicalName(), pred instanceof ReferenceEqualityPredicate);
		
		ReferenceEqualityPredicate val = (ReferenceEqualityPredicate)pred;
		Assert.assertTrue("Parsed predicate should be negative", !val.isPositive());
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain two free variable", 2, vars.size());
		Assert.assertEquals("Foo should have a Object type", "java.lang.Object", vars.getType(new SpecVar("foo")));		
		Assert.assertEquals("Bar should have a Object type", "java.lang.Object", vars.getType(new SpecVar("bar")));		
	}
}
