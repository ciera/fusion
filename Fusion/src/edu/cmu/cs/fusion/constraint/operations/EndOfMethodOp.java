package edu.cmu.cs.fusion.constraint.operations;

import edu.cmu.cs.crystal.tac.model.ReturnInstruction;
import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.Operation;
import edu.cmu.cs.fusion.constraint.SpecVar;

public class EndOfMethodOp implements Operation {
	public FreeVars getFreeVariables() {
		return new FreeVars();
	}

	public ConsList<Pair<SpecVar, Variable>> matches(TypeHierarchy types,
			TACInstruction instr) {
		if (instr instanceof ReturnInstruction)
			return ConsList.empty();
		else
			return null;
	}
}
