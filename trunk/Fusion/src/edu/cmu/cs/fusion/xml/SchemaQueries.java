package edu.cmu.cs.fusion.xml;

import java.io.File;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;

import net.sf.saxon.Configuration;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.xqj.SaxonXQDataSource;

import org.eclipse.jdt.core.JavaModelException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.crystal.util.Utilities;
import edu.cmu.cs.fusion.FusionTypeCheckException;
import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.RelationsEnvironment;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.alias.ObjectLabel;
import edu.cmu.cs.fusion.annot.Relation.Effect;
import edu.cmu.cs.fusion.constraint.XMLContext;
import edu.cmu.cs.fusion.relationship.RelationshipDelta;
import edu.cmu.cs.fusion.relationship.SevenPointLattice;

/**
 * This class holds the XQuery from a particular schema. Given a file of that schema, and a type hierarchy,
 * it can be used to query the data in the file and produce various results in the FUSION model.
 * 
 * @author ciera
 *
 */
public class SchemaQueries {
	private static String DOC = "doc";

	private RelationsEnvironment relEnv;
	private XMLContext context;
	private String topStr;
	private String localStr;
	private List<String> queryStrings;	
	
	/**
	 * Create a new schema query based on the info from fusion file.
	 * @param context The XMLContext, which contains all of our imports and classpath info
	 * @param relEnv The relations environment
	 * @param topElement The string representing the query for the top-level elements
	 * @param localVal The string representing any local functions or namepsaces
	 * @param queryStrings The list of query strings for relationships
	 */
	public SchemaQueries(XMLContext context, RelationsEnvironment relEnv, String topElement, String localVal,
			List<String> queryStrings) {
		topStr = topElement;
		localStr = localVal;
		this.queryStrings = queryStrings;
		this.context = context;
		this.relEnv = relEnv;
		
		if (localStr == null)
			localStr = "";
	}

	/**
	 * Run each of our queries on this file, and produce a list of relationship changes
	 * @param file The XML file to run the queries on. This is assumed to have the same schema as the one that was used
	 * to create this SchemaQueries object
	 * @param types A type hierarchy to use for subtyping checks.
	 * @return A representation of the changes to the starting context that should be made.
	 * @throws FusionTypeCheckException 
	 */
	public RelationshipDelta runQueries(File file, TypeHierarchy types) throws FusionTypeCheckException {
		List<RelationshipDelta> deltas = new LinkedList<RelationshipDelta>();
		try {
			SaxonXQDataSource data = new SaxonXQDataSource();
			Configuration config = data.getConfiguration();
			XQConnection conn = data.getConnection();
			config.registerExtensionFunction(new TypeComparisonDefinition(types));

			for (String query : queryStrings) {
				RelationshipDelta result;
				String complete = localStr + query;
				StringReader reader = new StringReader(complete);
				
				XQPreparedExpression exp = conn.prepareExpression(reader);
				String filename = file.getAbsolutePath().replace('\\', '/');
				exp.bindString(new QName(DOC), filename, null);
				
				XQResultSequence queryResult = exp.executeQuery();
				result = processRelationshipResults(queryResult, types);
				deltas.add(result);
			}
			
		} catch (XQException e) {
			throw new FusionTypeCheckException(context.getFullyQualifiedName(), file, e);
		} catch (JavaModelException e) {
			throw new FusionTypeCheckException(context.getFullyQualifiedName(), file, e);
		} catch (XPathException e) {
			throw new FusionTypeCheckException(context.getFullyQualifiedName(), file, e);
		}
		return !deltas.isEmpty() ? RelationshipDelta.joinAlt(deltas) : new RelationshipDelta();
	}
	
	/**
	 * Run the top query and produce a list of objects that are "top-level" objects in this file
	 * @param file The XML file to run the top query on. This is assumed to have the same schema as the one that was used
	 * to create this SchemaQueries object
	 * @param types A type hierarchy to use for subtyping checks.
	 * @return A list of ObjectLabels, which will have an associated type.
	 * @throws FusionTypeCheckException 
	 */
	public List<ObjectLabel> findTopObjects(File file, TypeHierarchy types) throws FusionTypeCheckException {
		if (topStr == null)
			return new LinkedList<ObjectLabel>();
		
		try {
			SaxonXQDataSource data = new SaxonXQDataSource();
			Configuration config = data.getConfiguration();
			XQConnection conn = data.getConnection();
			config.registerExtensionFunction(new TypeComparisonDefinition(types));

			String complete = localStr + topStr;
			StringReader reader = new StringReader(complete);
				
			XQPreparedExpression exp = conn.prepareExpression(reader);
			exp.bindString(new QName(DOC), file.getAbsolutePath().replace('\\', '/'), null);
				
			XQResultSequence queryResult = exp.executeQuery();
			return processBindingResults(queryResult);
		} catch (XQException e) {
			throw new FusionTypeCheckException(e);
		} catch (XPathException e) {
			throw new FusionTypeCheckException(e);
		} 
	}


	/**
	 * Process the results, where results is a list of <Object name="??" type="??"/>
	 */
	private List<ObjectLabel> processBindingResults(XQResultSequence results) throws XQException {
		List<ObjectLabel> tops = new LinkedList<ObjectLabel>();
		while (results.next()) {
			Element topElement = (Element)results.getObject();
			String labelName = topElement.getAttribute("name");
			String typeName = topElement.getAttribute("type");
			tops.add(new XMLObjectLabel(labelName, typeName));
		}
		return tops;
	}

	/**
	 * Process the results, where results is a list of <Relationship name="??" effect="??"> with objects in them
	 * @throws FusionTypeCheckException 
	 */
	private RelationshipDelta processRelationshipResults(XQResultSequence results, TypeHierarchy types) throws JavaModelException, XQException, FusionTypeCheckException {
		RelationshipDelta delta = new RelationshipDelta();
		while (results.next()) {
			Element relElement = (Element) results.getObject();
			String relName = relElement.getAttribute("name");
			Effect effect = Effect.valueOf(relElement.getAttribute("effect"));
			if (effect == null || effect == Effect.TEST)
				throw new FusionTypeCheckException(effect);
			
			relName = Utilities.resolveType(context, relName);
			Relation relType = relEnv.findRelation(relName);
			if (relType == null)
				throw new FusionTypeCheckException(relName);
			
			ObjectLabel[] labArr = getLabels(relElement, relType, types);
			Relationship rel = new Relationship(relType, labArr);
			
			delta.setRelationship(rel, effect == Effect.ADD ? SevenPointLattice.TRU : SevenPointLattice.FAL);
		}
		return delta;
	}

	/**
	 * Process the objects within a relation element, given the associated relation.
	 * Also do typechecking here.
	 * @throws FusionTypeCheckException 
	 */
	private ObjectLabel[] getLabels(Element relElement, Relation relation, TypeHierarchy types) throws FusionTypeCheckException {
		NodeList nodes = relElement.getElementsByTagName("Object");
		String[] relTypes = relation.getFullyQualifiedTypes();
		
		if (relTypes.length != nodes.getLength())
			throw new FusionTypeCheckException(relation, relTypes.length, nodes.getLength());
		ObjectLabel[] labels = new ObjectLabel[relTypes.length];
		
		for (int ndx = 0; ndx < nodes.getLength(); ndx++) {
			Element node = (Element) nodes.item(ndx);
			String name = node.getAttribute("name");
			String type = node.getAttribute("type");
			labels[ndx] = createLabel(name, type);
			
			if (!(types.isSubtypeCompatible(type, relTypes[ndx])))
				throw new FusionTypeCheckException(relation, ndx, type, name);
		}
		return labels;
	}

	private ObjectLabel createLabel(String name, String type) {
		if (type.equals("java.lang.String")) {
			return new XMLLiteralLabel(name, type);
		}
		else if (type.indexOf('.') != -1) {
			return new XMLObjectLabel(name, type);
		}
		else if (type.equals("int")) {
			return new XMLLiteralLabel(Integer.parseInt(name), type);
		}
		else if (type.equals("double")) {
			return new XMLLiteralLabel(Double.parseDouble(name), type);
		}
		else if (type.equals("char")) {
			assert (name.length() == 1);
			return new XMLLiteralLabel(name.charAt(0), type);
		}
		else if (type.equals("boolean")) {
			return new XMLLiteralLabel(Boolean.parseBoolean(name), type);
		}
		else if (type.equals("short")) {
			return new XMLLiteralLabel(Short.parseShort(name), type);
		}
		else if (type.equals("long")) {
			return new XMLLiteralLabel(Long.parseLong(name), type);
		}
		else if (type.equals("float")) {
			return new XMLLiteralLabel(Float.parseFloat(name), type);
		}
		else
			return new XMLObjectLabel(name, type);
	}
}
