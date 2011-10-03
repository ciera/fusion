package edu.cmu.cs.fusion.test.constraint.operations;

import org.eclipse.jdt.core.dom.ITypeBinding;

import edu.cmu.cs.crystal.tac.model.IVariableVisitor;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.fusion.xml.NamedTypeBinding;

public class StubVariable extends Variable {
	private String name;
	private NamedTypeBinding type;

	public StubVariable(String name, String type) {
		super();
		this.name = name;
		this.type = new NamedTypeBinding(type);
	}
	
	public StubVariable() {
		super();
	}
	
	@Override
	public <T> T dispatch(IVariableVisitor<T> visitor) {
		return null;
	}

	@Override
	public ITypeBinding resolveType() {
		return type;
	}
	
	@Override
	public String toString() {
		if (name != null)
			return name;
		else
			return super.toString();
	}

	public void setType(NamedTypeBinding type) {
		this.type = type;
	}

}
