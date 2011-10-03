package edu.cmu.cs.fusion.test.parsers.predicate;

import org.junit.Assert;
import org.junit.Test;

import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.RelationsEnvironment;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.RelEffect;
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
		RelEffect effect = parser.relEffect();
		
		Assert.assertEquals("Effect type is wrong", RelEffect.EffectType.ADD, effect.getType());
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
		RelEffect effect = parser.relEffect();
		
		Assert.assertEquals("Effect type is wrong", RelEffect.EffectType.REMOVE, effect.getType());
		Assert.assertEquals("Effect relation is wrong", "Foo", effect.getRelation().getName());
		
		FreeVars vars = effect.getFreeVariables();
		Assert.assertEquals("Should only contain two free variables", 2, vars.size());
		Assert.assertEquals("a should have a Bar type", "Bar", vars.getType(new SpecVar("a")));		
		Assert.assertEquals("b should have a Baz type", "Baz", vars.getType(new SpecVar("b")));		
	}
	
	@Test
	public void testRemoveManyEffect() throws ParseException {
		String string = "!Foo(*, b)";
		RelationsEnvironment env = new RelationsEnvironment();
		env.addRelation(new Relation("Foo", new String[] {"Bar", "Baz"}));
		
		FPLParser parser = new FPLParser(string, env, new StubIType());
		RelEffect effect = parser.relEffect();
		
		Assert.assertEquals("Effect type is wrong", RelEffect.EffectType.REMOVE, effect.getType());
		Assert.assertEquals("Effect relation is wrong", "Foo", effect.getRelation().getName());
		
		FreeVars vars = effect.getFreeVariables();
		FreeVars wcs = effect.getWildCards();
		Assert.assertEquals("Should only contain one free variable", 1, vars.size());
		Assert.assertEquals("But actually has two!", 2, effect.getVars().length);	
		Assert.assertEquals("one wildcard", 1, wcs.size());
		Assert.assertTrue("The first param should be a wildcard!", effect.getVars()[0].isWildCard());		
		Assert.assertEquals("wild card should have a Bar type", "Bar", wcs.getType(effect.getVars()[0]));		
		Assert.assertTrue("The second param should be normal", !effect.getVars()[1].isWildCard());
		Assert.assertEquals("b should have a Baz type", "Baz", vars.getType(new SpecVar("b")));		
	}


	@Test
	public void testTestEffect() throws ParseException {
		String string = "?Foo(a, b):c";
		RelationsEnvironment env = new RelationsEnvironment();
		env.addRelation(new Relation("Foo", new String[] {"Bar", "Baz"}));
		
		FPLParser parser = new FPLParser(string, env, new StubIType());
		RelEffect effect = parser.relEffect();
		
		Assert.assertEquals("Effect type is wrong", RelEffect.EffectType.TEST, effect.getType());
		Assert.assertEquals("Effect relation is wrong", "Foo", effect.getRelation().getName());
		
		FreeVars vars = effect.getFreeVariables();
		Assert.assertEquals("Should only contain three free variables", 3, vars.size());
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
		RelEffect effect = parser.relEffect();
		
		Assert.assertEquals("Effect type is wrong", RelEffect.EffectType.NEG_TEST, effect.getType());
		Assert.assertEquals("Effect relation is wrong", "Foo", effect.getRelation().getName());
		
		FreeVars vars = effect.getFreeVariables();
		Assert.assertEquals("Should only contain three free variables", 3, vars.size());
		Assert.assertEquals("a should have a Bar type", "Bar", vars.getType(new SpecVar("a")));		
		Assert.assertEquals("b should have a Baz type", "Baz", vars.getType(new SpecVar("b")));		
		Assert.assertEquals("c should have a boolean type", "boolean", vars.getType(new SpecVar("c")));		
	}
}
