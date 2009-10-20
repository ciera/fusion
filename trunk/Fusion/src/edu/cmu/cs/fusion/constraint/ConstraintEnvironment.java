package edu.cmu.cs.fusion.constraint;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.core.search.TypeReferenceMatch;

import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.RelationsEnvironment;
import edu.cmu.cs.fusion.constraint.operations.MethodInvocationOp;
import edu.cmu.cs.fusion.constraint.predicates.AndPredicate;
import edu.cmu.cs.fusion.constraint.predicates.BooleanValue;
import edu.cmu.cs.fusion.constraint.predicates.InstanceOfPredicate;
import edu.cmu.cs.fusion.constraint.predicates.RelationshipPredicate;
import edu.cmu.cs.fusion.constraint.predicates.TruePredicate;
import edu.cmu.cs.fusion.parsers.EffectParser;
import edu.cmu.cs.fusion.parsers.OperationParser;
import edu.cmu.cs.fusion.parsers.predicate.FPLParser;
import edu.cmu.cs.fusion.parsers.predicate.ParseException;

public class ConstraintEnvironment implements Iterable<Constraint> {
	private class ConstraintRequestor extends SearchRequestor {
		
		public ConstraintRequestor(RelationsEnvironment rels) {
			this.rels = rels;
		}
		
		private RelationsEnvironment rels;
		private OperationParser opParser = new OperationParser();
		private EffectParser effParser = new EffectParser();
		
		@Override
		public void acceptSearchMatch(SearchMatch match) throws CoreException {
			if (match.getAccuracy() == SearchMatch.A_INACCURATE)
				return;
			TypeReferenceMatch refMatch = (TypeReferenceMatch)match;
			
			IType contextType = (IType) refMatch.getElement();			
			IAnnotation constraint = (IAnnotation) refMatch.getLocalElement();
			
			String opString = (String)constraint.getMemberValuePairs()[0].getValue();
			String trgString = (String)constraint.getMemberValuePairs()[1].getValue();
			String reqString = (String)constraint.getMemberValuePairs()[2].getValue();
			
			Object[] effObj = (Object[])constraint.getMemberValuePairs()[3].getValue();
			String[] effString = new String[effObj.length];
			for (int ndx = 0; ndx < effString.length; ndx++) {
				effString[ndx] = (String)effObj[ndx];
			}
			
			FPLParser trgParser = new FPLParser(trgString, rels, contextType);
			FPLParser reqParser = new FPLParser(reqString, rels, contextType);
			
			Constraint cons;
			try {
				cons = new Constraint(opParser.parse(opString, contextType), trgParser.expression(), reqParser.expression(), effParser.parse(effString));
				constraints.add(cons);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private class EffectRequestor extends SearchRequestor {
		@Override
		public void acceptSearchMatch(SearchMatch match) throws CoreException {
			// TODO Auto-generated method stub	
		}
	}
	
	List<Constraint> constraints;
	
	public ConstraintEnvironment() {
		constraints = new LinkedList<Constraint>();
	}
	
	public void populate(RelationsEnvironment rels, IProgressMonitor monitor) throws CoreException {
		SearchEngine engine = new SearchEngine();
		SearchPattern pattern = SearchPattern.createPattern("Constraint", IJavaSearchConstants.ANNOTATION_TYPE, IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE, SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);
		SearchParticipant[] participants = new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()};
		IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
		
		engine.search(pattern, participants, scope, new ConstraintRequestor(rels), monitor);
		
		
		
		
		
		
		
		
		MethodInvocationOp op;
		Predicate trigger;
		Predicate required;
		List<Effect> effect;

		String listControlType = "edu.cmu.cs.fusion.test.aspnet.api.ListControl";
		String ddlType = "edu.cmu.cs.fusion.test.aspnet.api.DropDownList";
		String listItemCollType = "edu.cmu.cs.fusion.test.aspnet.api.ListItemCollection";
		String listItemType = "edu.cmu.cs.fusion.test.aspnet.api.ListItem";

		Relation itemsRel = new Relation("Items", new String[] {listItemCollType, listControlType});
		Relation itemRel = new Relation("Item", new String[] {listItemType, listItemCollType});
		Relation childRel = new Relation("Child", new String[] {listItemType, listControlType});
		Relation corrSelRel = new Relation("CorrectlySelected", new String[] {listControlType});
		Relation selectedRel = new Relation("Selected", new String[] {listItemType});
		Relation textRel = new Relation("Text", new String[] {"java.lang.String", listItemType});
		
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
		
		op = new MethodInvocationOp("getText", listItemType, new SpecVar[] {}, new String[] {}, "java.lang.String");
		trigger = new TruePredicate();
		required = new TruePredicate();
		effect = new LinkedList<Effect>();
		effect.add(Effect.createAddEffect(textRel, new SpecVar[] {Constraint.RESULT, Constraint.RECEIVER}));
		constraints.add(new Constraint(op, trigger, required, effect));
		
		op = new MethodInvocationOp("setText", listItemType, new SpecVar[] {new SpecVar("text")}, new String[] {"java.lang.String"}, "void");
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

		op = new MethodInvocationOp("findByText", listItemCollType, new SpecVar[] {new SpecVar("text")}, new String[] {"java.lang.String"}, listItemType);
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

		op = new MethodInvocationOp("setSelected", listItemType, new SpecVar[] {new SpecVar("selected")}, new String[] {"boolean"}, "void");
		Predicate p1 = new BooleanValue(new SpecVar("selected"));
		Predicate p2 = new RelationshipPredicate(childRel, new SpecVar[] {Constraint.RECEIVER, new SpecVar("ctrl")});
		Predicate p3 = new InstanceOfPredicate(new SpecVar("ctrl"), ddlType);		
		trigger = new AndPredicate(new AndPredicate(p1, p2), p3);
		required = new RelationshipPredicate(corrSelRel, new SpecVar[] {new SpecVar("ctrl")}, false);
		effect = new LinkedList<Effect>();
		effect.add(Effect.createAddEffect(corrSelRel, new SpecVar[] {new SpecVar("ctrl")}));
		constraints.add(new Constraint(op, trigger, required, effect));

		op = new MethodInvocationOp("setSelected", listItemType, new SpecVar[] {new SpecVar("selected")}, new String[] {"boolean"}, "void");
		p1 = new BooleanValue(new SpecVar("selected"), false);
		p2 = new RelationshipPredicate(childRel, new SpecVar[] {Constraint.RECEIVER, new SpecVar("ctrl")});
		p3 = new InstanceOfPredicate(new SpecVar("ctrl"), ddlType);		
		trigger = new AndPredicate(new AndPredicate(p1, p2), p3);
		required = new RelationshipPredicate(selectedRel, new SpecVar[] {Constraint.RECEIVER});
		effect = new LinkedList<Effect>();
		effect.add(Effect.createRemoveEffect(corrSelRel, new SpecVar[] {new SpecVar("ctrl")}));
		constraints.add(new Constraint(op, trigger, required, effect));
}

	public Iterator<Constraint> iterator() {
		return constraints.iterator();
	}
}
