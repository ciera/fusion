package edu.cmu.cs.fusion.constraint.operations;

import org.eclipse.jdt.core.dom.IMethodBinding;

import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.Binding;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.Operation;
import edu.cmu.cs.fusion.relationship.EntryInstruction;

public class BeginOfMethodOp implements Operation {
	private String thisType;
	private String methodName;
	private String[] paramTypes;
	
	public BeginOfMethodOp(String thisType, String methodName, String[] paramTypes) {
		this.thisType = thisType;
		this.methodName = methodName;
		this.paramTypes = paramTypes;
	}

	public FreeVars getFreeVariables() {
		return new FreeVars().addVar(Constraint.RECEIVER, thisType);
	}

	public ConsList<Binding> matches(TypeHierarchy types, TACInstruction instr) {
		if (!(instr instanceof EntryInstruction))
			return null;

		EntryInstruction invoke = (EntryInstruction) instr;

		IMethodBinding method = invoke.resolveBinding();
		
		if (!(methodName.equals(method.getName())))
			return null;

		if (!types.existsCommonSubtype(thisType, method.getDeclaringClass().getQualifiedName()))
			return null;

		if (method.getParameterTypes().length != paramTypes.length)
			return null;

		for (int ndx = 0; ndx < paramTypes.length; ndx++)
			if (!types.existsCommonSubtype(paramTypes[ndx], method.getParameterTypes()[ndx].getQualifiedName()))
				return null;
		
		ConsList<Binding> vars = ConsList.empty();
		
		vars = ConsList.cons(new Binding(Constraint.RECEIVER, invoke.getReceiver()), vars);

		return vars;
	}
}
