package edu.cmu.cs.fusion.constraint.operations;

import org.eclipse.jdt.core.dom.IMethodBinding;

import edu.cmu.cs.crystal.tac.model.MethodCallInstruction;
import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.Binding;
import edu.cmu.cs.fusion.Method;
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
	boolean isStatic;

	public MethodInvocationOp(String methodName, String thisType, SpecVar[] paramNames,
		    String[] paramTypes, String returnType, boolean isStatic) {
			thisVar = Constraint.RECEIVER;
			retVar = Constraint.RESULT;
			this.params = paramNames;
			this.paramTypes = paramTypes;
			this.retType = returnType;
			this.thisType = thisType;
			name = methodName;
			this.isStatic = isStatic;
		}

	public FreeVars getFreeVariables() {
		FreeVars vars = new FreeVars(params, paramTypes).addVar(retVar, retType);
		if (!isStatic)
			vars = vars.addVar(thisVar, thisType);
		return vars;
	}

	public ConsList<Binding> matches(TypeHierarchy types, Method method, TACInstruction instr) {
		if (!(instr instanceof MethodCallInstruction))
			return null;

		MethodCallInstruction invoke = (MethodCallInstruction) instr;

		//first, check to see if the instr is right for this op
		if (!name.equals(invoke.getMethodName()))
			return null;
		
		if (isStatic != invoke.isStaticMethodCall())
			return null;

		IMethodBinding binding = invoke.resolveBinding();

		if (!types.existsCommonSubtype(thisType, invoke.getReceiverOperand().resolveType().getQualifiedName()))
			return null;

		if (binding.getParameterTypes().length != paramTypes.length)
			return null;

		for (int ndx = 0; ndx < paramTypes.length; ndx++)
			if (!types.existsCommonSubtype(paramTypes[ndx], invoke.getArgOperands().get(ndx).resolveType().getQualifiedName()))
				return null;
		
		ConsList<Binding> vars = ConsList.empty();
		
		if (!isStatic)	
			vars = ConsList.cons(new Binding(thisVar, invoke.getReceiverOperand()), vars);
		vars = ConsList.cons(new Binding(retVar, invoke.getTarget()), vars);
		
		for (int ndx = 0; ndx < params.length; ndx++)
			vars = ConsList.cons(new Binding(params[ndx], invoke.getArgOperands().get(ndx)), vars);

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
