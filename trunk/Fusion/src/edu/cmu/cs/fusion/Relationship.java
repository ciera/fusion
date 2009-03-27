package edu.cmu.cs.fusion;

import java.util.Arrays;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;

/**
 * Represents a single dynamic relationship. These can be mapped to any value (true, false or unknown).
 * 
 * @author ciera
 *
 */
public class Relationship {
	private ObjectLabel[] parameters;
	
	private RelationshipType type;
	
	public Relationship(RelationshipType type, ObjectLabel[] params) {
		this.type = type;
		this.parameters = params;
	}
	
	public ObjectLabel getParam(int ndx) {
		return parameters[ndx];
	}
	
	public String getName() {
		return type.getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + Arrays.hashCode(parameters);
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
		final Relationship other = (Relationship) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (!Arrays.equals(parameters, other.parameters))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		String str =  type.getName() + "(";
		int ndx = 0;
		
		for (ObjectLabel param : parameters) {
			str += param.toString();
			if (ndx != parameters.length - 1)
				str += ",";
			ndx++;
		}
		str += ")";
		return str;
	}
}
