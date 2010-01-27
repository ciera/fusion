package edu.cmu.cs.fusion;

import java.io.File;
import java.io.IOException;
import java.util.Observable;

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

import edu.cmu.cs.crystal.util.Triple;
import edu.cmu.cs.fusion.constraint.XMLContext;

/**
 * Visits every fusion file in the resource tree. Calls back to registered listeners when it finds a file
 * with a fusion extension. Also loads up the XMLContext for that file.
 * 
 * Observers should expect to receive a hint with type:
 * Triple<IResource, Document, XMLContext>
 * IResource: the resource with the .fusion extension.
 * Document: the XML document in memory.
 * XMLContext: the typing context for this document, as given by the import statements.
 * @author ciera
 *
 */
public class FusionFileVisitor extends Observable  implements IResourceVisitor {	
	private static String IMPORT = "Import";
	private static String NAME = "qualName";
	private static String FUSION_FILE = "fusion";
	
	public boolean visit(IResource resource) throws CoreException {
		try {
			if (resource instanceof IFile && resource.getFileExtension().equals(FUSION_FILE)) {
				File file = resource.getLocation().toFile();
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
				XMLContext context = createContext(file.getAbsolutePath(), doc);
				setChanged();
				notifyObservers(new Triple<IResource, Document, XMLContext>(resource, doc, context));
				return false;
			}
				
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return true;
	}

	private XMLContext createContext(String absolutePath, Document doc) {
		XMLContext context = new XMLContext(absolutePath);
		NodeList imports = doc.getDocumentElement().getElementsByTagName(IMPORT);
		for (int ndx = 0; ndx < imports.getLength(); ndx++) {
			Element importElement = (Element) imports.item(ndx);
			context.addType(importElement.getAttribute(NAME));
		}
		return context;
	}
}
