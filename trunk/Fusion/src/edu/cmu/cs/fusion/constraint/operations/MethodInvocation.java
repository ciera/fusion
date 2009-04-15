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

public class MethodInvocation implements Operation {
	public String thisType;
	public String[] paramTypes;
	public SpecVar[] params;
	public String name;
	public SpecVar thisVar;
	public SpecVar retVar;
	public String retType;

	public MethodInvocation(String thisBindingKey, String methodName, String[] paramNames,
		    String[] paramBindingKeys) {
			thisVar = new SpecVar(Constraint.TARGET);
			retVar = new SpecVar(Constraint.RESULT);
			params = new SpecVar[paramNames.length];

			for (int ndx = 0; ndx < paramNames.length; ndx++)
				params[ndx] = (new SpecVar(paramNames[ndx]));

		}

	public FreeVars getFreeVariables() {
		return new FreeVars(params, paramTypes).addVar(thisVar, thisType).addVar(retVar, retType);
	}

	public Map<SpecVar, Variable> matches(TACInstruction instr) {
		if (!(instr instanceof MethodCallInstruction))
			return null;

		MethodCallInstruction invoke = (MethodCallInstruction) instr;
		IMethodBinding method = invoke.resolveBinding();

		if (!name.equals(method.getName()))
			return null;

		if (!Utils.isSubtypeCompatible(thisType, method.getDeclaringClass().getQualifiedName()))
			return null;

		if (method.getParameterTypes().length != paramTypes.length)
			return null;

		for (int ndx = 0; ndx < paramTypes.length; ndx++)
			if (!Utils.isSubtypeCompatible(paramTypes[ndx], method.getParameterTypes()[ndx]
			    .getKey()))
				return null;
		
		Map<SpecVar, Variable> map = new HashMap<SpecVar, Variable>();
		
		map.put(thisVar, invoke.getTarget());
		map.put(retVar, invoke.getReceiverOperand());
		
		for (int ndx = 0; ndx < params.length; ndx++)
			map.put(params[ndx], invoke.getArgOperands().get(ndx));

		return map;
	}
}
