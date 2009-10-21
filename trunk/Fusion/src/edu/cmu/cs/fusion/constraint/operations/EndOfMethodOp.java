package edu.cmu.cs.fusion.constraint.operations;

import edu.cmu.cs.crystal.tac.model.ReturnInstruction;
import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.fusion.TypeHierarchy;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.Operation;
import edu.cmu.cs.fusion.constraint.SpecVar;

public class EndOfMethodOp implements Operation {
	private SpecVar var;
	
	public EndOfMethodOp(SpecVar var) {
		this.var = var;
	}
	
	public FreeVars getFreeVariables() {
		return new FreeVars().addVar(var, FreeVars.OBJECT_TYPE);
	}

	public ConsList<Pair<SpecVar, Variable>> matches(TypeHierarchy types,
			TACInstruction instr) {
		if (instr instanceof ReturnInstruction) {
			Variable retVar = ((ReturnInstruction) instr).getReturnedVariable();
			ConsList<Pair<SpecVar, Variable>> vars = ConsList.empty();
			return ConsList.cons(new Pair<SpecVar, Variable>(var, retVar), vars);
		}
		else
			return null;
	}

}
