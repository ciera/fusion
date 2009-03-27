package edu.cmu.cs.fusion.test.lattice;

import org.eclipse.jdt.core.dom.ITypeBinding;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;

public class AbstractObjectLabel implements ObjectLabel {
	private String name;
	
	public AbstractObjectLabel(String name) {
		this.name = name;
	}
	
	public ITypeBinding getType() {
		return null;
	}

	public boolean isSummary() {
		return false;
	}

	public String toString() {
		return name;
	}
}
