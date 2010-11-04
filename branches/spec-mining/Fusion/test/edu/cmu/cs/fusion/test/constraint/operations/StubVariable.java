package edu.cmu.cs.fusion.test.constraint.operations;

import org.eclipse.jdt.core.dom.ITypeBinding;

import edu.cmu.cs.crystal.tac.model.IVariableVisitor;
import edu.cmu.cs.crystal.tac.model.Variable;

public class StubVariable extends Variable {
	private String name;

	public StubVariable(String name) {
		super();
		this.name = name;
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
		return null;
	}
	
	@Override
	public String toString() {
		if (name != null)
			return name;
		else
			return super.toString();
	}

}
