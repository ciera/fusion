package edu.cmu.cs.fusion.constraint.operations;

import org.eclipse.jdt.core.dom.IMethodBinding;

import edu.cmu.cs.crystal.tac.model.ReturnInstruction;
import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.Binding;
import edu.cmu.cs.fusion.Method;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.Operation;
import edu.cmu.cs.fusion.constraint.SpecVar;

public class EndOfMethodOp implements Operation {
	private String thisType;
	private String methodName;
	private SpecVar[] paramNames;
	private String[] paramTypes;
	private String resType;
	
	public EndOfMethodOp(String thisType, String methodName, SpecVar[] paramNames,
			String[] paramTypes, String resType) {
		this.thisType = thisType;
		this.methodName = methodName;
		this.paramNames = paramNames;
		this.paramTypes = paramTypes;
		this.resType = resType;
	}

	public FreeVars getFreeVariables() {
		FreeVars fv = new FreeVars();
	
		fv = fv.addVar(Constraint.RECEIVER, thisType != null ? thisType : FreeVars.OBJECT_TYPE);
		
		if (resType != null)
			fv = fv.addVar(Constraint.RESULT, resType);
		
		if (paramTypes != null)
			fv = fv.addVars(paramNames, paramTypes);
		
		return fv;
	}

	public ConsList<Binding> matches(TypeHierarchy types, Method method, 
			TACInstruction instr) {
		if (!(instr instanceof ReturnInstruction))
			return null;

		ReturnInstruction invoke = (ReturnInstruction) instr;

		IMethodBinding binding = method.getBinding();
		
		if (methodName != null && !(methodName.equals(binding.getName())))
			return null;

		if (thisType != null && !types.existsCommonSubtype(thisType, binding.getDeclaringClass().getQualifiedName()))
			return null;
		
		if (invoke.getReturnedVariable() != null) {
			String resVarType = invoke.getReturnedVariable().resolveType().getQualifiedName();
			if (resType != null && !types.existsCommonSubtype(resType, resVarType))
				return null;
		}

		ConsList<Binding> vars = ConsList.empty();
		Variable[] params = method.getParams();
		
		if (paramTypes != null) {
			if (binding.getParameterTypes().length != paramTypes.length)
				return null;
			
			for (int ndx = 0; ndx < paramTypes.length; ndx++) {
				if (!types.existsCommonSubtype(paramTypes[ndx], binding.getParameterTypes()[ndx].getQualifiedName()))
					return null;
				vars = ConsList.cons(new Binding(paramNames[ndx], params[ndx]), vars);
			}
		}

		if (invoke.getReturnedVariable() != null && resType != null)
			vars = ConsList.cons(new Binding(Constraint.RESULT, invoke.getReturnedVariable()), vars);
		
		return ConsList.cons(new Binding(Constraint.RECEIVER, method.getThisVar()), vars);
	}
}
