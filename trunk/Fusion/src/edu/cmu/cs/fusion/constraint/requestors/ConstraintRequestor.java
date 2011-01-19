package edu.cmu.cs.fusion.constraint.requestors;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.core.search.TypeReferenceMatch;

import edu.cmu.cs.fusion.RelationsEnvironment;
import edu.cmu.cs.fusion.ReportingUtility;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.Effect;
import edu.cmu.cs.fusion.constraint.Operation;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.predicates.TruePredicate;
import edu.cmu.cs.fusion.parsers.predicate.FPLParser;
import edu.cmu.cs.fusion.parsers.predicate.ParseException;

public class ConstraintRequestor extends SearchRequestor {
	
	public ConstraintRequestor(RelationsEnvironment rels) {
		this.rels = rels;
		parsed = new HashSet<IAnnotation>();
		constraints = new LinkedList<Constraint>();
	}
	
	private RelationsEnvironment rels;
	private HashSet<IAnnotation> parsed;
	private List<Constraint> constraints;
	
	@Override
	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		if (match.getAccuracy() == SearchMatch.A_INACCURATE)
			return;
		TypeReferenceMatch refMatch = (TypeReferenceMatch)match;
		
		IType contextType = (IType) refMatch.getElement();			
		IAnnotation constraint = (IAnnotation) refMatch.getLocalElement();
		
		if (parsed.contains(constraint))
			return; //SCREW YOU ECLIPSE!!!!
		
		if (constraint.getElementName().contains("Constraints")) {
			for (Object objAnno : (Object[])constraint.getMemberValuePairs()[0].getValue()) {
				parseConstraint((IAnnotation)objAnno, contextType);
			}
		}
		else
			parseConstraint(constraint, contextType);
		parsed.add(constraint);
	}

	private void parseConstraint(IAnnotation constraint, IType contextType) throws JavaModelException {
		FPLParser parser = new FPLParser(rels, contextType);
		Operation op = null;
		Predicate trigger = null, requires = null, restrict = null;
		List<Effect> effects = new LinkedList<Effect>();
		
		try {
			for (IMemberValuePair pair : constraint.getMemberValuePairs()) {
				String name = pair.getMemberName();
				if (name.equals("op")) {
					parser.reset((String)pair.getValue());
					op = parser.operation();
				}
				else if (name.equals("trigger")) {
					parser.reset((String)pair.getValue());
					trigger = parser.expression();
				}
				else if (name.equals("requires")) {
					parser.reset((String)pair.getValue());
					requires = parser.expression();
				}
				else if (name.equals("restrictTo")) {
					parser.reset((String)pair.getValue());
					restrict = parser.expression();
				}
				else if (name.equals("effects")) {			
					for (Object effect : (Object[])pair.getValue()) {
						parser.reset((String)effect);
						effects.add(parser.effect());
					}
				}
			}
			
			if (trigger == null)
				trigger = new TruePredicate();
			if (restrict == null)
				restrict = new TruePredicate();
			if (requires == null)
				requires = new TruePredicate();
				
			String owner = contextType.getFullyQualifiedName();	
			constraints.add(new Constraint(owner, op, trigger, restrict, requires, effects));
		} catch (ParseException e) {
			ReportingUtility.reportParseError(constraint.getResource(), constraint.getNameRange(), e.getMessage());
		}
	}

	public List<Constraint> getConstraints() {
		return constraints;
	}
}
