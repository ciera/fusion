package edu.cmu.cs.fusion.test.constraint.operations;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.ITACTransferFunction;
import edu.cmu.cs.crystal.tac.model.ReturnInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;

public class StubReturnInstruction implements ReturnInstruction {
	private Variable retVar;

	public StubReturnInstruction(Variable ret) {
		this.retVar = ret;
	}
	
	@Override
	public Variable getReturnedVariable() {
		return retVar;
	}

	@Override
	public ASTNode getNode() {
		return null;
	}

	@Override
	public <LE> LE transfer(ITACTransferFunction<LE> tf, LE value) {
		throw new UnsupportedOperationException("stub not complete");	}

	@Override
	public <LE> IResult<LE> transfer(
			ITACBranchSensitiveTransferFunction<LE> tf, List<ILabel> labels,
			LE value) {
		throw new UnsupportedOperationException("stub not complete");
	}

}
