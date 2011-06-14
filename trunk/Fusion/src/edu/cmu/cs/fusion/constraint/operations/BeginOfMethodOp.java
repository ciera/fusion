package edu.cmu.cs.fusion.constraint.operations;

import org.eclipse.jdt.core.dom.IMethodBinding;

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
import edu.cmu.cs.fusion.relationship.EntryInstruction;

public class BeginOfMethodOp implements Operation {
	private String thisType;
	private String methodName;
	private String[] paramTypes;
	private SpecVar[] paramNames;
	private boolean isStatic;
	
	public BeginOfMethodOp(String thisType, String methodName, SpecVar[] paramNames, String[] paramTypes, boolean isStatic) {
		this.thisType = thisType;
		this.methodName = methodName;
		this.paramTypes = paramTypes;
		this.paramNames = paramNames;
		this.isStatic = isStatic;
	}

	public FreeVars getFreeVariables() {
		FreeVars fv = new FreeVars();
		if (!isStatic) {
			if (thisType == null)
				fv = fv.addVar(Constraint.RECEIVER, FreeVars.OBJECT_TYPE);
			else
				fv = fv.addVar(Constraint.RECEIVER, thisType);
		}
		
		if (paramNames != null) {
			fv = fv.addVars(paramNames, paramTypes);
		}
		return fv;
	}

	public ConsList<Binding> matches(TypeHierarchy types, Method method, TACInstruction instr) {
		if (!(instr instanceof EntryInstruction))
			return null;

		EntryInstruction invoke = (EntryInstruction) instr;

		IMethodBinding binding = invoke.resolveBinding();
		
		if (methodName != null && !(methodName.equals(binding.getName())))
			return null;

		if (thisType != null && !types.existsCommonSubtype(thisType, binding.getDeclaringClass().getQualifiedName()))
			return null;
		
		//it's ok if we have a receiver and we're static (we just won't use it), but not the other way around.
		if (invoke.getReceiver() == null && !isStatic) 
			return null;

		ConsList<Binding> vars = ConsList.empty();
		if (paramTypes != null) {
			if (binding.getParameterTypes().length != paramTypes.length)
				return null;
			
			Variable[] params = method.getParams();
	
			for (int ndx = 0; ndx < paramTypes.length; ndx++) {
				if (!types.existsCommonSubtype(paramTypes[ndx], binding.getParameterTypes()[ndx].getQualifiedName()))
					return null;
				vars = ConsList.cons(new Binding(paramNames[ndx], params[ndx]), vars);
			}
		}
		
		if (!isStatic)
			vars = ConsList.cons(new Binding(Constraint.RECEIVER, invoke.getReceiver()), vars);
		
		return vars;
	}
}
