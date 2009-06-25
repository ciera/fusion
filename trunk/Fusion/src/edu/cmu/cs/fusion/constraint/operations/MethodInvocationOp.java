package edu.cmu.cs.fusion.constraint.operations;

import org.eclipse.jdt.core.dom.IMethodBinding;

import edu.cmu.cs.crystal.tac.model.MethodCallInstruction;
import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.fusion.TypeHierarchy;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.Operation;
import edu.cmu.cs.fusion.constraint.SpecVar;

public class MethodInvocationOp implements Operation {
	private String thisType;
	private String[] paramTypes;
	private SpecVar[] params;
	private String name;
	private SpecVar thisVar;
	private SpecVar retVar;
	private String retType;

	public MethodInvocationOp(String methodName, String thisType, SpecVar[] paramNames,
		    String[] paramTypes, String returnType) {
			thisVar = Constraint.RECEIVER;
			retVar = Constraint.RESULT;
			this.params = paramNames;
			this.paramTypes = paramTypes;
			this.retType = returnType;
			this.thisType = thisType;
			name = methodName;
		}

	public FreeVars getFreeVariables() {
		return new FreeVars(params, paramTypes).addVar(thisVar, thisType).addVar(retVar, retType);
	}

	public ConsList<Pair<SpecVar, Variable>> matches(TypeHierarchy types, TACInstruction instr) {
		if (!(instr instanceof MethodCallInstruction))
			return null;

		MethodCallInstruction invoke = (MethodCallInstruction) instr;

		//first, check to see if the instr is right for this op
		if (!name.equals(invoke.getMethodName()))
			return null;

		IMethodBinding method = invoke.resolveBinding();

		if (!types.existsCommonSubtype(thisType, method.getDeclaringClass().getQualifiedName()))
			return null;

		if (method.getParameterTypes().length != paramTypes.length)
			return null;

		for (int ndx = 0; ndx < paramTypes.length; ndx++)
			if (!types.existsCommonSubtype(paramTypes[ndx], method.getParameterTypes()[ndx].getQualifiedName()))
				return null;
		
		ConsList<Pair<SpecVar, Variable>> vars = ConsList.empty();
		
		vars = ConsList.cons(new Pair<SpecVar, Variable>(thisVar, invoke.getReceiverOperand()), vars);
		vars = ConsList.cons(new Pair<SpecVar, Variable>(retVar, invoke.getTarget()), vars);
		
		for (int ndx = 0; ndx < params.length; ndx++)
			vars = ConsList.cons(new Pair<SpecVar, Variable>(params[ndx], invoke.getArgOperands().get(ndx)), vars);

		return vars;
	}
	
	public String toString() {
		String str = thisType + "." + name + "(";
		
		for (int ndx = 0; ndx < params.length; ndx++) {
			str += paramTypes[ndx] + " " + params[ndx];
			if (ndx < params.length - 1)
				str += ", ";
		}
		str += ") : " + retType;
		
		return str;
	}
}
