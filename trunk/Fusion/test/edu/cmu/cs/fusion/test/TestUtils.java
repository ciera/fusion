package edu.cmu.cs.fusion.test;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.relationship.FourPointLattice;
import edu.cmu.cs.fusion.relationship.RelationshipContext;
import edu.cmu.cs.fusion.relationship.RelationshipDelta;
import edu.cmu.cs.fusion.test.lattice.AbstractObjectLabel;


public class TestUtils {
	RelationshipDelta[] deltas; 
	RelationshipContext[] contexts;
	Relation[] relations;
	ObjectLabel[] labels;
	SpecVar[] vars;
	Substitution[] subs;

	public RelationshipDelta getDelta(int i) {
		return deltas[i];
	}
	
	public RelationshipContext getContext(int i) {
		return contexts[i];
	}
	
	public Relation getRelation(int i) {
		return relations[i];
	}
	
	public ObjectLabel getLabel(int i) {
		return labels[i];
	}
	
	public SpecVar getVar(int i) {
		return vars[i];
	}
	
	public Substitution getSub(int i) {
		return subs[i];
	}
	
	public TestUtils() {
		deltas = new RelationshipDelta[4];
		contexts = new RelationshipContext[4];
		relations = new Relation[2];
		labels = new ObjectLabel[4];
		vars = new SpecVar[5];
		subs = new Substitution[2];
		
		RelationshipDelta delta;
		relations[0] = new Relation("A", new String[] {"Foo", "Bar"});
		relations[1] = new Relation("B", new String[] {"Bar", "Bar"});
		
		labels[0] = new AbstractObjectLabel("w");
		labels[1] = new AbstractObjectLabel("x");
		labels[2] = new AbstractObjectLabel("y");
		labels[3] = new AbstractObjectLabel("z");
		
		vars[0] = new SpecVar();
		vars[1] = new SpecVar();
		vars[2] = new SpecVar();
		vars[3] = new SpecVar();
		vars[4] = new SpecVar();
		
		subs[0] = new Substitution();
		subs[0].addSub(vars[0], labels[0]);
		subs[0].addSub(vars[1], labels[2]);
		subs[0].addSub(vars[2], labels[3]);
		subs[0].addSub(vars[3], labels[2]);
		subs[0].addSub(vars[4], labels[1]);

		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[2]}), FourPointLattice.TRU);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[3]}), FourPointLattice.FAL);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[2]}), FourPointLattice.TRU);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[3]}), FourPointLattice.FAL);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[2], labels[3]}), FourPointLattice.TRU);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[3], labels[2]}), FourPointLattice.UNK);
		contexts[0] = new RelationshipContext(true).applyChangesFromDelta(delta);
		
		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[2]}), FourPointLattice.TRU);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[3]}), FourPointLattice.FAL);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[2]}), FourPointLattice.FAL);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[3]}), FourPointLattice.TRU);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[2], labels[3]}), FourPointLattice.UNK);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[3], labels[2]}), FourPointLattice.UNK);
		contexts[1] = new RelationshipContext(true).applyChangesFromDelta(delta);

		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[2]}), FourPointLattice.TRU);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[3]}), FourPointLattice.FAL);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[2]}), FourPointLattice.UNK);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[3]}), FourPointLattice.UNK);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[2], labels[3]}), FourPointLattice.UNK);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[3], labels[2]}), FourPointLattice.UNK);
		contexts[2] = new RelationshipContext(true).applyChangesFromDelta(delta);

		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[2]}), FourPointLattice.TRU);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[3]}), FourPointLattice.UNK);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[2]}), FourPointLattice.FAL);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[3]}), FourPointLattice.UNK);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[2], labels[3]}), FourPointLattice.UNK);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[3], labels[2]}), FourPointLattice.UNK);
		contexts[3] = new RelationshipContext(true).applyChangesFromDelta(delta);
		
		
		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[2]}), FourPointLattice.TRU);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[3]}), FourPointLattice.BOT);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[2]}), FourPointLattice.BOT);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[3]}), FourPointLattice.FAL);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[2], labels[3]}), FourPointLattice.TRU);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[3], labels[2]}), FourPointLattice.UNK);
		deltas[0] = delta;
		
		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[2]}), FourPointLattice.TRU);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[3]}), FourPointLattice.BOT);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[2]}), FourPointLattice.FAL);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[3]}), FourPointLattice.BOT);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[2], labels[3]}), FourPointLattice.UNK);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[3], labels[2]}), FourPointLattice.UNK);
		deltas[1] = delta;

		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[2]}), FourPointLattice.TRU);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[3]}), FourPointLattice.FAL);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[2]}), FourPointLattice.UNK);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[3]}), FourPointLattice.UNK);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[2], labels[3]}), FourPointLattice.UNK);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[3], labels[2]}), FourPointLattice.UNK);
		deltas[2] = delta;

		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[2]}), FourPointLattice.BOT);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[3]}), FourPointLattice.UNK);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[2]}), FourPointLattice.TRU);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[3]}), FourPointLattice.BOT);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[2], labels[3]}), FourPointLattice.BOT);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[3], labels[2]}), FourPointLattice.BOT);
		deltas[3] = delta;
	}
}