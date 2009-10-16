package edu.cmu.cs.fusion;

import java.util.Set;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * The alias context at a particular instruction in the analysis.
 * @author ciera
 *
 */
public interface AliasContext {
	/**
	 * @param var the variable to get aliases for
	 * @return the possible object labels. May be null if this variable is unknown to us.
	 */
	public Set<ObjectLabel> getAliases(Variable var);
	
	/**
	 * 
	 * @return all object labels which are currently known. That is, returns a complete representation of the heap.
	 */
	public Set<ObjectLabel> getAllAliases();
	
}
