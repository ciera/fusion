package edu.cmu.cs.fusion.test.constraint;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.ITACTransferFunction;
import edu.cmu.cs.crystal.tac.model.ReturnInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;

public class DefaultReturnInstruction implements ReturnInstruction {

	public Variable getReturnedVariable() {
		throw new UnsupportedOperationException("Only to be used as a stub for the reporter");
	}

	public ASTNode getNode() {
		throw new UnsupportedOperationException("Only to be used as a stub for the reporter");
	}

	public <LE> LE transfer(ITACTransferFunction<LE> tf, LE value) {
		throw new UnsupportedOperationException("Only to be used as a stub for the reporter");
	}

	public <LE> IResult<LE> transfer(
			ITACBranchSensitiveTransferFunction<LE> tf, List<ILabel> labels,
			LE value) {
		throw new UnsupportedOperationException("Only to be used as a stub for the reporter");
	}

}
