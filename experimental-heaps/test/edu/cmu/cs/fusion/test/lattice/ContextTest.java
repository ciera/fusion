package edu.cmu.cs.fusion.test.lattice;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.alias.ObjectLabel;
import edu.cmu.cs.fusion.relationship.FivePointLattice;
import edu.cmu.cs.fusion.relationship.RelationshipContext;
import edu.cmu.cs.fusion.relationship.RelationshipDelta;

public class ContextTest {
	static RelationshipDelta d1, d2, d3, d4;
	static RelationshipContext c1, c2, c3, c4;
	static Relation tA, tB;
	
	static ObjectLabel w, x, y, z;

	@BeforeClass
	static public void testRelationships() {
		RelationshipDelta delta;
		tA = new Relation("A", new String[] {"Foo", "Bar"});
		tB = new Relation("B", new String[] {"Bar", "Bar"});
		
		w = new AbstractObjectLabel("w");
		x = new AbstractObjectLabel("x");
		y = new AbstractObjectLabel("y");
		z = new AbstractObjectLabel("z");

		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FivePointLattice.TRU);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, z}), FivePointLattice.FAL);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {x, y}), FivePointLattice.TRU);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), FivePointLattice.FAL);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FivePointLattice.TRU);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FivePointLattice.UNK);
		c1 = new RelationshipContext(true).applyChangesFromDelta(delta);
		
		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FivePointLattice.TRU);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, z}), FivePointLattice.FAL);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {x, y}), FivePointLattice.FAL);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), FivePointLattice.TRU);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FivePointLattice.UNK);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FivePointLattice.UNK);
		c2 = new RelationshipContext(true).applyChangesFromDelta(delta);

		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FivePointLattice.TRU);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, z}), FivePointLattice.FAL);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {x, y}), FivePointLattice.UNK);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), FivePointLattice.UNK);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FivePointLattice.UNK);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FivePointLattice.UNK);
		c3 = new RelationshipContext(true).applyChangesFromDelta(delta);

		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FivePointLattice.TRU);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, z}), FivePointLattice.UNK);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {x, y}), FivePointLattice.FAL);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), FivePointLattice.UNK);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FivePointLattice.UNK);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FivePointLattice.UNK);
		c4 = new RelationshipContext(true).applyChangesFromDelta(delta);
		
		
		d1 = new RelationshipDelta();
		d1.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FivePointLattice.TRU);
		d1.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), FivePointLattice.FAL);
		d1.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FivePointLattice.TRU);
		d1.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FivePointLattice.UNK);

		d2 = new RelationshipDelta();
		d2.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FivePointLattice.TRU);
		d2.setRelationship(new Relationship(tA, new ObjectLabel[] {x, y}), FivePointLattice.FAL);
		d2.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FivePointLattice.UNK);
		d2.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FivePointLattice.UNK);

		d3 = new RelationshipDelta();
		d3.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FivePointLattice.TRU_STAR);
		d3.setRelationship(new Relationship(tA, new ObjectLabel[] {w, z}), FivePointLattice.FAL);
		d3.setRelationship(new Relationship(tA, new ObjectLabel[] {x, y}), FivePointLattice.UNK);
		d3.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), FivePointLattice.UNK);
		d3.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FivePointLattice.UNK);
		d3.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FivePointLattice.UNK);

		d4 = new RelationshipDelta();
		d4.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FivePointLattice.TRU_STAR);
		d4.setRelationship(new Relationship(tA, new ObjectLabel[] {w, z}), FivePointLattice.UNK);
		d4.setRelationship(new Relationship(tA, new ObjectLabel[] {x, y}), FivePointLattice.TRU);
		d4.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), FivePointLattice.FAL_STAR);
		d4.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FivePointLattice.FAL_STAR);
		d4.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FivePointLattice.TRU_STAR);
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
	public void testJoinBottom() {
		RelationshipContext b1 = new RelationshipContext(true);
		RelationshipContext b2 = new RelationshipContext(true);
		RelationshipContext joined;
		
		joined = c2.join(b1);
		assertTrue(joined.equals(c2));
		
		joined = b1.join(c4);
		assertTrue(joined.equals(c4));
	
		joined = b1.join(b2);
		assertTrue(joined.equals(b2));
		assertTrue(joined.equals(b1));
	}
	
	@Test
	public void testBottomIsMorePrecise() {
		RelationshipContext b1 = new RelationshipContext(true);
		RelationshipContext b2 = new RelationshipContext(true);
		
		assertTrue(b1.isMorePreciseOrEqualTo(c3));
		assertTrue(!c1.isMorePreciseOrEqualTo(b2));
		assertTrue(b1.isMorePreciseOrEqualTo(b2));
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
	
	
	@Test
	public void testBottomStrictlyIsMorePrecise() {
		RelationshipContext b1 = new RelationshipContext(true);
		RelationshipContext b2 = new RelationshipContext(true);
		
		assertTrue(b1.isStrictlyMorePrecise(c3));
		assertTrue(!c1.isStrictlyMorePrecise(b2));
		assertTrue(!b1.isStrictlyMorePrecise(b2));
	}

	
	@Test
	public void testApplyChangesToContext() {
		RelationshipContext applied;
		
		applied = c1.applyChangesFromDelta(d1);
		assertTrue(applied.getRelationship(new Relationship(tA, new ObjectLabel[] {w, y})) == ThreeValue.TRUE);
		assertTrue(applied.getRelationship(new Relationship(tA, new ObjectLabel[] {w, z})) == ThreeValue.FALSE);
		assertTrue(applied.getRelationship(new Relationship(tA, new ObjectLabel[] {x, y})) == ThreeValue.TRUE);
		assertTrue(applied.getRelationship(new Relationship(tA, new ObjectLabel[] {x, z})) == ThreeValue.FALSE);
		assertTrue(applied.getRelationship(new Relationship(tB, new ObjectLabel[] {y, z})) == ThreeValue.TRUE);
		assertTrue(applied.getRelationship(new Relationship(tB, new ObjectLabel[] {z, y})) == ThreeValue.UNKNOWN);
	
		applied = c1.applyChangesFromDelta(d2);
		assertTrue(applied.getRelationship(new Relationship(tA, new ObjectLabel[] {w, y})) == ThreeValue.TRUE);
		assertTrue(applied.getRelationship(new Relationship(tA, new ObjectLabel[] {w, z})) == ThreeValue.FALSE);
		assertTrue(applied.getRelationship(new Relationship(tA, new ObjectLabel[] {x, y})) == ThreeValue.FALSE);
		assertTrue(applied.getRelationship(new Relationship(tA, new ObjectLabel[] {x, z})) == ThreeValue.FALSE);
		assertTrue(applied.getRelationship(new Relationship(tB, new ObjectLabel[] {y, z})) == ThreeValue.UNKNOWN);
		assertTrue(applied.getRelationship(new Relationship(tB, new ObjectLabel[] {z, y})) == ThreeValue.UNKNOWN);

		applied = c1.applyChangesFromDelta(d3);
		assertTrue(applied.getRelationship(new Relationship(tA, new ObjectLabel[] {w, y})) == ThreeValue.TRUE);
		assertTrue(applied.getRelationship(new Relationship(tA, new ObjectLabel[] {w, z})) == ThreeValue.FALSE);
		assertTrue(applied.getRelationship(new Relationship(tA, new ObjectLabel[] {x, y})) == ThreeValue.UNKNOWN);
		assertTrue(applied.getRelationship(new Relationship(tA, new ObjectLabel[] {x, z})) == ThreeValue.UNKNOWN);
		assertTrue(applied.getRelationship(new Relationship(tB, new ObjectLabel[] {y, z})) == ThreeValue.UNKNOWN);
		assertTrue(applied.getRelationship(new Relationship(tB, new ObjectLabel[] {z, y})) == ThreeValue.UNKNOWN);

		applied = c1.applyChangesFromDelta(d4);
		assertTrue(applied.getRelationship(new Relationship(tA, new ObjectLabel[] {w, y})) == ThreeValue.TRUE);
		assertTrue(applied.getRelationship(new Relationship(tA, new ObjectLabel[] {w, z})) == ThreeValue.UNKNOWN);
		assertTrue(applied.getRelationship(new Relationship(tA, new ObjectLabel[] {x, y})) == ThreeValue.TRUE);
		assertTrue(applied.getRelationship(new Relationship(tA, new ObjectLabel[] {x, z})) == ThreeValue.FALSE);
		assertTrue(applied.getRelationship(new Relationship(tB, new ObjectLabel[] {y, z})) == ThreeValue.UNKNOWN);
		assertTrue(applied.getRelationship(new Relationship(tB, new ObjectLabel[] {z, y})) == ThreeValue.UNKNOWN);
	}

	@Test
	public void testApplyChangesToContextBottom() {
		RelationshipContext b1 = new RelationshipContext(true);
		RelationshipDelta delta = new RelationshipDelta();
		RelationshipContext changes;
		
		changes = b1.applyChangesFromDelta(delta);
		assertTrue(b1.isMorePreciseOrEqualTo(changes));
		assertTrue(changes.isMorePreciseOrEqualTo(b1));
		
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FivePointLattice.UNK);
		changes = b1.applyChangesFromDelta(delta);
		assertTrue(b1.isMorePreciseOrEqualTo(changes));
		assertTrue(!changes.isMorePreciseOrEqualTo(b1));
	}
}
