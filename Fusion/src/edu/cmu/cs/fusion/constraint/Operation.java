package edu.cmu.cs.fusion.constraint;

import java.util.Map;

import edu.cmu.cs.crystal.tac.TACInstruction;
import edu.cmu.cs.crystal.tac.Variable;

public interface Operation {
	/**
	 *Attempts to match an instruction to an operation.
	 * @param instr
	 * @return null if no match is possible. If there is a match,
	 * returns a mapping from the spec variables in the operation to
	 * the variables in the instuction. There will be one entry
	 * for each spec variable returned by free vars, though they
	 * may point to the same variable if that is what the instruction
	 * maps them too.
	 */
	public Map<SpecVar, Variable> matches(TACInstruction instr);
	
	/**
	 * Return the free variables and types
	 * @return
	 */
	public FreeVars getFreeVariables();
}
