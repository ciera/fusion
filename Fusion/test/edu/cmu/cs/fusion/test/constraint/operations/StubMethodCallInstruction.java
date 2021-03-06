package edu.cmu.cs.fusion.test.constraint.operations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;

import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.ITACTransferFunction;
import edu.cmu.cs.crystal.tac.model.MethodCallInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;

public class StubMethodCallInstruction implements MethodCallInstruction {
	private String methodName;
	private StubVariable receiver;
	private StubVariable target;
	private List<StubVariable> vars;
	private StubMethodBinding binding;
	
	public StubMethodCallInstruction() {}
	
	public StubMethodCallInstruction(String methodName, StubVariable receiver,
			List<StubVariable> vars, StubMethodBinding binding, StubVariable target) {
		this.methodName = methodName;
		this.receiver = receiver;
		this.target = target;
		this.vars = vars;
		this.binding = binding;
	}

	public String getMethodName() {return methodName;}

	public Variable getReceiverOperand() {return receiver;}
	
	public Variable getTarget() {return target;}
	
	public List<Variable> getArgOperands() {return new ArrayList<Variable>(vars);}

	public IMethodBinding resolveBinding() {return binding;}

	public boolean isStaticMethodCall() {
		return false;
	}

	public boolean isSuperCall() {
		throw new UnsupportedOperationException("stub not complete");
	}

	public ASTNode getNode() {
		return null;
	}


	public <LE> LE transfer(ITACTransferFunction<LE> tf, LE value) {
		throw new UnsupportedOperationException("stub not complete");
	}

	public <LE> IResult<LE> transfer(
			ITACBranchSensitiveTransferFunction<LE> tf, List<ILabel> labels,
			LE value) {
		throw new UnsupportedOperationException("stub not complete");
	}
}
