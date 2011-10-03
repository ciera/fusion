package edu.cmu.cs.fusion.test.parsers.predicate;

import org.junit.Assert;
import org.junit.Test;

import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.predicates.BooleanValue;
import edu.cmu.cs.fusion.constraint.predicates.FalsePredicate;
import edu.cmu.cs.fusion.constraint.predicates.TruePredicate;
import edu.cmu.cs.fusion.parsers.predicate.FPLParser;
import edu.cmu.cs.fusion.parsers.predicate.ParseException;

public class ParseSimplePredicate {
	@Test
	public void testTrueParses() throws ParseException {
		String string = "TRUE";
		FPLParser parser = new FPLParser(string, null, null);
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a TruePredicate, but is " + pred.getClass().getCanonicalName(), pred instanceof TruePredicate);		
	}

	@Test
	public void testFalseParses() throws ParseException {
		String string = "FALSE";
		FPLParser parser = new FPLParser(string, null, null);
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a FalsePredicate, but is " + pred.getClass().getCanonicalName(), pred instanceof FalsePredicate);
		
	}

	@Test
	public void testBoolParses() throws ParseException {
		String string = "foo";
		FPLParser parser = new FPLParser(string, null, null);
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a BooleanValue, but is " + pred.getClass().getCanonicalName(), pred instanceof BooleanValue);
		
		BooleanValue val = (BooleanValue)pred;
		Assert.assertTrue("Parsed predicate should be positive", val.isPositive());
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain one free variable", 1, vars.size());
		Assert.assertEquals("Foo should have a boolean type", "boolean", vars.getType(new SpecVar("foo")));
	}

	@Test
	public void testNegBoolParses() throws ParseException {
		String string = "!foo";
		FPLParser parser = new FPLParser(string, null, null);
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a BooleanValue, but is " + pred.getClass().getCanonicalName(), pred instanceof BooleanValue);
		
		BooleanValue val = (BooleanValue)pred;
		Assert.assertTrue("Parsed predicate should be negative", !val.isPositive());
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain one free variable", 1, vars.size());
		Assert.assertEquals("Foo should have a boolean type", "boolean", vars.getType(new SpecVar("foo")));
	}
	
	@Test(expected=ParseException.class)
	public void badParse() throws ParseException {
		String string = "!";
		FPLParser parser = new FPLParser(string, null, null);
		parser.expression();
	}
}
