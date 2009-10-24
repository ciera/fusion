package edu.cmu.cs.fusion.constraint;

import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.crystal.util.TypeHierarchy;

public interface Operation {
	/**
	 *Attempts to match an instruction to an operation.
	 * @param types
	 * @param instr
	 * @return null if no match is possible. If there is a match,
	 * returns a mapping from the spec variables in the operation to
	 * the variables in the instuction. There will be one entry
	 * for each spec variable returned by free vars, though they
	 * may point to the same variable if that is what the instruction
	 * maps them too.
	 */
	public ConsList<Pair<SpecVar, Variable>> matches(TypeHierarchy types, TACInstruction instr);
	
	/**
	 * @return A collection of the free variables and their types
	 */
	public FreeVars getFreeVariables();
}
