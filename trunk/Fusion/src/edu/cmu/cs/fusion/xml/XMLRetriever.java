package edu.cmu.cs.fusion.xml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.Triple;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.AliasContext;
import edu.cmu.cs.fusion.FusionTypeCheckException;
import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.RelationsEnvironment;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.XMLContext;
import edu.cmu.cs.fusion.relationship.FourPointLattice;
import edu.cmu.cs.fusion.relationship.RelationshipContext;
import edu.cmu.cs.fusion.relationship.RelationshipDelta;

/**
 * Parses out the XMLSchema part of a fusion file and provides starting lattice information.
 * 
 * @author ciera
 *
 */
public class XMLRetriever implements Observer, IResourceVisitor {
	private RelationshipDelta delta;
	private Set<ObjectLabel> topLabels;
	private RelationsEnvironment rels;
	private Map<String, SchemaQueries> queries;
	private TypeHierarchy types;

	/**
	 * @param rels The populated relations environment that we are using.
	 */
	public XMLRetriever(RelationsEnvironment rels) {
		this.rels = rels;
		queries = new HashMap<String, SchemaQueries>();
	}
	
	/**
	 * Will receive a callback for every fusion file found.
	 */
	public void update(Observable o, Object arg) {
		Triple<IResource, Document, XMLContext> triple = (Triple<IResource, Document, XMLContext>)arg;
		NodeList consNodes = triple.snd().getDocumentElement().getElementsByTagName("XMLSchema");
		for (int ndx = 0; ndx < consNodes.getLength(); ndx++) {
			addQueriesFromXML(triple.fst(), triple.thrd(), consNodes.item(ndx));
		}
	}

	private void addQueriesFromXML(IResource resource, XMLContext context, Node item) {
		String schemaName = item.getAttributes().getNamedItem("schema").getNodeValue();
		NodeList nodes = item.getChildNodes();
		String localVal = null;
		String topElement = null;
		List<String> queryStrings = new LinkedList<String>();
		
		for (int ndx = 0; ndx < nodes.getLength(); ndx++) {
			Node node = nodes.item(ndx);
			if (!(node instanceof Element))
				continue;
			String name = node.getNodeName();

			if (name.equals("topElement")) {
				topElement = node.getTextContent();
			}
			else if (name.equals("locals")) {
				localVal = node.getTextContent();
			}
			else if (name.equals("query")) {
				queryStrings.add(node.getTextContent());
			}
		}
		assert (localVal != null);
		assert (!queryStrings.isEmpty());
		queries.put(schemaName, new SchemaQueries(context, rels, topElement, localVal, queryStrings));
	}
	
	/**
	 * Retrieves the relationships for all the XML files in the resource. This must be called before
	 * calling getStartContext. For each XML file found, it will find the schema to use to retrieve the relationships
	 * and load them up.
	 * 
	 * Every call to this will reset the loaded data. It should be reloaded whenever the type hierarchy changes.
	 * 
	 * @param resource the top of a resource tree
	 * @param tHierarchy the type hierarchy to use
	 * @throws CoreException
	 */
	public void retrieveRelationships(IResource resource, TypeHierarchy tHierarchy) throws CoreException {
		delta = new RelationshipDelta();
		topLabels = new HashSet<ObjectLabel>();
		types = tHierarchy;
		resource.accept(this);
	}

	/**
	 * Callback for retrieveRelationships. Do not call externally.
	 */
	public boolean visit(IResource resource) throws CoreException {
		try {
			if (resource instanceof IFile && resource.getFileExtension().equals("xml")) {
				File file = resource.getLocation().toFile();
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(true);
				
				Document doc = factory.newDocumentBuilder().parse(file);
				retrieveWithSchema(file, doc.getDocumentElement().getNamespaceURI());
				return false;
			}
				
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FusionTypeCheckException e) {
			// TODO Send typechecking error to screen
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Check to see if this XML file's schema is known. If so, load relationships from it.
	 * @param file
	 * @param schema
	 * @throws FusionTypeCheckException
	 */
	private void retrieveWithSchema(File file, String schema) throws FusionTypeCheckException {
		SchemaQueries sQueries = queries.get(schema);
		
		if (sQueries != null) {
			RelationshipDelta result = sQueries.runQueries(file, types);
			delta = RelationshipDelta.join(delta, result);
			topLabels.addAll(sQueries.findTopObjects(file, types));
		}	
	}

	/**
	 * Make sure retrieveRelationships has already been called before calling this method.
	 * 
	 * Using the loaded data, this will create an appropriate starting context based on the parameters. It will use
	 * aliases to substitute in the appropriate ObjectLabels for any known variable that should be bound (right now,
	 * this is only thisVar). All other labels will remain in the context.
	 * 
	 * @param thisVar A this variable to bind to.
	 * @param aliases The alias context to use for substitutions
	 * @return A relationship context that is based on the data retrieved from the XML files, but bound according to aliases.
	 */
	public RelationshipContext getStartContext(Variable thisVar, AliasContext aliases) {
		RelationshipContext start = new RelationshipContext(false);
		RelationshipDelta converted = new RelationshipDelta();
		Map<ObjectLabel, ObjectLabel> bindings = new HashMap<ObjectLabel, ObjectLabel>();
		
		for (ObjectLabel possibleTop : topLabels) {
			String thisType = thisVar.resolveType().getQualifiedName();
			String possibleTopType = possibleTop.getType().getQualifiedName();
			if (types.isSubtypeCompatible(thisType, possibleTopType)) {
				Set<ObjectLabel> thisAliases = aliases.getAliases(thisVar);
				assert (thisAliases.size() == 1);
				bindings.put(possibleTop, thisAliases.iterator().next());
			}
		}
		
		for (Entry<Relationship, ThreeValue> entry : delta) {
			Relationship convDelta = convertRelationship(entry.getKey(), bindings);
			converted.setRelationship(convDelta, FourPointLattice.convert(entry.getValue()));
		}
		return start.applyChangesFromDelta(converted);
	}

	private Relationship convertRelationship(Relationship original, Map<ObjectLabel, ObjectLabel> bindings) {
		Relation relation = original.getRelation();
		ObjectLabel[] newLabels = new ObjectLabel[relation.getFullyQualifiedTypes().length];
		
		for (int ndx = 0; ndx < newLabels.length; ndx++) {
			ObjectLabel lab = bindings.get(original.getParam(ndx));
			newLabels[ndx] = (lab != null) ? lab : original.getParam(ndx);
		}
		
		return new Relationship(relation, newLabels);
	}
}
