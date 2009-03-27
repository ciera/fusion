package edu.cmu.cs.fusion;

public class RelationshipType {
	private String name;
	private String[] fullyQualifiedTypes;
	
	public RelationshipType(String name, String[] fullyQualifiedTypes) {
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
	
}
