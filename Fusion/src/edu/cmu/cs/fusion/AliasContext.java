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
	 * Returns the possible object labels. May be null.
	 * @param var the variable to get aliases for
	 * @param existing the existing aliases we should assume have already been made.
	 * @return
	 */
	public Set<ObjectLabel> getAliases(Variable var);
	
	public Set<ObjectLabel> getAllAliases();
	
}
