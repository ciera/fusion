package edu.cmu.cs.fusion.constraint;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.core.search.TypeReferenceMatch;

import edu.cmu.cs.crystal.util.Utilities;
import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.RelationsEnvironment;
import edu.cmu.cs.fusion.constraint.operations.ConstructorOp;
import edu.cmu.cs.fusion.constraint.operations.MethodInvocationOp;
import edu.cmu.cs.fusion.constraint.predicates.TruePredicate;
import edu.cmu.cs.fusion.parsers.predicate.FPLParser;
import edu.cmu.cs.fusion.parsers.predicate.ParseException;

public class ConstraintEnvironment implements Iterable<Constraint> {
	private class ConstraintRequestor extends SearchRequestor {
		
		public ConstraintRequestor(RelationsEnvironment rels) {
			this.rels = rels;
			parsed = new HashSet<IAnnotation>();
		}
		
		private RelationsEnvironment rels;
		private HashSet<IAnnotation> parsed;
		
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
				//TODO send this out to the actual error reporting functionality!!!
				e.printStackTrace();
			}
		}
	}
	
	private class EffectRequestor extends SearchRequestor {

		private RelationsEnvironment rels;
		private HashSet<IMethod> parsed;
		
		public EffectRequestor(RelationsEnvironment rels) {
			this.rels = rels;
			parsed = new HashSet<IMethod>();
		}

		@Override
		public void acceptSearchMatch(SearchMatch match) throws CoreException {
			if (match.getAccuracy() == SearchMatch.A_INACCURATE)
				return;
			TypeReferenceMatch refMatch = (TypeReferenceMatch)match;
			
			IMethod method = (IMethod) refMatch.getElement();		
			if (parsed.contains(method))	//SYE!!!!
				return;

			List<Effect> effects = new LinkedList<Effect>();
			
			for (IAnnotation anno : method.getAnnotations()) {
				String qName = Utilities.resolveType(method.getDeclaringType(), anno.getElementName());
				Relation rel = rels.findRelation(qName);
				
				if (rel != null) {
					effects.add(parseEffect(anno, rel));
				}
				else {
					rel = isMultiRelation(anno, method.getDeclaringType());
					if (rel != null) {
						for (Object obj : (Object[])anno.getMemberValuePairs()[0].getValue()) {
							IAnnotation inner = (IAnnotation)obj;
							effects.add(parseEffect(inner, rel));
						}
					}
				}
			}
			parsed.add(method);
			assert(effects.size() > 0);
			
			Operation op;
			//this effect could be on a method OR a constructor.
			if (method.isConstructor()) {
				IType contextType = method.getDeclaringType();
				String type = contextType.getFullyQualifiedName();
				String[] paramTypes = new String[method.getParameterTypes().length];
				SpecVar[] opParams = new SpecVar[method.getParameterNames().length];
				
				for (int ndx = 0; ndx < paramTypes.length; ndx++) {
					paramTypes[ndx] = Utilities.resolveType(contextType, Signature.toString(method.getParameterTypes()[ndx]));
					opParams[ndx] = new SpecVar(method.getParameterNames()[ndx]);
				}
				op = new ConstructorOp(type, opParams, paramTypes);
			
			}
			else {
				IType contextType = method.getDeclaringType();
				String methodName = method.getElementName();
				String receiverType = contextType.getFullyQualifiedName();
				String returnType = Utilities.resolveType(contextType, Signature.toString(method.getReturnType()));
				String[] paramTypes = new String[method.getParameterTypes().length];
				SpecVar[] opParams = new SpecVar[method.getParameterNames().length];
				
				for (int ndx = 0; ndx < paramTypes.length; ndx++) {
					paramTypes[ndx] = Utilities.resolveType(contextType, Signature.toString(method.getParameterTypes()[ndx]));
					opParams[ndx] = new SpecVar(method.getParameterNames()[ndx]);
				}
				op = new MethodInvocationOp(methodName, receiverType, opParams, paramTypes, returnType);
			}
			
			constraints.add(new Constraint(op, new TruePredicate(), new TruePredicate(), effects));
		}
		
		private Relation isMultiRelation(IAnnotation anno, IType context) throws JavaModelException {
			//check if it has an array of annotations
			if (anno.getMemberValuePairs().length != 1)
				return null;
			IMemberValuePair pair = anno.getMemberValuePairs()[0];
			if (pair.getValueKind() != IMemberValuePair.K_ANNOTATION)
				return null;
			if (!(pair.getValue() instanceof Object[]))
				return null;
			if (((Object[])pair.getValue()).length < 2)	//can't be a multi-anno if there's less than two things in it
				return null;
			
			
			//check if those annotations are a relation
			IAnnotation inner = (IAnnotation)((Object[])pair.getValue())[0];
			String qName = Utilities.resolveType(context, inner.getElementName());
			return rels.findRelation(qName);
		}
		
		private Effect parseEffect(IAnnotation effectAnno, Relation rel) throws JavaModelException {
			Effect effect = null;
			IMemberValuePair paramsPair = null;
			IMemberValuePair actEffectPair = null;
			IMemberValuePair testPair = null;
			
			switch (effectAnno.getMemberValuePairs().length) {
			case 1:
				paramsPair = effectAnno.getMemberValuePairs()[0];
				break;
			case 2: 
				if (effectAnno.getMemberValuePairs()[0].getMemberName().equals("value")) {
					paramsPair = effectAnno.getMemberValuePairs()[0];
					actEffectPair = effectAnno.getMemberValuePairs()[1];
				}
				else {
					paramsPair = effectAnno.getMemberValuePairs()[1];
					actEffectPair = effectAnno.getMemberValuePairs()[0];
				}	
				break;
			case 3:
				if (effectAnno.getMemberValuePairs()[0].getMemberName().equals("value")) {
					paramsPair = effectAnno.getMemberValuePairs()[0];
					if (effectAnno.getMemberValuePairs()[1].getMemberName().equals("effect")) {
						actEffectPair = effectAnno.getMemberValuePairs()[1];
						testPair = effectAnno.getMemberValuePairs()[2];
					}
					else {
						actEffectPair = effectAnno.getMemberValuePairs()[2];
						testPair = effectAnno.getMemberValuePairs()[1];
					}	
				}
				else if (effectAnno.getMemberValuePairs()[1].getMemberName().equals("value")) {
					paramsPair = effectAnno.getMemberValuePairs()[1];
					if (effectAnno.getMemberValuePairs()[0].getMemberName().equals("effect")) {
						actEffectPair = effectAnno.getMemberValuePairs()[0];
						testPair = effectAnno.getMemberValuePairs()[2];
					}
					else {
						actEffectPair = effectAnno.getMemberValuePairs()[2];
						testPair = effectAnno.getMemberValuePairs()[0];
					}	
				}
				else {
					paramsPair = effectAnno.getMemberValuePairs()[2];
					if (effectAnno.getMemberValuePairs()[0].getMemberName().equals("effect")) {
						actEffectPair = effectAnno.getMemberValuePairs()[0];
						testPair = effectAnno.getMemberValuePairs()[1];
					}
					else {
						actEffectPair = effectAnno.getMemberValuePairs()[1];
						testPair = effectAnno.getMemberValuePairs()[0];
					}	
				}
				break;
			}
			
			
			Object[] objParams = (Object[])paramsPair.getValue();
			SpecVar[] params = new SpecVar[objParams.length];
			for (int ndx = 0; ndx < objParams.length; ndx++) {
				params[ndx] = new SpecVar((String)objParams[ndx]);
			}
			
			edu.cmu.cs.fusion.annot.Relation.Effect actualEffect;
			
			if (actEffectPair != null) {
				String strEffect = ((String)actEffectPair.getValue()).substring(((String)actEffectPair.getValue()).lastIndexOf('.') + 1); //SCREW YOU ECLIPSE!!!!!
				actualEffect = edu.cmu.cs.fusion.annot.Relation.Effect.valueOf(strEffect);
			}
			else
				actualEffect = edu.cmu.cs.fusion.annot.Relation.Effect.ADD;
			
			String test = null;
			
			switch (actualEffect) {
			case ADD:
				effect = Effect.createAddEffect(rel, params);
				break;
			case REMOVE:
				effect = Effect.createRemoveEffect(rel, params);
				break;
			case TEST:
				test = (String) testPair.getValue();
				effect = Effect.createTestEffect(rel, params, new SpecVar(test));
				break;
			}
			return effect;
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
		
		EffectRequestor requestor = new EffectRequestor(rels);
		for (Relation rel : rels) {
			pattern = SearchPattern.createPattern(rel.getName(), IJavaSearchConstants.ANNOTATION_TYPE, IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE, SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);
			engine.search(pattern, participants, scope, requestor, monitor);			
		}
	}

	public Iterator<Constraint> iterator() {
		return constraints.iterator();
	}
}
