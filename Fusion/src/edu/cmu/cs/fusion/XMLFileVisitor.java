package edu.cmu.cs.fusion;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.cmu.cs.fusion.constraint.ConstraintEnvironment;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.constraint.XMLContext;
import edu.cmu.cs.fusion.parsers.predicate.ParseException;

public class XMLFileVisitor implements IResourceVisitor {	
	
	private ConstraintEnvironment constraints;
	private InferenceEnvironment infers;

	public XMLFileVisitor(ConstraintEnvironment constraints, InferenceEnvironment infers) {
		this.constraints = constraints;
		this.infers = infers;
	}
	
	public boolean visit(IResource resource) throws CoreException {
		try {
			if (resource instanceof IFile && resource.getFileExtension().equals("fusion")) {
				File file = resource.getLocation().toFile();
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
				XMLContext context = createContext(file.getAbsolutePath(), doc);
				constraints.populateFromXMLFile(doc, context);
				infers.populateFromXMLFile(doc, context);
				return false;
			}
				
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return true;
	}

	private XMLContext createContext(String absolutePath, Document doc) {
		XMLContext context = new XMLContext(absolutePath);
		NodeList imports = doc.getDocumentElement().getElementsByTagName("Import");
		for (int ndx = 0; ndx < imports.getLength(); ndx++) {
			Element importElement = (Element) imports.item(ndx);
			context.addType(importElement.getAttribute("qualName"));
		}
		return context;
	}
}
