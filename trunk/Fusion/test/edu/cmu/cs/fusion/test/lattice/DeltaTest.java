package edu.cmu.cs.fusion.test.lattice;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.relationship.FourPointLattice;
import edu.cmu.cs.fusion.relationship.RelationshipContext;
import edu.cmu.cs.fusion.relationship.RelationshipDelta;

public class DeltaTest {
	static RelationshipDelta d1, d2, d3, d4;
	
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
	public void testPolarize() {
		RelationshipDelta polar = d1.polarize();
		
		assertTrue(polar.getValue(new Relationship(tA, new ObjectLabel[] {w, y})) == FourPointLattice.UNK);
		assertTrue(polar.getValue(new Relationship(tA, new ObjectLabel[] {w, z})) == FourPointLattice.BOT);
		assertTrue(polar.getValue(new Relationship(tA, new ObjectLabel[] {x, y})) == FourPointLattice.BOT);
		assertTrue(polar.getValue(new Relationship(tA, new ObjectLabel[] {x, z})) == FourPointLattice.UNK);
		assertTrue(polar.getValue(new Relationship(tB, new ObjectLabel[] {y, z})) == FourPointLattice.UNK);
		assertTrue(polar.getValue(new Relationship(tB, new ObjectLabel[] {z, y})) == FourPointLattice.UNK);
	}
	
	@Test
	public void testJoin() {
		RelationshipDelta join;
		List<RelationshipDelta> list;
				
		list = new LinkedList<RelationshipDelta>();
		list.add(d1);
		list.add(d2);
		join = RelationshipDelta.join(list);
		
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {w, y})) == FourPointLattice.TRU);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {w, z})) == FourPointLattice.BOT);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {x, y})) == FourPointLattice.FAL);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {x, z})) == FourPointLattice.FAL);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {y, z})) == FourPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {z, y})) == FourPointLattice.UNK);

		list = new LinkedList<RelationshipDelta>();
		list.add(d2);
		list.add(d4);
		join = RelationshipDelta.join(list);
		
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {w, y})) == FourPointLattice.TRU);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {w, z})) == FourPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {x, y})) == FourPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {x, z})) == FourPointLattice.BOT);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {y, z})) == FourPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {z, y})) == FourPointLattice.UNK);

		list = new LinkedList<RelationshipDelta>();
		list.add(d1);
		list.add(d2);
		list.add(d3);
		list.add(d4);
		join = RelationshipDelta.join(list);
		
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {w, y})) == FourPointLattice.TRU);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {w, z})) == FourPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {x, y})) == FourPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {x, z})) == FourPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {y, z})) == FourPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {z, y})) == FourPointLattice.UNK);
	}

	@Test
	public void testEqualityJoin() {
		RelationshipDelta join;
		List<RelationshipDelta> list;
				
		list = new LinkedList<RelationshipDelta>();
		list.add(d1);
		list.add(d2);
		join = RelationshipDelta.equalityJoin(list);
		
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {w, y})) == FourPointLattice.TRU);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {w, z})) == FourPointLattice.BOT);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {x, y})) == FourPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {x, z})) == FourPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {y, z})) == FourPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {z, y})) == FourPointLattice.UNK);

		list = new LinkedList<RelationshipDelta>();
		list.add(d2);
		list.add(d4);
		join = RelationshipDelta.equalityJoin(list);
		
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {w, y})) == FourPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {w, z})) == FourPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {x, y})) == FourPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {x, z})) == FourPointLattice.BOT);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {y, z})) == FourPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {z, y})) == FourPointLattice.UNK);

		list = new LinkedList<RelationshipDelta>();
		list.add(d1);
		list.add(d2);
		list.add(d3);
		list.add(d4);
		join = RelationshipDelta.equalityJoin(list);
		
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {w, y})) == FourPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {w, z})) == FourPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {x, y})) == FourPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {x, z})) == FourPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {y, z})) == FourPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {z, y})) == FourPointLattice.UNK);
	}
	
	@Test
	public void testStrictPrecise() {
		RelationshipContext ctx = new RelationshipContext(false).applyChangesFromDelta(d1);
		RelationshipDelta delta;
		
		//test the empty delta
		delta = new RelationshipDelta();
		assertTrue(!delta.isStrictlyMorePrecise(ctx));
		
		//test an equal delta
		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FourPointLattice.TRU);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, z}), FourPointLattice.BOT);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {x, y}), FourPointLattice.BOT);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), FourPointLattice.FAL);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FourPointLattice.TRU);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FourPointLattice.UNK);
		assertTrue(!delta.isStrictlyMorePrecise(ctx));
		
		//test a more precise delta
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FourPointLattice.TRU);
		assertTrue(delta.isStrictlyMorePrecise(ctx));
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FourPointLattice.UNK);
		
		//test a more precise delta, BUT BOTTOM
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FourPointLattice.BOT);
		assertTrue(!delta.isStrictlyMorePrecise(ctx));
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FourPointLattice.TRU);

		//test a more precise delta, BUT BOTTOM on unknown
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, z}), FourPointLattice.TRU);
		assertTrue(delta.isStrictlyMorePrecise(ctx));
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, z}), FourPointLattice.BOT);

		//test a more and less precise delta
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FourPointLattice.UNK);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {z, y}), FourPointLattice.TRU);
		assertTrue(!delta.isStrictlyMorePrecise(ctx));
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FourPointLattice.TRU);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FourPointLattice.UNK);

		//test a less precise U delta
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FourPointLattice.UNK);
		assertTrue(!delta.isStrictlyMorePrecise(ctx));
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FourPointLattice.TRU);
		
		//test a thrashing delta
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FourPointLattice.FAL);
		assertTrue(!delta.isStrictlyMorePrecise(ctx));
	}

}
