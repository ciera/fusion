package edu.cmu.cs.fusion.constraint;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.constraint.operations.MethodInvocationOp;
import edu.cmu.cs.fusion.constraint.predicates.TruePredicate;

public class ConstraintEnvironment implements Iterable<Constraint> {
	List<Constraint> constraints;
	
	public void populate() {
		MethodInvocationOp op;
		Predicate trigger;
		Predicate required;
		List<Effect> effect;

		constraints = new LinkedList<Constraint>();

		String listControlType = "edu.cmu.cs.fusion.test.aspnet.api.ListControl";
		String listItemCollType = "edu.cmu.cs.fusion.test.aspnet.api.ListItemCollection";
		String listItemType = "edu.cmu.cs.fusion.test.aspnet.api.ListItem";

		Relation itemsRel = new Relation("Items", new String[] {listItemCollType, listControlType});
		Relation itemRel = new Relation("Item", new String[] {listItemType, listItemCollType});
		Relation childRel = new Relation("Child", new String[] {listItemType, listControlType});
		Relation selectedRel = new Relation("Selected", new String[] {listItemType});
		Relation textRel = new Relation("Text", new String[] {"String", listItemType});
		
		op = new MethodInvocationOp("getItems", listControlType, new SpecVar[] {}, new String[] {}, listItemCollType);
		trigger = new TruePredicate();
		required = new TruePredicate();
		effect = new LinkedList<Effect>();
		effect.add(Effect.createAddEffect(itemsRel, new SpecVar[] {Constraint.RESULT, Constraint.RECEIVER}));
		constraints.add(new Constraint(op, trigger, required, effect));
		
		op = new MethodInvocationOp("getSelectedItem", listControlType, new SpecVar[] {}, new String[] {}, listItemType);
		trigger = new TruePredicate();
		required = new TruePredicate();
		effect = new LinkedList<Effect>();
		effect.add(Effect.createAddEffect(childRel, new SpecVar[] {Constraint.RESULT, Constraint.RECEIVER}));
		effect.add(Effect.createAddEffect(selectedRel, new SpecVar[] {Constraint.RESULT}));
		constraints.add(new Constraint(op, trigger, required, effect));
		
		op = new MethodInvocationOp("getSelected", listItemType, new SpecVar[] {}, new String[] {}, "boolean");
		trigger = new TruePredicate();
		required = new TruePredicate();
		effect = new LinkedList<Effect>();
		effect.add(Effect.createTestEffect(selectedRel, new SpecVar[] {Constraint.RECEIVER}, Constraint.RESULT));
		constraints.add(new Constraint(op, trigger, required, effect));
		
		op = new MethodInvocationOp("setSelected", listItemType, new SpecVar[] {new SpecVar("selected")}, new String[] {"boolean"}, "void");
		trigger = new TruePredicate();
		required = new TruePredicate();
		effect = new LinkedList<Effect>();
		effect.add(Effect.createTestEffect(selectedRel, new SpecVar[] {Constraint.RECEIVER}, new SpecVar("selected")));
		constraints.add(new Constraint(op, trigger, required, effect));
		
		op = new MethodInvocationOp("getText", listItemType, new SpecVar[] {}, new String[] {}, "boolean");
		trigger = new TruePredicate();
		required = new TruePredicate();
		effect = new LinkedList<Effect>();
		effect.add(Effect.createAddEffect(textRel, new SpecVar[] {Constraint.RESULT, Constraint.RECEIVER}));
		constraints.add(new Constraint(op, trigger, required, effect));
		
		op = new MethodInvocationOp("setText", listItemType, new SpecVar[] {new SpecVar("text")}, new String[] {"boolean"}, "void");
		trigger = new TruePredicate();
		required = new TruePredicate();
		effect = new LinkedList<Effect>();
		effect.add(Effect.createRemoveEffect(textRel, new SpecVar[] {new SpecVar(), Constraint.RECEIVER}));
		effect.add(Effect.createAddEffect(textRel, new SpecVar[] {new SpecVar("text"), Constraint.RECEIVER}));
		constraints.add(new Constraint(op, trigger, required, effect));
		
		op = new MethodInvocationOp("remove", listItemCollType, new SpecVar[] {new SpecVar("item")}, new String[] {listItemType}, "void");
		trigger = new TruePredicate();
		required = new TruePredicate();
		effect = new LinkedList<Effect>();
		effect.add(Effect.createRemoveEffect(itemRel, new SpecVar[] {new SpecVar("item"), Constraint.RECEIVER}));
		constraints.add(new Constraint(op, trigger, required, effect));

	
		op = new MethodInvocationOp("add", listItemCollType, new SpecVar[] {new SpecVar("item")}, new String[] {listItemType}, "void");
		trigger = new TruePredicate();
		required = new TruePredicate();
		effect = new LinkedList<Effect>();
		effect.add(Effect.createAddEffect(itemRel, new SpecVar[] {new SpecVar("item"), Constraint.RECEIVER}));
		constraints.add(new Constraint(op, trigger, required, effect));
		
		op = new MethodInvocationOp("contains", listItemCollType, new SpecVar[] {new SpecVar("item")}, new String[] {listItemType}, "boolean");
		trigger = new TruePredicate();
		required = new TruePredicate();
		effect = new LinkedList<Effect>();
		effect.add(Effect.createTestEffect(itemRel, new SpecVar[] {new SpecVar("item"), Constraint.RECEIVER}, Constraint.RESULT));
		constraints.add(new Constraint(op, trigger, required, effect));

		op = new MethodInvocationOp("addOrRemove", listItemCollType, new SpecVar[] {new SpecVar("item"), new SpecVar("toAdd")}, new String[] {listItemType, "boolean"}, "void");
		trigger = new TruePredicate();
		required = new TruePredicate();
		effect = new LinkedList<Effect>();
		effect.add(Effect.createTestEffect(itemRel, new SpecVar[] {new SpecVar("item"), Constraint.RECEIVER}, new SpecVar("toAdd")));
		constraints.add(new Constraint(op, trigger, required, effect));

		op = new MethodInvocationOp("findByText", listItemCollType, new SpecVar[] {new SpecVar("text")}, new String[] {"String"}, listItemType);
		trigger = new TruePredicate();
		required = new TruePredicate();
		effect = new LinkedList<Effect>();
		effect.add(Effect.createAddEffect(itemRel, new SpecVar[] {Constraint.RESULT, Constraint.RECEIVER}));
		effect.add(Effect.createAddEffect(textRel, new SpecVar[] {new SpecVar("text"), Constraint.RESULT}));
		constraints.add(new Constraint(op, trigger, required, effect));

		op = new MethodInvocationOp("clear", listItemCollType, new SpecVar[] {}, new String[] {}, "void");
		trigger = new TruePredicate();
		required = new TruePredicate();
		effect = new LinkedList<Effect>();
		effect.add(Effect.createRemoveEffect(itemRel, new SpecVar[] {new SpecVar(), Constraint.RECEIVER}));
		constraints.add(new Constraint(op, trigger, required, effect));
}

	public Iterator<Constraint> iterator() {
		return constraints.iterator();
	}
}
