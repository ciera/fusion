package edu.cmu.cs.fusion.test.parsers.predicate;

import org.junit.Assert;
import org.junit.Test;

import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.RelationsEnvironment;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.predicates.RelationshipPredicate;
import edu.cmu.cs.fusion.parsers.predicate.FPLParser;
import edu.cmu.cs.fusion.parsers.predicate.ParseException;
import edu.cmu.cs.fusion.test.StubIType;

public class ParseRelationshipPredicate {
	
	
	@Test
	public void testRelPred() throws ParseException {
		String string = "Foo(a, b)";
		RelationsEnvironment env = new RelationsEnvironment();
		env.addRelation(new Relation("Foo", new String[] {"Bar", "Baz"}));
		
		FPLParser parser = new FPLParser(string, env, new StubIType());
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a RelationshipPredicate, but is " + pred.getClass().getCanonicalName(), pred instanceof RelationshipPredicate);
		
		RelationshipPredicate val = (RelationshipPredicate)pred;
		Assert.assertTrue("Parsed predicate should be positive", val.isPositive());
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain two free variables", 2, vars.size());
		Assert.assertEquals("a should have a Bar type", "Bar", vars.getType(new SpecVar("a")));		
		Assert.assertEquals("b should have a Baz type", "Baz", vars.getType(new SpecVar("b")));		
	}
	
	@Test
	public void testNegRelPred() throws ParseException {
		String string = "!Foo(a, b)";
		RelationsEnvironment env = new RelationsEnvironment();
		env.addRelation(new Relation("Foo", new String[] {"Bar", "Baz"}));

		FPLParser parser = new FPLParser(string, env, new StubIType());
		Predicate pred = parser.expression();
		
		Assert.assertTrue("Parsed predicate should be a RelationshipPredicate, but is " + pred.getClass().getCanonicalName(), pred instanceof RelationshipPredicate);
		
		RelationshipPredicate val = (RelationshipPredicate)pred;
		Assert.assertTrue("Parsed predicate should be negative", !val.isPositive());
		
		FreeVars vars = val.getFreeVariables();
		Assert.assertEquals("Should only contain two free variables", 2, vars.size());
		Assert.assertEquals("a should have a Bar type", "Bar", vars.getType(new SpecVar("a")));		
		Assert.assertEquals("b should have a Baz type", "Baz", vars.getType(new SpecVar("b")));		
	}
}
