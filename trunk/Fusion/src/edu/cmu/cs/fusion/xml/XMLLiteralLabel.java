package edu.cmu.cs.fusion.xml;

import edu.cmu.cs.crystal.analysis.alias.LiteralLabel;

/**
 * An ObjectLabel which was generated from XML, rather than from an alias analysis or as a dummy node.
 * These labels
 * @author ciera
 *
 */
public class XMLLiteralLabel extends LiteralLabel {
	public XMLLiteralLabel(Object literal, String type) {
		super(literal, new NamedTypeBinding(type));
	}
}
