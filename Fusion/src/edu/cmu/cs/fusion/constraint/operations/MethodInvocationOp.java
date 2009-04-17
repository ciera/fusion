package edu.cmu.cs.fusion.constraint.operations;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.IMethodBinding;

import edu.cmu.cs.crystal.tac.MethodCallInstruction;
import edu.cmu.cs.crystal.tac.TACInstruction;
import edu.cmu.cs.crystal.tac.Variable;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.Utils;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.Operation;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.SubPair;

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
			thisVar = new SpecVar(Constraint.TARGET);
			retVar = new SpecVar(Constraint.RESULT);
			this.params = paramNames;
			this.paramTypes = paramTypes;
			this.retType = returnType;
			this.thisType = thisType;
			name = methodName;
		}

	public FreeVars getFreeVariables() {
		return new FreeVars(params, paramTypes).addVar(thisVar, thisType).addVar(retVar, retType);
	}

	public Map<SpecVar, Variable> matches(TACInstruction instr) {
		if (!(instr instanceof MethodCallInstruction))
			return null;

		MethodCallInstruction invoke = (MethodCallInstruction) instr;

		//first, check to see if the instr is right for this op
		if (!name.equals(invoke.getMethodName()))
			return null;

		IMethodBinding method = invoke.resolveBinding();

		if (!Utils.isSubtypeCompatible(thisType, method.getDeclaringClass().getQualifiedName()))
			return null;

		if (method.getParameterTypes().length != paramTypes.length)
			return null;

		for (int ndx = 0; ndx < paramTypes.length; ndx++)
			if (!Utils.isSubtypeCompatible(paramTypes[ndx], method.getParameterTypes()[ndx].getQualifiedName()))
				return null;
		
		Map<SpecVar, Variable> map = new HashMap<SpecVar, Variable>();
		
		map.put(thisVar, invoke.getTarget());
		map.put(retVar, invoke.getReceiverOperand());
		
		for (int ndx = 0; ndx < params.length; ndx++)
			map.put(params[ndx], invoke.getArgOperands().get(ndx));

		return map;
	}
}
