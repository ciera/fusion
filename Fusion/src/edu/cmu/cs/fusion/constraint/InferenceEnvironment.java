package edu.cmu.cs.fusion.constraint;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

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

import edu.cmu.cs.crystal.util.Triple;
import edu.cmu.cs.fusion.RelationsEnvironment;
import edu.cmu.cs.fusion.ReportingUtility;
import edu.cmu.cs.fusion.constraint.requestors.InferRequestor;
import edu.cmu.cs.fusion.parsers.predicate.FPLParser;
import edu.cmu.cs.fusion.parsers.predicate.ParseException;

public class InferenceEnvironment implements Iterable<InferredRel>, Observer{
	private Set<InferredRel> inferRules;
	private RelationsEnvironment rels;

	/**
	 * for testing purposes only
	 * @param rels
	 */
	@Deprecated
	public InferenceEnvironment() {
		inferRules = new HashSet<InferredRel>();
	}

	public InferenceEnvironment(RelationsEnvironment rels) {
		inferRules = new HashSet<InferredRel>();
		this.rels = rels;
	}
	
	public Iterator<InferredRel> iterator() {
		return inferRules.iterator();
	}

	public void addRule(InferredRel inf) {
		inferRules.add(inf);
	}

	public void populate(IProgressMonitor monitor) throws CoreException {
		SearchEngine engine = new SearchEngine();
		SearchPattern pattern = SearchPattern.createPattern("Infer", IJavaSearchConstants.ANNOTATION_TYPE, IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE, SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);
		SearchParticipant[] participants = new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()};
		IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
		InferRequestor requestor = new InferRequestor(rels);
		
		engine.search(pattern, participants, scope, requestor, monitor);
		inferRules.addAll(requestor.getRules());
	}

	public void update(Observable o, Object arg) {
		Triple<IResource, Document, XMLContext> triple = (Triple<IResource, Document, XMLContext>)arg;
		NodeList consNodes = triple.snd().getDocumentElement().getElementsByTagName("Infer");
		for (int ndx = 0; ndx < consNodes.getLength(); ndx++) {
			addInferFromXML(triple.fst(), triple.thrd(), consNodes.item(ndx));
		}
	}

	private void addInferFromXML(IResource resource, XMLContext context, Node item) {
		try {
			NodeList consParts = item.getChildNodes();
			Predicate trigger = null;
			List<Effect> effects = new LinkedList<Effect>();
			
			FPLParser parser = new FPLParser(rels, context);
			
			for (int ndx = 0; ndx < consParts.getLength(); ndx++) {
				Node node = consParts.item(ndx);
				if (!(node instanceof Element))
					continue;
				String name = node.getNodeName();
				
				if (name.equals("trg")) {
					assert trigger == null;
					parser.reset(node.getTextContent());
					trigger = parser.expression();
				}
				else if (name.equals("eff")) {
					parser.reset(node.getTextContent());
					effects.add(parser.effect());
				}
			}
			assert (trigger != null);
			assert (!effects.isEmpty());
			inferRules.add(new InferredRel(trigger, effects));
		} catch (ParseException e) {
			ReportingUtility.reportParseError(resource, null, e.getMessage());
		}
	}
}
