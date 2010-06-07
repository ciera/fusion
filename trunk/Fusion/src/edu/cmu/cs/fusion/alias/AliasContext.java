package edu.cmu.cs.fusion.alias;

import java.util.Set;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * The alias context at a particular instruction in the analysis.
 * @author ciera
 *
 */
public interface AliasContext extends Cloneable {
	/**
	 * @param var the variable to get aliases for
	 * @return the possible object labels. May be null if this variable is unknown to the lattice.
	 */
	public Set<ObjectLabel> getAliases(Variable var);
	
	/**
	 * 
	 * @return all object labels which are currently known. That is, returns a complete representation of the heap.
	 */
	public Set<ObjectLabel> getAllAliases();

	public Set<Variable> getVariables();
	
	public void reset(Variable var, Set<ObjectLabel> labels);
	
	//requires that object labels be somehow marked as whether they are definite or only have "potential" of being used
	//anything that is "potential" at this point is definite if it is used, and completely removed otherwise
	//public void cleanPotentialLabels();
	
	public Object clone();
}
