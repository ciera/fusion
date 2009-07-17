package edu.cmu.cs.fusion.constraint;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.constraint.predicates.AndPredicate;
import edu.cmu.cs.fusion.constraint.predicates.RelationshipPredicate;

public class InferenceEnvironment implements Iterable<InferredRel>{
	Set<InferredRel> inferRules;
	
	public InferenceEnvironment() {
		inferRules = new HashSet<InferredRel>();
		
		String listControlType = "edu.cmu.cs.fusion.test.aspnet.api.ListControl";
		String listItemCollType = "edu.cmu.cs.fusion.test.aspnet.api.ListItemCollection";
		String listItemType = "edu.cmu.cs.fusion.test.aspnet.api.ListItem";

		Relation itemsRel = new Relation("Items", new String[] {listItemCollType, listControlType});
		Relation itemRel = new Relation("Item", new String[] {listItemType, listItemCollType});
		Relation childRel = new Relation("Child", new String[] {listItemType, listControlType});
		RelationshipPredicate r1 = new RelationshipPredicate(itemsRel, new SpecVar[] {new SpecVar("list"), new SpecVar("ctrl")});
		RelationshipPredicate r2 = new RelationshipPredicate(itemRel, new SpecVar[] {new SpecVar("item"), new SpecVar("list")});
		
		AndPredicate trigger = new AndPredicate(r1, r2);
		LinkedList<Effect> effect = new LinkedList<Effect>();
		effect.add(Effect.createAddEffect(childRel, new SpecVar[] {new SpecVar("item"), new SpecVar("ctrl")}));
		inferRules.add(new InferredRel(trigger, effect));
	}
	
	public Iterator<InferredRel> iterator() {
		return inferRules.iterator();
	}

	public void addRule(InferredRel inf) {
		inferRules.add(inf);
	}
}
