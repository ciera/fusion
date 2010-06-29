package edu.cmu.cs.fusion;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

import edu.cmu.cs.crystal.util.Utilities;

public class RelationsEnvironment extends SearchRequestor implements Iterable<Relation> {
	private Map<String, Relation> rels;
	
	public RelationsEnvironment() {
		rels = new HashMap<String, Relation>();
	}
	
	public Relation findRelation(String qualifiedName) {
		return rels.get(qualifiedName);
	}
	
	public Iterator<Relation> iterator() {
		return rels.values().iterator();
	}
	
	public void populate(IProgressMonitor monitor) throws CoreException {
		SearchEngine engine = new SearchEngine();
		SearchPattern pattern = SearchPattern.createPattern("Relation", IJavaSearchConstants.ANNOTATION_TYPE, IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE, SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);
		SearchParticipant[] participants = new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()};
		IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
		
		engine.search(pattern, participants, scope, this, monitor);
	}
	
	public void addRelation(Relation rel) {
		rels.put(rel.getName(), rel);
	}
	
	@Override
	public String toString() {
		return rels.values().toString();
	}

	@Override
	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		if (match.getAccuracy() == SearchMatch.A_INACCURATE)
			return;
		TypeReferenceMatch refMatch = (TypeReferenceMatch)match;
		
		IType relation = (IType) refMatch.getElement();
		IAnnotation relAnno = (IAnnotation) refMatch.getLocalElement();
		
		Object[] types = (Object[])relAnno.getMemberValuePairs()[0].getValue();
		String[] qualifiedTypes = new String[types.length];
		for (int ndx = 0; ndx < qualifiedTypes.length; ndx++)
			qualifiedTypes[ndx] = Utilities.resolveType(relation, (String)types[ndx]);
		addRelation(new Relation(relation.getFullyQualifiedName(), qualifiedTypes));

		//DON't ACTUALLY CHECK HERE!!! That occurs in the framework-side checker.
		//here, just assume all is ok.
/*
		
		//check that the resolved type is actually correct
		//get the list of members
		//check that it has each of the three required members, with the right types
		IMethod[] methods = relation.getMethods();
		
		if (methods.length != 3) {
			ASTNode node = getNode(relation);
			reporter.reportUserProblem("Relations must have three attributes: value, effect, and test.", node, "Relation Parser", SEVERITY.ERROR);
		}
		if (!(methods[0].getElementName().equals("value") && methods[0].getReturnType().equals("[QString;"))){
			ASTNode node = getNode(relation);
			reporter.reportUserProblem("The first attribute must be String[] value().", node, "Relation Parser", SEVERITY.ERROR);
		}
		if (!(methods[1].getElementName().equals("effect") && methods[1].getReturnType().equals("QRelation.Effect;"))){
			ASTNode node = getNode(relation);
			reporter.reportUserProblem("The second attribute must be Relation.Effect effect() default Relation.Effect.ADD.", node, "Relation Parser", SEVERITY.ERROR);
		}
		if (!(methods[2].getElementName().equals("test") && methods[2].getReturnType().equals("QString;"))){
			ASTNode node = getNode(relation);
			reporter.reportUserProblem("The third attribute must be String test() default \"\"", node, "Relation Parser", SEVERITY.ERROR);
		}
*/			
	}
}
