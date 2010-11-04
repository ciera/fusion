package edu.cmu.cs.fusion.test.lattice;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.alias.ObjectLabel;
import edu.cmu.cs.fusion.relationship.SevenPointLattice;
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
		d1.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), SevenPointLattice.TRU);
		d1.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), SevenPointLattice.FAL);
		d1.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), SevenPointLattice.TRU);
		d1.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), SevenPointLattice.UNK);

		d2 = new RelationshipDelta();
		d2.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), SevenPointLattice.TRU);
		d2.setRelationship(new Relationship(tA, new ObjectLabel[] {x, y}), SevenPointLattice.FAL);
		d2.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), SevenPointLattice.UNK);
		d2.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), SevenPointLattice.UNK);

		d3 = new RelationshipDelta();
		d3.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), SevenPointLattice.TRU);
		d3.setRelationship(new Relationship(tA, new ObjectLabel[] {w, z}), SevenPointLattice.FAL);
		d3.setRelationship(new Relationship(tA, new ObjectLabel[] {x, y}), SevenPointLattice.UNK);
		d3.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), SevenPointLattice.UNK);
		d3.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), SevenPointLattice.UNK);
		d3.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), SevenPointLattice.UNK);

		d4 = new RelationshipDelta();
		d4.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), SevenPointLattice.TRU_STAR);
		d4.setRelationship(new Relationship(tA, new ObjectLabel[] {w, z}), SevenPointLattice.UNK);
		d4.setRelationship(new Relationship(tA, new ObjectLabel[] {x, y}), SevenPointLattice.TRU);
		d4.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), SevenPointLattice.FAL_STAR);
		d4.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), SevenPointLattice.FAL_STAR);
		d4.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), SevenPointLattice.TRU_STAR);
	}

	@Test
	public void testPolarize() {
		RelationshipDelta polar = d1.polarize();
		
		assertTrue(polar.getValue(new Relationship(tA, new ObjectLabel[] {w, y})) == SevenPointLattice.TRU_STAR);
		assertTrue(polar.getValue(new Relationship(tA, new ObjectLabel[] {x, z})) == SevenPointLattice.FAL_STAR);
		assertTrue(polar.getValue(new Relationship(tB, new ObjectLabel[] {y, z})) == SevenPointLattice.TRU_STAR);
		assertTrue(polar.getValue(new Relationship(tB, new ObjectLabel[] {z, y})) == SevenPointLattice.UNK);

		polar = d4.polarize();
		
		assertTrue(polar.getValue(new Relationship(tA, new ObjectLabel[] {w, y})) == SevenPointLattice.TRU_STAR);
		assertTrue(polar.getValue(new Relationship(tA, new ObjectLabel[] {w, z})) == SevenPointLattice.UNK);
		assertTrue(polar.getValue(new Relationship(tA, new ObjectLabel[] {x, y})) == SevenPointLattice.TRU_STAR);
		assertTrue(polar.getValue(new Relationship(tA, new ObjectLabel[] {x, z})) == SevenPointLattice.FAL_STAR);
		assertTrue(polar.getValue(new Relationship(tB, new ObjectLabel[] {y, z})) == SevenPointLattice.FAL_STAR);
		assertTrue(polar.getValue(new Relationship(tB, new ObjectLabel[] {z, y})) == SevenPointLattice.TRU_STAR);
	}
	
	@Test
	public void testJoin() {
		RelationshipDelta join;
		List<RelationshipDelta> list;
				
		list = new LinkedList<RelationshipDelta>();
		list.add(d1);
		list.add(d2);
		join = RelationshipDelta.join(list);
		
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {w, y})) == SevenPointLattice.TRU);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {x, y})) == SevenPointLattice.FAL_STAR);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {x, z})) == SevenPointLattice.FAL_STAR);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {y, z})) == SevenPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {z, y})) == SevenPointLattice.UNK);

		list = new LinkedList<RelationshipDelta>();
		list.add(d2);
		list.add(d4);
		join = RelationshipDelta.join(list);
		
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {w, y})) == SevenPointLattice.TRU_STAR);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {w, z})) == SevenPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {x, y})) == SevenPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {y, z})) == SevenPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {z, y})) == SevenPointLattice.UNK);

		list = new LinkedList<RelationshipDelta>();
		list.add(d1);
		list.add(d2);
		list.add(d3);
		list.add(d4);
		join = RelationshipDelta.join(list);
		
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {w, y})) == SevenPointLattice.TRU_STAR);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {w, z})) == SevenPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {x, y})) == SevenPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {x, z})) == SevenPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {y, z})) == SevenPointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {z, y})) == SevenPointLattice.UNK);
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
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), SevenPointLattice.TRU);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), SevenPointLattice.FAL);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), SevenPointLattice.TRU);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), SevenPointLattice.UNK);
		assertTrue(!delta.isStrictlyMorePrecise(ctx));
		
		//test a more precise delta
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), SevenPointLattice.TRU);
		assertTrue(delta.isStrictlyMorePrecise(ctx));
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), SevenPointLattice.UNK);
		
		//test a more and less precise delta
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), SevenPointLattice.UNK);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), SevenPointLattice.TRU);
		assertTrue(!delta.isStrictlyMorePrecise(ctx));
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), SevenPointLattice.TRU);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), SevenPointLattice.UNK);

		//test a less precise U delta
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), SevenPointLattice.UNK);
		assertTrue(!delta.isStrictlyMorePrecise(ctx));
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), SevenPointLattice.TRU);
		
		//test a thrashing delta
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), SevenPointLattice.FAL);
		assertTrue(!delta.isStrictlyMorePrecise(ctx));
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), SevenPointLattice.TRU);
		
		//test a delta which is less precise when star is used.
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {x, z}), SevenPointLattice.TRU_STAR);
		assertTrue(!delta.isStrictlyMorePrecise(ctx));
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {x, z}), SevenPointLattice.FAL);
		
		//another star, but equal precise
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), SevenPointLattice.TRU_STAR);
		assertTrue(!delta.isStrictlyMorePrecise(ctx));
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), SevenPointLattice.TRU);
		
		//test a more and the star used
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), SevenPointLattice.TRU_STAR);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), SevenPointLattice.FAL);
		assertTrue(delta.isStrictlyMorePrecise(ctx));
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), SevenPointLattice.TRU);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), SevenPointLattice.UNK);
	}

}
