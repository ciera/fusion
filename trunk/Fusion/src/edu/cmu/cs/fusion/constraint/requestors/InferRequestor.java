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
import edu.cmu.cs.fusion.constraint.Effect;
import edu.cmu.cs.fusion.constraint.InferredRel;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.parsers.predicate.FPLParser;
import edu.cmu.cs.fusion.parsers.predicate.ParseException;

public class InferRequestor extends SearchRequestor {

	private RelationsEnvironment rels;
	private HashSet<IAnnotation> parsed;
	private List<InferredRel> inferRules;


	public InferRequestor(RelationsEnvironment rels) {
		this.rels = rels;
		parsed = new HashSet<IAnnotation>();
		inferRules = new LinkedList<InferredRel>();
	}

	@Override
	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		if (match.getAccuracy() == SearchMatch.A_INACCURATE)
			return;
		TypeReferenceMatch refMatch = (TypeReferenceMatch)match;
		
		IType contextType = (IType) refMatch.getElement();			
		IAnnotation infer = (IAnnotation) refMatch.getLocalElement();
		
		if (parsed.contains(infer))
			return; //SCREW YOU ECLIPSE!!!!
		
		if (infer.getElementName().contains("Infers")) {
			for (Object objAnno : (Object[])infer.getMemberValuePairs()[0].getValue()) {
				parseInferRule((IAnnotation)objAnno, contextType);
			}
		}
		else
			parseInferRule(infer, contextType);
		parsed.add(infer);
	}

	private void parseInferRule(IAnnotation infer, IType contextType) throws JavaModelException {
		String trgString = (String)infer.getMemberValuePairs()[0].getValue();
		
		Object[] effObj = (Object[])infer.getMemberValuePairs()[1].getValue();
		String[] effStrings = new String[effObj.length];
		for (int ndx = 0; ndx < effStrings.length; ndx++) {
			effStrings[ndx] = (String)effObj[ndx];
		}
		
		FPLParser parser = new FPLParser(rels, contextType);
		Predicate trigger;
		List<Effect> effects = new LinkedList<Effect>();
		
		try {			
			parser.reset(trgString);
			trigger = parser.expression();
			assert(trigger != null);
			
			for (String eString : effStrings) {
				parser.reset(eString);
				effects.add(parser.effect());
			}
			
			inferRules.add(new InferredRel(trigger, effects));
		} catch (ParseException e) {
			ReportingUtility.reportParseError(infer.getResource(), infer.getNameRange(), e.getMessage());
			e.printStackTrace();
		}

	}

	public List<InferredRel> getRules() {
		return inferRules;
	}
	
}

