package edu.cmu.cs.fusion.xml;

import edu.cmu.cs.fusion.alias.ObjectLabel;


/**
 * An ObjectLabel which was generated from XML, rather than from an alias analysis or as a dummy node.
 * These labels
 * @author ciera
 *
 */
public class XMLObjectLabel implements ObjectLabel {
	private String type;
	private String name;

	public XMLObjectLabel(String name, String type) {
		this.name = name;
		this.type = type;
	}

	public boolean isSummary() {
		return false;
	}

	@Override
	public String toString() {
		return "XML:" + name; 
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XMLObjectLabel other = (XMLObjectLabel) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	public boolean isTemporary() {
		return false;
	}

	public void makePermanent() {
	}

	public String getTypeName() {
		return type;
	}
}
