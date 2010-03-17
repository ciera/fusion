package edu.cmu.cs.fusion.relationship;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;

import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.ITACTransferFunction;
import edu.cmu.cs.crystal.tac.model.TACInvocation;
import edu.cmu.cs.crystal.tac.model.Variable;

public class EntryInstruction implements TACInvocation {

	private List<Variable> args;
	private IMethodBinding binding;

	public EntryInstruction(List<Variable> args, IMethodBinding binding) {
		super();
		this.args = args;
		this.binding = binding;
	}

	public List<Variable> getArgOperands() {
		return args;
	}

	public IMethodBinding resolveBinding() {
		return binding;
	}

	public ASTNode getNode() {
		throw new UnsupportedOperationException();
	}

	public <LE> LE transfer(ITACTransferFunction<LE> tf, LE value) {
		throw new UnsupportedOperationException();
	}

	public <LE> IResult<LE> transfer(
			ITACBranchSensitiveTransferFunction<LE> tf, List<ILabel> labels,
			LE value) {
		throw new UnsupportedOperationException();
	}

}
