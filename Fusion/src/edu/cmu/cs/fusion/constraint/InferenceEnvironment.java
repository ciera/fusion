package edu.cmu.cs.fusion.constraint;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.core.search.TypeReferenceMatch;

import edu.cmu.cs.fusion.RelationsEnvironment;
import edu.cmu.cs.fusion.parsers.predicate.FPLParser;
import edu.cmu.cs.fusion.parsers.predicate.ParseException;

public class InferenceEnvironment implements Iterable<InferredRel>{
	private class InferRequestor extends SearchRequestor {

		private RelationsEnvironment rels;
		private HashSet<IAnnotation> parsed;

		public InferRequestor(RelationsEnvironment rels) {
			this.rels = rels;
			parsed = new HashSet<IAnnotation>();
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
				
				for (String eString : effStrings) {
					parser.reset(eString);
					effects.add(parser.effect());
				}

				inferRules.add(new InferredRel(trigger, effects));
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}
		
	}
	
	
	Set<InferredRel> inferRules;
	
	public InferenceEnvironment() {
		inferRules = new HashSet<InferredRel>();
	}
	
	public Iterator<InferredRel> iterator() {
		return inferRules.iterator();
	}

	public void addRule(InferredRel inf) {
		inferRules.add(inf);
	}

	public void populate(RelationsEnvironment rels, IProgressMonitor monitor) throws CoreException {
		SearchEngine engine = new SearchEngine();
		SearchPattern pattern = SearchPattern.createPattern("Infer", IJavaSearchConstants.ANNOTATION_TYPE, IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE, SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);
		SearchParticipant[] participants = new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()};
		IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
		
		engine.search(pattern, participants, scope, new InferRequestor(rels), monitor);
	}
}
