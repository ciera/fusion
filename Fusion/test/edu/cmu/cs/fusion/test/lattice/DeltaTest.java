package edu.cmu.cs.fusion.test.lattice;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.alias.ObjectLabel;
import edu.cmu.cs.fusion.relationship.FivePointLattice;
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
		d3.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FivePointLattice.TRU);
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
	public void testPolarize() {
		RelationshipDelta polar = d1.polarize();
		
		assertTrue(polar.getValue(new Relationship(tA, new ObjectLabel[] {w, y})) == FivePointLattice.TRU_STAR);
		assertTrue(polar.getValue(new Relationship(tA, new ObjectLabel[] {x, z})) == FivePointLattice.FAL_STAR);
		assertTrue(polar.getValue(new Relationship(tB, new ObjectLabel[] {y, z})) == FivePointLattice.TRU_STAR);
		assertTrue(polar.getValue(new Relationship(tB, new ObjectLabel[] {z, y})) == FivePointLattice.UNK);

		polar = d4.polarize();
		
		assertTrue(polar.getValue(new Relationship(tA, new ObjectLabel[] {w, y})) == FivePointLattice.TRU_STAR);
		assertTrue(polar.getValue(new Relationship(tA, new ObjectLabel[] {w, z})) == FivePointLattice.UNK);
		assertTrue(polar.getValue(new Relationship(tA, new ObjectLabel[] {x, y})) == FivePointLattice.TRU_STAR);
		assertTrue(polar.getValue(new Relationship(tA, new ObjectLabel[] {x, z})) == FivePointLattice.FAL_STAR);
		assertTrue(polar.getValue(new Relationship(tB, new ObjectLabel[] {y, z})) == FivePointLattice.FAL_STAR);
		assertTrue(polar.getValue(new Relationship(tB, new ObjectLabel[] {z, y})) == FivePointLattice.TRU_STAR);
	}
	
	@Test
	public void testJoin() {
		RelationshipDelta join;
		List<RelationshipDelta> list;
				
		list = new LinkedList<RelationshipDelta>();
		list.add(d1);
		list.add(d2);
		join = RelationshipDelta.join(list);
		
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {w, y})) == FivePointLattice.TRU);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {x, y})) == FivePointLattice.FAL_STAR);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {x, z})) == FivePointLattice.FAL_STAR);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {y, z})) == FivePointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {z, y})) == FivePointLattice.UNK);

		list = new LinkedList<RelationshipDelta>();
		list.add(d2);
		list.add(d4);
		join = RelationshipDelta.join(list);
		
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {w, y})) == FivePointLattice.TRU_STAR);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {w, z})) == FivePointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {x, y})) == FivePointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {y, z})) == FivePointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {z, y})) == FivePointLattice.UNK);

		list = new LinkedList<RelationshipDelta>();
		list.add(d1);
		list.add(d2);
		list.add(d3);
		list.add(d4);
		join = RelationshipDelta.join(list);
		
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {w, y})) == FivePointLattice.TRU_STAR);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {w, z})) == FivePointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {x, y})) == FivePointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tA, new ObjectLabel[] {x, z})) == FivePointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {y, z})) == FivePointLattice.UNK);
		assertTrue(join.getValue(new Relationship(tB, new ObjectLabel[] {z, y})) == FivePointLattice.UNK);
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
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FivePointLattice.TRU);
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {x, z}), FivePointLattice.FAL);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FivePointLattice.TRU);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FivePointLattice.UNK);
		assertTrue(!delta.isStrictlyMorePrecise(ctx));
		
		//test a more precise delta
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FivePointLattice.TRU);
		assertTrue(delta.isStrictlyMorePrecise(ctx));
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FivePointLattice.UNK);
		
		//test a more and less precise delta
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FivePointLattice.UNK);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FivePointLattice.TRU);
		assertTrue(!delta.isStrictlyMorePrecise(ctx));
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FivePointLattice.TRU);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FivePointLattice.UNK);

		//test a less precise U delta
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FivePointLattice.UNK);
		assertTrue(!delta.isStrictlyMorePrecise(ctx));
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FivePointLattice.TRU);
		
		//test a thrashing delta
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FivePointLattice.FAL);
		assertTrue(!delta.isStrictlyMorePrecise(ctx));
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FivePointLattice.TRU);
		
		//test a delta which is less precise when star is used.
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {x, z}), FivePointLattice.TRU_STAR);
		assertTrue(!delta.isStrictlyMorePrecise(ctx));
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {x, z}), FivePointLattice.FAL);
		
		//another star, but equal precise
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FivePointLattice.TRU_STAR);
		assertTrue(!delta.isStrictlyMorePrecise(ctx));
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {y, z}), FivePointLattice.TRU);
		
		//test a more and the star used
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FivePointLattice.TRU_STAR);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FivePointLattice.FAL);
		assertTrue(delta.isStrictlyMorePrecise(ctx));
		delta.setRelationship(new Relationship(tA, new ObjectLabel[] {w, y}), FivePointLattice.TRU);
		delta.setRelationship(new Relationship(tB, new ObjectLabel[] {z, y}), FivePointLattice.UNK);
	}

}
