package edu.cmu.cs.fusion.test.lattice;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.BeforeClass;

import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.relationship.RelationshipContext;
import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;

public class ContextTest {
	static RelationshipContext c1, c2, c3, c4;
	static Relation tA, tB;
	
	static ObjectLabel w, x, y, z;

	@BeforeClass
	static public void testRelationships() {
		tA = new Relation("A", new String[] {"Foo", "Bar"});
		tB = new Relation("B", new String[] {"Bar", "Bar"});
		
		w = new AbstractObjectLabel("w");
		x = new AbstractObjectLabel("x");
		y = new AbstractObjectLabel("y");
		z = new AbstractObjectLabel("z");

		c1 = new RelationshipContext();
		c1.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), ThreeValue.TRUE);
		c1.setRelationship(new Relationship(tA, new ObjectLabel[] {w, z}), ThreeValue.FALSE);
		c1.setRelationship(new Relationship(tA, new ObjectLabel[] {x, y}), ThreeValue.TRUE);
		c1.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), ThreeValue.FALSE);
		c1.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), ThreeValue.TRUE);
		c1.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), ThreeValue.UNKNOWN);

		c2 = new RelationshipContext();
		c2.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), ThreeValue.TRUE);
		c2.setRelationship(new Relationship(tA, new ObjectLabel[] {w, z}), ThreeValue.FALSE);
		c2.setRelationship(new Relationship(tA, new ObjectLabel[] {x, y}), ThreeValue.FALSE);
		c2.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), ThreeValue.TRUE);
		c2.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), ThreeValue.UNKNOWN);
		c2.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), ThreeValue.UNKNOWN);

		c3 = new RelationshipContext();
		c3.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), ThreeValue.TRUE);
		c3.setRelationship(new Relationship(tA, new ObjectLabel[] {w, z}), ThreeValue.FALSE);
		c3.setRelationship(new Relationship(tA, new ObjectLabel[] {x, y}), ThreeValue.UNKNOWN);
		c3.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), ThreeValue.UNKNOWN);
		c3.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), ThreeValue.UNKNOWN);
		c3.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), ThreeValue.UNKNOWN);

		c4 = new RelationshipContext();
		c4.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), ThreeValue.TRUE);
		c4.setRelationship(new Relationship(tA, new ObjectLabel[] {w, z}), ThreeValue.UNKNOWN);
		c4.setRelationship(new Relationship(tA, new ObjectLabel[] {x, y}), ThreeValue.FALSE);
		c4.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), ThreeValue.UNKNOWN);
		c4.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), ThreeValue.UNKNOWN);
		c4.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), ThreeValue.UNKNOWN);
	
}
	
	@Test
	public void testJoinSame() {
		RelationshipContext joined = c1.join(c2);
		assertTrue(joined.getRelationship(new Relationship(tA, new ObjectLabel[] {w, y})) == ThreeValue.TRUE);
		assertTrue(joined.getRelationship(new Relationship(tA, new ObjectLabel[] {w, z})) == ThreeValue.FALSE);
		assertTrue(joined.getRelationship(new Relationship(tA, new ObjectLabel[] {x, y})) == ThreeValue.UNKNOWN);
		assertTrue(joined.getRelationship(new Relationship(tA, new ObjectLabel[] {x, z})) == ThreeValue.UNKNOWN);
		assertTrue(joined.getRelationship(new Relationship(tB, new ObjectLabel[] {y, z})) == ThreeValue.UNKNOWN);
		assertTrue(joined.getRelationship(new Relationship(tB, new ObjectLabel[] {z, y})) == ThreeValue.UNKNOWN);
	}
	
	@Test
	public void testJoinMorePrecise() {
		RelationshipContext joined = c2.join(c3);
		assertTrue(joined.equals(c3));
	}
	
	@Test
	public void testIsMorePreciseOrEqualTo() {
		assertTrue(c1.isMorePreciseOrEqualTo(c3));
		assertTrue(c2.isMorePreciseOrEqualTo(c3));
		assertTrue(c2.isMorePreciseOrEqualTo(c4));
		assertTrue(c4.isMorePreciseOrEqualTo(c4));
		assertTrue(c2.isMorePreciseOrEqualTo(c2));
		assertTrue(!c1.isMorePreciseOrEqualTo(c4));
		assertTrue(!c3.isMorePreciseOrEqualTo(c2));
		assertTrue(!c2.isMorePreciseOrEqualTo(c1));
	}

	@Test
	public void testIsStrictlyMorePrecise() {
		assertTrue(c1.isStrictlyMorePrecise(c3));
		assertTrue(c2.isStrictlyMorePrecise(c3));
		assertTrue(c2.isStrictlyMorePrecise(c4));
		assertTrue(!c4.isStrictlyMorePrecise(c4));
		assertTrue(!c1.isStrictlyMorePrecise(c1));
		assertTrue(!c1.isStrictlyMorePrecise(c4));
		assertTrue(!c3.isStrictlyMorePrecise(c2));
		assertTrue(!c2.isStrictlyMorePrecise(c1));
	}
}
