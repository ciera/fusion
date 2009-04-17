package edu.cmu.cs.fusion.test.constraint.operations;

import org.eclipse.jdt.core.dom.ITypeBinding;

import edu.cmu.cs.crystal.tac.IVariableVisitor;
import edu.cmu.cs.crystal.tac.Variable;

public class StubVariable extends Variable {

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

}
