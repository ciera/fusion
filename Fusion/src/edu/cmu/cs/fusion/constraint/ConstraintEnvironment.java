package edu.cmu.cs.fusion.constraint;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.RelationsEnvironment;
import edu.cmu.cs.fusion.ReportingUtility;
import edu.cmu.cs.fusion.constraint.predicates.TruePredicate;
import edu.cmu.cs.fusion.constraint.requestors.ConstraintRequestor;
import edu.cmu.cs.fusion.constraint.requestors.EffectRequestor;
import edu.cmu.cs.fusion.parsers.predicate.FPLParser;
import edu.cmu.cs.fusion.parsers.predicate.ParseException;

public class ConstraintEnvironment implements Iterable<Constraint> {
	List<Constraint> constraints;
	private RelationsEnvironment rels;
	
	public ConstraintEnvironment(RelationsEnvironment rels) {
		constraints = new LinkedList<Constraint>();
		this.rels = rels;
	}
	
	public void populate(IProgressMonitor monitor) throws CoreException {
		SearchEngine engine = new SearchEngine();
		SearchPattern pattern = SearchPattern.createPattern("Constraint", IJavaSearchConstants.ANNOTATION_TYPE, IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE, SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);
		SearchParticipant[] participants = new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()};
		IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
		
		ConstraintRequestor consReq = new ConstraintRequestor(rels);		
		engine.search(pattern, participants, scope, consReq, monitor);
		constraints.addAll(consReq.getConstraints());
		
		EffectRequestor effReq = new EffectRequestor(rels);
		for (Relation rel : rels) {
			pattern = SearchPattern.createPattern(rel.getName(), IJavaSearchConstants.ANNOTATION_TYPE, IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE, SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);
			engine.search(pattern, participants, scope, effReq, monitor);			
		}
		constraints.addAll(effReq.getConstraints());
	}

	public void populateFromXMLFile(IResource resource, Document doc, XMLContext context) {
		NodeList consNodes = doc.getDocumentElement().getElementsByTagName("Constraint");
		for (int ndx = 0; ndx < consNodes.getLength(); ndx++) {
			addConstraintFromXML(resource, context, consNodes.item(ndx));
		}
	}

	private void addConstraintFromXML(IResource resource, XMLContext context, Node item) {
		try {
			NodeList consParts = item.getChildNodes();
			Operation op = null;
			Predicate trigger = null, requires = null;
			List<Effect> effects = new LinkedList<Effect>();
			
			FPLParser parser = new FPLParser(rels, context);
			
			for (int ndx = 0; ndx < consParts.getLength(); ndx++) {
				Node node = consParts.item(ndx);
				if (!(node instanceof Element))
					continue;
				String name = node.getNodeName();
				
				if (name.equals("op")) {
					assert op == null;
					parser.reset(node.getTextContent());
					op = parser.operation();
				}
				else if (name.equals("trg")) {
					assert trigger == null;
					parser.reset(node.getTextContent());
					trigger = parser.expression();
				}
				else if (name.equals("req")) {
					assert requires == null;
					parser.reset(node.getTextContent());
					requires = parser.expression();
				}
				else if (name.equals("eff")) {
					parser.reset(node.getTextContent());
					effects.add(parser.effect());
				}
			}
			if (trigger == null)
				trigger = new TruePredicate();
			if (requires == null)
				requires = new TruePredicate();
			constraints.add(new Constraint(op, trigger, requires, effects));
		} catch (ParseException e) {
			ReportingUtility.reportParseError(resource, null, e.getMessage());
		}
	}

	public Iterator<Constraint> iterator() {
		return constraints.iterator();
	}

}
