package edu.cmu.cs.fusion.test.parsers.predicate;

import org.junit.Assert;
import org.junit.Test;

import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.RelationsEnvironment;
import edu.cmu.cs.fusion.constraint.Effect;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.parsers.predicate.FPLParser;
import edu.cmu.cs.fusion.parsers.predicate.ParseException;
import edu.cmu.cs.fusion.test.StubIType;

public class ParseEffect {
	
	@Test
	public void testAddEffect() throws ParseException {
		String string = "Foo(a, b)";
		RelationsEnvironment env = new RelationsEnvironment();
		env.addRelation(new Relation("Foo", new String[] {"Bar", "Baz"}));
		
		FPLParser parser = new FPLParser(string, env, new StubIType());
		Effect effect = parser.effect();
		
		Assert.assertEquals("Effect type is wrong", Effect.EffectType.ADD, effect.getType());
		Assert.assertEquals("Effect relation is wrong", "Foo", effect.getRelation().getName());
		
		FreeVars vars = effect.getFreeVariables();
		Assert.assertEquals("Should only contain two free variables", 2, vars.size());
		Assert.assertEquals("a should have a Bar type", "Bar", vars.getType(new SpecVar("a")));		
		Assert.assertEquals("b should have a Baz type", "Baz", vars.getType(new SpecVar("b")));		
	}
	
	@Test
	public void testRemoveEffect() throws ParseException {
		String string = "!Foo(a, b)";
		RelationsEnvironment env = new RelationsEnvironment();
		env.addRelation(new Relation("Foo", new String[] {"Bar", "Baz"}));
		
		FPLParser parser = new FPLParser(string, env, new StubIType());
		Effect effect = parser.effect();
		
		Assert.assertEquals("Effect type is wrong", Effect.EffectType.REMOVE, effect.getType());
		Assert.assertEquals("Effect relation is wrong", "Foo", effect.getRelation().getName());
		
		FreeVars vars = effect.getFreeVariables();
		Assert.assertEquals("Should only contain two free variables", 2, vars.size());
		Assert.assertEquals("a should have a Bar type", "Bar", vars.getType(new SpecVar("a")));		
		Assert.assertEquals("b should have a Baz type", "Baz", vars.getType(new SpecVar("b")));		
	}

	@Test
	public void testTestEffect() throws ParseException {
		String string = "?Foo(a, b):c";
		RelationsEnvironment env = new RelationsEnvironment();
		env.addRelation(new Relation("Foo", new String[] {"Bar", "Baz"}));
		
		FPLParser parser = new FPLParser(string, env, new StubIType());
		Effect effect = parser.effect();
		
		Assert.assertEquals("Effect type is wrong", Effect.EffectType.TEST, effect.getType());
		Assert.assertEquals("Effect relation is wrong", "Foo", effect.getRelation().getName());
		
		FreeVars vars = effect.getFreeVariables();
		Assert.assertEquals("Should only contain two free variables", 3, vars.size());
		Assert.assertEquals("a should have a Bar type", "Bar", vars.getType(new SpecVar("a")));		
		Assert.assertEquals("b should have a Baz type", "Baz", vars.getType(new SpecVar("b")));		
		Assert.assertEquals("c should have a boolean type", "boolean", vars.getType(new SpecVar("c")));		
	}

	@Test
	public void testNegTestEffect() throws ParseException {
		String string = "?!Foo(a, b):c";
		RelationsEnvironment env = new RelationsEnvironment();
		env.addRelation(new Relation("Foo", new String[] {"Bar", "Baz"}));
		
		FPLParser parser = new FPLParser(string, env, new StubIType());
		Effect effect = parser.effect();
		
		Assert.assertEquals("Effect type is wrong", Effect.EffectType.NEG_TEST, effect.getType());
		Assert.assertEquals("Effect relation is wrong", "Foo", effect.getRelation().getName());
		
		FreeVars vars = effect.getFreeVariables();
		Assert.assertEquals("Should only contain two free variables", 3, vars.size());
		Assert.assertEquals("a should have a Bar type", "Bar", vars.getType(new SpecVar("a")));		
		Assert.assertEquals("b should have a Baz type", "Baz", vars.getType(new SpecVar("b")));		
		Assert.assertEquals("c should have a boolean type", "boolean", vars.getType(new SpecVar("c")));		
	}


}
