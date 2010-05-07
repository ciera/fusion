package edu.cmu.cs.fusion.constraint.operations;

import org.eclipse.jdt.core.dom.IMethodBinding;

import edu.cmu.cs.crystal.tac.model.NewObjectInstruction;
import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.Binding;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.Operation;
import edu.cmu.cs.fusion.constraint.SpecVar;

public class ConstructorOp implements Operation {
	private String type;
	private String[] paramTypes;
	private SpecVar[] params;
	private SpecVar ret;

	public ConstructorOp(String type, SpecVar[] paramNames, String[] paramTypes) {
			ret = Constraint.RESULT;
			this.params = paramNames;
			this.paramTypes = paramTypes;
			this.type = type;
		}

	public FreeVars getFreeVariables() {
		return new FreeVars(params, paramTypes).addVar(ret, type);
	}

	public ConsList<Binding> matches(TypeHierarchy types, TACInstruction instr) {
		if (!(instr instanceof NewObjectInstruction))
			return null;

		NewObjectInstruction newObj = (NewObjectInstruction) instr;

		IMethodBinding method = newObj.resolveBinding();

		if (method.getParameterTypes().length != paramTypes.length)
			return null;

		if (!types.existsCommonSubtype(type, method.getDeclaringClass().getQualifiedName()))
			return null;

		for (int ndx = 0; ndx < paramTypes.length; ndx++)
			if (!types.existsCommonSubtype(paramTypes[ndx], method.getParameterTypes()[ndx].getQualifiedName()))
				return null;
		
		ConsList<Binding> vars = ConsList.empty();
		
		vars = ConsList.cons(new Binding(ret, newObj.getTarget()), vars);
		
		for (int ndx = 0; ndx < params.length; ndx++)
			vars = ConsList.cons(new Binding(params[ndx], newObj.getArgOperands().get(ndx)), vars);

		return vars;
	}
	
	public String toString() {
		String str = type + "(";
		
		for (int ndx = 0; ndx < params.length; ndx++) {
			str += paramTypes[ndx] + " " + params[ndx];
			if (ndx < params.length - 1)
				str += ", ";
		}
		str += ")";
		
		return str;
	}
}
