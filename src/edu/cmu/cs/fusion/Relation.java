package edu.cmu.cs.fusion;

import java.util.Arrays;

public class Relation {
	private String name;
	private String[] fullyQualifiedTypes;
	
	public Relation(String name, String[] fullyQualifiedTypes) {
		this.name = name;
		this.fullyQualifiedTypes = fullyQualifiedTypes;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String[] getFullyQualifiedTypes() {
		return fullyQualifiedTypes;
	}
	public void setFullyQualifiedTypes(String[] fullyQualifiedTypes) {
		this.fullyQualifiedTypes = fullyQualifiedTypes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(fullyQualifiedTypes);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Relation other = (Relation) obj;
		if (!Arrays.equals(fullyQualifiedTypes, other.fullyQualifiedTypes))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		String str = name + "(";
		int ndx;
		
		for (ndx = 0; ndx < fullyQualifiedTypes.length - 1; ndx++)
			str += fullyQualifiedTypes[ndx] + ", ";
		if (ndx < fullyQualifiedTypes.length)
			str += fullyQualifiedTypes[ndx];
		str += ")";
		return str;	
	}
}
