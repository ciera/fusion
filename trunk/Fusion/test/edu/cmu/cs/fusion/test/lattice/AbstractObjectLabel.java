package edu.cmu.cs.fusion.test.lattice;

import org.eclipse.jdt.core.dom.ITypeBinding;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.fusion.xml.NamedTypeBinding;

public class AbstractObjectLabel implements ObjectLabel {
	private String name;
	private ITypeBinding binding;
	
	public AbstractObjectLabel(String name) {
		this.name = name;
	}

	public AbstractObjectLabel(String name, String type) {
		this.name = name;
		binding = new NamedTypeBinding(type);
	}

	public ITypeBinding getType() {
		return binding;
	}

	public boolean isSummary() {
		return false;
	}

	public String toString() {
		return name;
	}

	public void setType(ITypeBinding type) {
		binding = type;
	}
}
