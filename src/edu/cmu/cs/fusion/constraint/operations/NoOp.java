package edu.cmu.cs.fusion.constraint.operations;

import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.Binding;
import edu.cmu.cs.fusion.Method;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.Operation;

public class NoOp implements Operation {
	public NoOp() {
	}

	public FreeVars getFreeVariables() {
		return new FreeVars();
	}

	public ConsList<Binding> matches(TypeHierarchy types, Method method, TACInstruction instr) {
		if (instr != null)
			return null;
		else
			return ConsList.empty();
	}
}
