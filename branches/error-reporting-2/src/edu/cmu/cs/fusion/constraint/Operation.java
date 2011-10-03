package edu.cmu.cs.fusion.constraint;

import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.Binding;
import edu.cmu.cs.fusion.Method;

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
	public ConsList<Binding> matches(TypeHierarchy types, Method method, TACInstruction instr);
	
	/**
	 * @return A collection of the free variables and their types
	 */
	public FreeVars getFreeVariables();
}
