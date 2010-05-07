package edu.cmu.cs.fusion.constraint.operations;

import edu.cmu.cs.crystal.tac.model.ReturnInstruction;
import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.Binding;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.Operation;

public class EndOfMethodOp implements Operation {
	public FreeVars getFreeVariables() {
		return new FreeVars();
	}

	public ConsList<Binding> matches(TypeHierarchy types,
			TACInstruction instr) {
		if (instr instanceof ReturnInstruction)
			return ConsList.empty();
		else
			return null;
	}
}
