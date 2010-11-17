package edu.cmu.cs.fusion.test.lattice;

import edu.cmu.cs.fusion.alias.ObjectLabel;

public class AbstractObjectLabel implements ObjectLabel {
	private String name;
	private String type;
	
	public AbstractObjectLabel(String name) {
		this.name = name;
	}

	public AbstractObjectLabel(String name, String type) {
		this.name = name;
		this.type = type;
	}


	public boolean isSummary() {
		return false;
	}

	public String toString() {
		return name;
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
