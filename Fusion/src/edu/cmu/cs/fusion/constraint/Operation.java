package edu.cmu.cs.fusion.constraint;

import edu.cmu.cs.crystal.tac.TACInstruction;
import edu.cmu.cs.fusion.FusionEnvironment;

public interface Operation {
	public boolean matches(TACInstruction instr);
	
	public SubPair getSubstitutions(TACInstruction instr, FusionEnvironment env, FreeVars fv);
	
	public FreeVars getFreeVariables();
}
