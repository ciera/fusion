package edu.cmu.cs.fusion.xml;

import org.eclipse.jdt.core.dom.ITypeBinding;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;

/**
 * An ObjectLabel which was generated from XML, rather than from an alias analysis or as a dummy node.
 * These labels
 * @author ciera
 *
 */
public class XMLObjectLabel implements ObjectLabel {
	private ITypeBinding type;
	private String name;

	public XMLObjectLabel(String name, String type) {
		this.name = name;
		this.type = new NamedTypeBinding(type);
	}

	/**
	 * @return a false ITypeBinding, which can only be used to get the fully qualified name.
	 */
	public ITypeBinding getType() {
		return type;
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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
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

	public void setType(ITypeBinding type) {
		this.type = type;
	}
}
