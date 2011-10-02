package edu.cmu.cs.fusion.test.constraint.operations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.ITACTransferFunction;
import edu.cmu.cs.crystal.tac.model.NewObjectInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;

public class StubNewObjectInstruction implements NewObjectInstruction {
	private StubVariable target;
	private List<StubVariable> vars;
	private StubMethodBinding binding;
	
	public StubNewObjectInstruction() {}
	
	public StubNewObjectInstruction(List<StubVariable> vars, StubMethodBinding binding, StubVariable target) {
		this.target = target;
		this.vars = vars;
		this.binding = binding;
	}


	public ASTNode getNode() {
		return null;
	}

	public Variable getOuterObjectSpecifierOperand() {
		throw new UnsupportedOperationException("stub not complete");
	}

	public boolean hasOuterObjectSpecifier() {
		throw new UnsupportedOperationException("stub not complete");
	}

	public boolean isAnonClassType() {
		throw new UnsupportedOperationException("stub not complete");
	}

	public ITypeBinding resolveInstantiatedType() {
		throw new UnsupportedOperationException("stub not complete");
	}

	public Variable getTarget() {
		return target;
	}

	public <LE> LE transfer(ITACTransferFunction<LE> tf, LE value) {
		throw new UnsupportedOperationException("stub not complete");
	}

	public <LE> IResult<LE> transfer(
			ITACBranchSensitiveTransferFunction<LE> tf, List<ILabel> labels,
			LE value) {
		throw new UnsupportedOperationException("stub not complete");
	}

	public List<Variable> getArgOperands() {
		return new ArrayList<Variable>(vars);
	}

	public IMethodBinding resolveBinding() {
		return binding;
	}

}
