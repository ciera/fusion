package edu.cmu.cs.fusion.test.parsers.predicate;

import org.junit.Assert;
import org.junit.Test;

import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.predicates.AndPredicate;
import edu.cmu.cs.fusion.constraint.predicates.BooleanValue;
import edu.cmu.cs.fusion.constraint.predicates.ImpliesPredicate;
import edu.cmu.cs.fusion.constraint.predicates.OrPredicate;
import edu.cmu.cs.fusion.parsers.predicate.FPLParser;
import edu.cmu.cs.fusion.parsers.predicate.ParseException;

public class ParseBinaryPredicate {
	@Test
	public void testAndPredicate() throws ParseException {
		String string = "a AND b";
		
		FPLParser parser = new FPLParser(string, null, null);
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a AndPredicate, but is " + pred.getClass().getCanonicalName(), pred instanceof AndPredicate);
		
		AndPredicate val = (AndPredicate)pred;
		
		Assert.assertTrue("Left side is a bool value", val.getLeft() instanceof BooleanValue);
		Assert.assertTrue("Right side is a bool value", val.getRight() instanceof BooleanValue);
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain two free variables", 2, vars.size());

		vars = val.getLeft().getFreeVariables();
		Assert.assertEquals("Should only contain one free variables", 1, vars.size());
		Assert.assertEquals("a should have a boolean type", "boolean", vars.getType(new SpecVar("a")));		

		vars = val.getRight().getFreeVariables();
		Assert.assertEquals("Should only contain one free variables", 1, vars.size());
		Assert.assertEquals("b should have a boolean type", "boolean", vars.getType(new SpecVar("b")));		
	}
	
	@Test
	public void testAnd2Predicate() throws ParseException {
		String string = "a AND b AND c";
		
		FPLParser parser = new FPLParser(string, null, null);
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a AndPredicate, but is " + pred.getClass().getCanonicalName(), pred instanceof AndPredicate);
		
		AndPredicate val = (AndPredicate)pred;
		
		Assert.assertTrue("Left side is a and pred", val.getLeft() instanceof AndPredicate);
		Assert.assertTrue("Right side is a bool value", val.getRight() instanceof BooleanValue);
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain three free variables", 3, vars.size());

		vars = val.getLeft().getFreeVariables();
		Assert.assertEquals("Should only contain two free variables", 2, vars.size());
		Assert.assertEquals("a should have a boolean type", "boolean", vars.getType(new SpecVar("a")));		
		Assert.assertEquals("b should have a boolean type", "boolean", vars.getType(new SpecVar("b")));		

		vars = val.getRight().getFreeVariables();
		Assert.assertEquals("Should only contain one free variables", 1, vars.size());
		Assert.assertEquals("c should have a boolean type", "boolean", vars.getType(new SpecVar("c")));		
	}
	
	@Test
	public void testAnd3ParenPredicate() throws ParseException {
		String string = "a AND (b AND (c AND d))";
		
		FPLParser parser = new FPLParser(string, null, null);
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a AndPredicate, but is " + pred.getClass().getCanonicalName(), pred instanceof AndPredicate);		
		AndPredicate val = (AndPredicate)pred;
		
		Assert.assertTrue("Left side is a bool value", val.getLeft() instanceof BooleanValue);
		Assert.assertTrue("Right side is a and pred", val.getRight() instanceof AndPredicate);
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain 4 free variables", 4, vars.size());

		vars = val.getLeft().getFreeVariables();
		Assert.assertEquals("Should only contain one free variables", 1, vars.size());
		Assert.assertEquals("a should have a boolean type", "boolean", vars.getType(new SpecVar("a")));		

		vars = val.getRight().getFreeVariables();
		Assert.assertEquals("Should only contain 3 free variables", 3, vars.size());
		
		val = (AndPredicate)val.getRight();
		Assert.assertTrue("Left side is a bool value", val.getLeft() instanceof BooleanValue);
		Assert.assertTrue("Right side is a and pred", val.getRight() instanceof AndPredicate);
		
		vars = val.getLeft().getFreeVariables();
		Assert.assertEquals("Should only contain one free variables", 1, vars.size());
		Assert.assertEquals("b should have a boolean type", "boolean", vars.getType(new SpecVar("b")));		

		vars = val.getRight().getFreeVariables();
		Assert.assertEquals("Should only contain 2 free variables", 2, vars.size());

		val = (AndPredicate)val.getRight();
		Assert.assertTrue("Left side is a bool value", val.getLeft() instanceof BooleanValue);
		Assert.assertTrue("Right side is a bool value", val.getRight() instanceof BooleanValue);

		vars = val.getLeft().getFreeVariables();
		Assert.assertEquals("Should only contain one free variables", 1, vars.size());
		Assert.assertEquals("c should have a boolean type", "boolean", vars.getType(new SpecVar("c")));		

		vars = val.getRight().getFreeVariables();
		Assert.assertEquals("Should only contain one free variables", 1, vars.size());
		Assert.assertEquals("d should have a boolean type", "boolean", vars.getType(new SpecVar("d")));		

	}

	
	@Test
	public void testOrAndPredicate() throws ParseException {
		String string = "c OR b AND a";
		
		FPLParser parser = new FPLParser(string, null, null);
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a OrPredicate, but is " + pred.getClass().getCanonicalName(), pred instanceof OrPredicate);
		
		OrPredicate val = (OrPredicate)pred;
		
		Assert.assertTrue("Left side is a bool value", val.getLeft() instanceof BooleanValue);
		Assert.assertTrue("Right side is a and pred", val.getRight() instanceof AndPredicate);
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain three free variables", 3, vars.size());

		vars = val.getLeft().getFreeVariables();
		Assert.assertEquals("Should only contain one free variables", 1, vars.size());
		Assert.assertEquals("c should have a boolean type", "boolean", vars.getType(new SpecVar("c")));		

		vars = val.getRight().getFreeVariables();
		Assert.assertEquals("Should only contain two free variables", 2, vars.size());
		Assert.assertEquals("a should have a boolean type", "boolean", vars.getType(new SpecVar("a")));		
		Assert.assertEquals("b should have a boolean type", "boolean", vars.getType(new SpecVar("b")));		

	}

	@Test
	public void testOrAndParenPredicate() throws ParseException {
		String string = "(c OR b) AND a";
		
		FPLParser parser = new FPLParser(string, null, null);
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a AndPredicate, but is " + pred.getClass().getCanonicalName(), pred instanceof AndPredicate);
		
		AndPredicate val = (AndPredicate)pred;
		
		Assert.assertTrue("Left side is a or pred", val.getLeft() instanceof OrPredicate);
		Assert.assertTrue("Right side is a bool value", val.getRight() instanceof BooleanValue);
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain three free variables", 3, vars.size());

		vars = val.getLeft().getFreeVariables();
		Assert.assertEquals("Should only contain 2 free variables", 2, vars.size());
		Assert.assertEquals("c should have a boolean type", "boolean", vars.getType(new SpecVar("c")));		
		Assert.assertEquals("b should have a boolean type", "boolean", vars.getType(new SpecVar("b")));		

		vars = val.getRight().getFreeVariables();
		Assert.assertEquals("Should only contain 1 free variables", 1, vars.size());
		Assert.assertEquals("a should have a boolean type", "boolean", vars.getType(new SpecVar("a")));		
	}


	@Test
	public void testOrPredicate() throws ParseException {
		String string = "a OR b";
		
		FPLParser parser = new FPLParser(string, null, null);
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a OrPredicate, but is " + pred.getClass().getCanonicalName(), pred instanceof OrPredicate);
		
		OrPredicate val = (OrPredicate)pred;
		
		Assert.assertTrue("Left side is a bool value", val.getLeft() instanceof BooleanValue);
		Assert.assertTrue("Right side is a bool value", val.getRight() instanceof BooleanValue);
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain two free variables", 2, vars.size());

		vars = val.getLeft().getFreeVariables();
		Assert.assertEquals("Should only contain one free variables", 1, vars.size());
		Assert.assertEquals("a should have a boolean type", "boolean", vars.getType(new SpecVar("a")));		

		vars = val.getRight().getFreeVariables();
		Assert.assertEquals("Should only contain one free variables", 1, vars.size());
		Assert.assertEquals("b should have a boolean type", "boolean", vars.getType(new SpecVar("b")));		
	}
	
	@Test
	public void testOr2Predicate() throws ParseException {
		String string = "a OR b OR c";
		
		FPLParser parser = new FPLParser(string, null, null);
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a OrPredicate, but is " + pred.getClass().getCanonicalName(), pred instanceof OrPredicate);
		
		OrPredicate val = (OrPredicate)pred;
		
		Assert.assertTrue("Left side is a or pred", val.getLeft() instanceof OrPredicate);
		Assert.assertTrue("Right side is a bool value", val.getRight() instanceof BooleanValue);
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain three free variables", 3, vars.size());

		vars = val.getLeft().getFreeVariables();
		Assert.assertEquals("Should only contain two free variables", 2, vars.size());
		Assert.assertEquals("a should have a boolean type", "boolean", vars.getType(new SpecVar("a")));		
		Assert.assertEquals("b should have a boolean type", "boolean", vars.getType(new SpecVar("b")));		

		vars = val.getRight().getFreeVariables();
		Assert.assertEquals("Should only contain one free variables", 1, vars.size());
		Assert.assertEquals("c should have a boolean type", "boolean", vars.getType(new SpecVar("c")));		
	}

	@Test
	public void testImpliesOrPredicate() throws ParseException {
		String string = "c IMPLIES b OR a";
		
		FPLParser parser = new FPLParser(string, null, null);
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a ImpliesPredicate, but is " + pred.getClass().getCanonicalName(), pred instanceof ImpliesPredicate);
		
		ImpliesPredicate val = (ImpliesPredicate)pred;
		
		Assert.assertTrue("Left side is a bool value", val.getLeft() instanceof BooleanValue);
		Assert.assertTrue("Right side is a or pred", val.getRight() instanceof OrPredicate);
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain three free variables", 3, vars.size());

		vars = val.getLeft().getFreeVariables();
		Assert.assertEquals("Should only contain one free variables", 1, vars.size());
		Assert.assertEquals("c should have a boolean type", "boolean", vars.getType(new SpecVar("c")));		

		vars = val.getRight().getFreeVariables();
		Assert.assertEquals("Should only contain two free variables", 2, vars.size());
		Assert.assertEquals("a should have a boolean type", "boolean", vars.getType(new SpecVar("a")));		
		Assert.assertEquals("b should have a boolean type", "boolean", vars.getType(new SpecVar("b")));		

	}
	
	@Test
	public void testImpliesOrParenPredicate() throws ParseException {
		String string = "(c IMPLIES b) OR a";
		
		FPLParser parser = new FPLParser(string, null, null);
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a ImpliesPredicate, but is " + pred.getClass().getCanonicalName(), pred instanceof OrPredicate);
		
		OrPredicate val = (OrPredicate)pred;
		
		Assert.assertTrue("Left side is a implies pred", val.getLeft() instanceof ImpliesPredicate);
		Assert.assertTrue("Right side is a bool value", val.getRight() instanceof BooleanValue);
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain three free variables", 3, vars.size());

		vars = val.getLeft().getFreeVariables();
		Assert.assertEquals("Should only contain two free variables", 2, vars.size());
		Assert.assertEquals("c should have a boolean type", "boolean", vars.getType(new SpecVar("c")));		
		Assert.assertEquals("b should have a boolean type", "boolean", vars.getType(new SpecVar("b")));		

		vars = val.getRight().getFreeVariables();
		Assert.assertEquals("Should only contain 1 free variables", 1, vars.size());
		Assert.assertEquals("a should have a boolean type", "boolean", vars.getType(new SpecVar("a")));		
	}


	@Test
	public void testImpliesPredicate() throws ParseException {
		String string = "a IMPLIES b";
		
		FPLParser parser = new FPLParser(string, null, null);
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a ImpliesPredicate, but is " + pred.getClass().getCanonicalName(), pred instanceof ImpliesPredicate);
		
		ImpliesPredicate val = (ImpliesPredicate)pred;
		
		Assert.assertTrue("Left side is a bool value", val.getLeft() instanceof BooleanValue);
		Assert.assertTrue("Right side is a bool value", val.getRight() instanceof BooleanValue);
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain two free variables", 2, vars.size());

		vars = val.getLeft().getFreeVariables();
		Assert.assertEquals("Should only contain one free variables", 1, vars.size());
		Assert.assertEquals("a should have a boolean type", "boolean", vars.getType(new SpecVar("a")));		

		vars = val.getRight().getFreeVariables();
		Assert.assertEquals("Should only contain one free variables", 1, vars.size());
		Assert.assertEquals("b should have a boolean type", "boolean", vars.getType(new SpecVar("b")));		
	}

	@Test
	public void testImplies2Predicate() throws ParseException {
		String string = "a IMPLIES b IMPLIES c";
		
		FPLParser parser = new FPLParser(string, null, null);
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a ImpliesPredicate, but is " + pred.getClass().getCanonicalName(), pred instanceof ImpliesPredicate);
		
		ImpliesPredicate val = (ImpliesPredicate)pred;
		
		Assert.assertTrue("Left side is a implies pred", val.getLeft() instanceof ImpliesPredicate);
		Assert.assertTrue("Right side is a bool value", val.getRight() instanceof BooleanValue);
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain three free variables", 3, vars.size());

		vars = val.getLeft().getFreeVariables();
		Assert.assertEquals("Should only contain two free variables", 2, vars.size());
		Assert.assertEquals("a should have a boolean type", "boolean", vars.getType(new SpecVar("a")));		
		Assert.assertEquals("b should have a boolean type", "boolean", vars.getType(new SpecVar("b")));		

		vars = val.getRight().getFreeVariables();
		Assert.assertEquals("Should only contain one free variables", 1, vars.size());
		Assert.assertEquals("c should have a boolean type", "boolean", vars.getType(new SpecVar("c")));		
	}

	@Test
	public void testImpliesAndPredicate() throws ParseException {
		String string = "c IMPLIES b AND a";
		
		FPLParser parser = new FPLParser(string, null, null);
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a ImpliesPredicate, but is " + pred.getClass().getCanonicalName(), pred instanceof ImpliesPredicate);
		
		ImpliesPredicate val = (ImpliesPredicate)pred;
		
		Assert.assertTrue("Left side is a bool value", val.getLeft() instanceof BooleanValue);
		Assert.assertTrue("Right side is a and pred", val.getRight() instanceof AndPredicate);
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain three free variables", 3, vars.size());

		vars = val.getLeft().getFreeVariables();
		Assert.assertEquals("Should only contain one free variables", 1, vars.size());
		Assert.assertEquals("c should have a boolean type", "boolean", vars.getType(new SpecVar("c")));		

		vars = val.getRight().getFreeVariables();
		Assert.assertEquals("Should only contain two free variables", 2, vars.size());
		Assert.assertEquals("a should have a boolean type", "boolean", vars.getType(new SpecVar("a")));		
		Assert.assertEquals("b should have a boolean type", "boolean", vars.getType(new SpecVar("b")));		

	}

}
