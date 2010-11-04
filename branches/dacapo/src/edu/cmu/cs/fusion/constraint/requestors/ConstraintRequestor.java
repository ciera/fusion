package edu.cmu.cs.fusion.constraint.requestors;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotation;
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
		String opString = (String)constraint.getMemberValuePairs()[0].getValue();
		String trgString = (String)constraint.getMemberValuePairs()[1].getValue();
		String reqString = (String)constraint.getMemberValuePairs()[2].getValue();
		
		Object[] effObj = (Object[])constraint.getMemberValuePairs()[3].getValue();
		String[] effStrings = new String[effObj.length];
		for (int ndx = 0; ndx < effStrings.length; ndx++) {
			effStrings[ndx] = (String)effObj[ndx];
		}
		
		FPLParser parser = new FPLParser(rels, contextType);
		Operation op;
		Predicate trigger, requires;
		List<Effect> effects = new LinkedList<Effect>();
		
		try {
			parser.reset(opString);
			op = parser.operation();
			
			parser.reset(trgString);
			trigger = parser.expression();
		
			parser.reset(reqString);
			requires = parser.expression();
			
			for (String eString : effStrings) {
				parser.reset(eString);
				effects.add(parser.effect());
			}

			constraints.add(new Constraint(op, trigger, requires, effects));
		} catch (ParseException e) {
			ReportingUtility.reportParseError(constraint.getResource(), constraint.getNameRange(), e.getMessage());
		}
	}

	public List<Constraint> getConstraints() {
		return constraints;
	}
}
