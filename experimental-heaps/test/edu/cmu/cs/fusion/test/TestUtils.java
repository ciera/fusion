package edu.cmu.cs.fusion.test;

import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.alias.AliasContext;
import edu.cmu.cs.fusion.alias.ObjectLabel;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.relationship.FivePointLattice;
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
	AliasContext[] aliases;

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
	
	public AliasContext getAliases(int i) {
		return aliases[i];
	}
	
	public TestUtils() {
		FreeVars.setHierarchy(new EqualityOnlyTypeHierarchy());
		deltas = new RelationshipDelta[4];
		contexts = new RelationshipContext[4];
		relations = new Relation[3];
		labels = new ObjectLabel[4];
		vars = new SpecVar[5];
		subs = new Substitution[2];
		aliases = new TestAliasContext[2];
		
		RelationshipDelta delta;
		TestAliasContext aliasContext;
		
		relations[0] = new Relation("A", new String[] {"Foo", "Bar"});
		relations[1] = new Relation("B", new String[] {"Bar", "Bar"});
		relations[2] = new Relation("C", new String[] {"Bar", "Bazar"});
	
		labels[0] = new AbstractObjectLabel("w", "Bar");
		labels[1] = new AbstractObjectLabel("x");
		labels[2] = new AbstractObjectLabel("y", "Foo");
		labels[3] = new AbstractObjectLabel("z");
		
		vars[0] = new SpecVar();
		vars[1] = new SpecVar();
		vars[2] = new SpecVar();
		vars[3] = new SpecVar();
		vars[4] = new SpecVar();
		
		subs[0] = new Substitution();
		subs[0] = subs[0].addSub(vars[0], labels[0]);
		subs[0] = subs[0].addSub(vars[1], labels[2]);
		subs[0] = subs[0].addSub(vars[2], labels[3]);
		subs[0] = subs[0].addSub(vars[3], labels[2]);
		subs[0] = subs[0].addSub(vars[4], labels[1]);

		subs[1] = new Substitution();
		subs[1] = subs[1].addSub(vars[0], labels[0]);
		subs[1] = subs[1].addSub(vars[1], labels[2]);
		subs[1] = subs[1].addSub(vars[2], labels[3]);
		subs[1] = subs[1].addSub(Constraint.RESULT, labels[2]);
		subs[1] = subs[1].addSub(Constraint.RECEIVER, labels[1]);
		
		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[2]}), FivePointLattice.TRU);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[3]}), FivePointLattice.FAL);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[2]}), FivePointLattice.TRU);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[3]}), FivePointLattice.FAL);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[2], labels[3]}), FivePointLattice.TRU);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[3], labels[2]}), FivePointLattice.UNK);
		contexts[0] = new RelationshipContext(true).applyChangesFromDelta(delta);
		
		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[2]}), FivePointLattice.TRU);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[3]}), FivePointLattice.FAL);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[2]}), FivePointLattice.FAL);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[3]}), FivePointLattice.TRU);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[2], labels[3]}), FivePointLattice.UNK);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[3], labels[2]}), FivePointLattice.UNK);
		contexts[1] = new RelationshipContext(true).applyChangesFromDelta(delta);

		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[2]}), FivePointLattice.TRU);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[3]}), FivePointLattice.FAL);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[2]}), FivePointLattice.UNK);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[3]}), FivePointLattice.UNK);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[2], labels[3]}), FivePointLattice.UNK);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[3], labels[2]}), FivePointLattice.UNK);
		contexts[2] = new RelationshipContext(true).applyChangesFromDelta(delta);

		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[2]}), FivePointLattice.TRU);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[3]}), FivePointLattice.UNK);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[2]}), FivePointLattice.FAL);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[3]}), FivePointLattice.UNK);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[2], labels[3]}), FivePointLattice.UNK);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[3], labels[2]}), FivePointLattice.UNK);
		contexts[3] = new RelationshipContext(true).applyChangesFromDelta(delta);
		
		
		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[2]}), FivePointLattice.TRU);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[3]}), FivePointLattice.FAL);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[2], labels[3]}), FivePointLattice.TRU);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[3], labels[2]}), FivePointLattice.UNK);
		deltas[0] = delta;
		
		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[2]}), FivePointLattice.TRU);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[2]}), FivePointLattice.FAL);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[2], labels[3]}), FivePointLattice.UNK);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[3], labels[2]}), FivePointLattice.UNK);
		deltas[1] = delta;

		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[2]}), FivePointLattice.TRU);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[3]}), FivePointLattice.FAL);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[2]}), FivePointLattice.UNK);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[3]}), FivePointLattice.UNK);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[2], labels[3]}), FivePointLattice.UNK);
		delta.setRelationship(new Relationship(relations[1], new ObjectLabel[] {labels[3], labels[2]}), FivePointLattice.UNK);
		deltas[2] = delta;

		delta = new RelationshipDelta();
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[0], labels[3]}), FivePointLattice.UNK);
		delta.setRelationship(new Relationship(relations[0], new ObjectLabel[] {labels[1], labels[2]}), FivePointLattice.TRU);
		deltas[3] = delta;
		
		aliasContext = new TestAliasContext();
		aliases[0] = aliasContext;
		
		aliasContext = new TestAliasContext();
		aliases[1] = aliasContext;
	}

}
