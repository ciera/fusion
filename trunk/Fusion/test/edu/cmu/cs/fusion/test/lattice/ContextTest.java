package edu.cmu.cs.fusion.test.lattice;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.relationship.FourPointLattice;
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
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FourPointLattice.TRU);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, z}), FourPointLattice.FAL);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {x, y}), FourPointLattice.TRU);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), FourPointLattice.FAL);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FourPointLattice.TRU);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FourPointLattice.UNK);
		c1 = new RelationshipContext(true).applyChangesFromDelta(delta);
		
		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FourPointLattice.TRU);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, z}), FourPointLattice.FAL);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {x, y}), FourPointLattice.FAL);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), FourPointLattice.TRU);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FourPointLattice.UNK);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FourPointLattice.UNK);
		c2 = new RelationshipContext(true).applyChangesFromDelta(delta);

		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FourPointLattice.TRU);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, z}), FourPointLattice.FAL);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {x, y}), FourPointLattice.UNK);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), FourPointLattice.UNK);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FourPointLattice.UNK);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FourPointLattice.UNK);
		c3 = new RelationshipContext(true).applyChangesFromDelta(delta);

		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FourPointLattice.TRU);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, z}), FourPointLattice.UNK);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {x, y}), FourPointLattice.FAL);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), FourPointLattice.UNK);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FourPointLattice.UNK);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FourPointLattice.UNK);
		c4 = new RelationshipContext(true).applyChangesFromDelta(delta);
		
		
		d1 = new RelationshipDelta();
		d1.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FourPointLattice.TRU);
		d1.setRelationship(new Relationship(tA, new ObjectLabel[] {w, z}), FourPointLattice.BOT);
		d1.setRelationship(new Relationship(tA, new ObjectLabel[] {x, y}), FourPointLattice.BOT);
		d1.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), FourPointLattice.FAL);
		d1.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FourPointLattice.TRU);
		d1.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FourPointLattice.UNK);

		d2 = new RelationshipDelta();
		d2.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FourPointLattice.TRU);
		d2.setRelationship(new Relationship(tA, new ObjectLabel[] {w, z}), FourPointLattice.BOT);
		d2.setRelationship(new Relationship(tA, new ObjectLabel[] {x, y}), FourPointLattice.FAL);
		d2.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), FourPointLattice.BOT);
		d2.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FourPointLattice.UNK);
		d2.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FourPointLattice.UNK);

		d3 = new RelationshipDelta();
		d3.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FourPointLattice.TRU);
		d3.setRelationship(new Relationship(tA, new ObjectLabel[] {w, z}), FourPointLattice.FAL);
		d3.setRelationship(new Relationship(tA, new ObjectLabel[] {x, y}), FourPointLattice.UNK);
		d3.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), FourPointLattice.UNK);
		d3.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FourPointLattice.UNK);
		d3.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FourPointLattice.UNK);

		d4 = new RelationshipDelta();
		d4.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FourPointLattice.BOT);
		d4.setRelationship(new Relationship(tA, new ObjectLabel[] {w, z}), FourPointLattice.UNK);
		d4.setRelationship(new Relationship(tA, new ObjectLabel[] {x, y}), FourPointLattice.TRU);
		d4.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), FourPointLattice.BOT);
		d4.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FourPointLattice.BOT);
		d4.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FourPointLattice.BOT);
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
		assertTrue(applied.getRelationship(new Relationship(tB, new ObjectLabel[] {y, z})) == ThreeValue.TRUE);
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
		
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FourPointLattice.BOT);
		changes = b1.applyChangesFromDelta(delta);
		assertTrue(b1.isMorePreciseOrEqualTo(changes));
		assertTrue(changes.isMorePreciseOrEqualTo(b1));
	
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FourPointLattice.UNK);
		changes = b1.applyChangesFromDelta(delta);
		assertTrue(b1.isMorePreciseOrEqualTo(changes));
		assertTrue(!changes.isMorePreciseOrEqualTo(b1));
	}
}
