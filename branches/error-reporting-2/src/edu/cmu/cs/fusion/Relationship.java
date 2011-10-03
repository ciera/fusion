package edu.cmu.cs.fusion;

import java.util.Arrays;

import edu.cmu.cs.fusion.alias.ObjectLabel;

/**
 * Represents a single dynamic relationship. These can be mapped to any value (true, false or unknown).
 * 
 * @author ciera
 *
 */
public class Relationship {
	private ObjectLabel[] parameters;
	private Relation type;
	/** Since relationships are immutable and we do hashing on them a lot, store the hashCode! */
	private int hashCode = 0;

	public Relationship(Relation type, ObjectLabel[] params) {
		this.type = type;
		this.parameters = params;
	}

	public ObjectLabel getParam(int ndx) {
		return parameters[ndx];
	}

	public Relation getRelation() {
		return type;
	}

	public String getName() {
		return type.getName();
	}

	@Override
	public int hashCode() {
		if (hashCode == 0) {
			final int prime = 31;
			hashCode = 1;
			hashCode = prime * hashCode + ((type == null) ? 0 : type.hashCode());
			hashCode = prime * hashCode + Arrays.hashCode(parameters);
		}
		return hashCode;
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

	public String toString(boolean qualified) {
		String str = type.getName();
		int last = str.lastIndexOf('.');
		if (qualified && last != -1 && last + 1 < str.length()) {
			str = str.substring(last + 1);
		}
		str += "(";
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

	@Override
	public String toString() {
		return toString(false);
	}

	public String toErrorString() {
		String str = type.getName().substring(type.getName().lastIndexOf('.') + 1) + "(";
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
