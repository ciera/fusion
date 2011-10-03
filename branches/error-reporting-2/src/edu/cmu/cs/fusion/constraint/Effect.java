package edu.cmu.cs.fusion.constraint;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.relationship.RelationshipDelta;

public interface Effect {

	/**
	 * @return the free variables for this effect, including the test variable if applicable. Do not include
	 * free variables, as they must be dealt with later.
	 */
	public FreeVars getFreeVariables();

	/**
	 * @return the wild cards, as a set of free variables.
	 */
	public FreeVars getWildCards();

	/**
	 * Make a delta lattice from this effect. 
	 * @param env The enivonment for boolean values
	 * @param subs The substitution to use to create the relationship
	 * @return The resulting lattice will be bottom for
	 * all relationships except the one defined by this relationship.
	 */
	public RelationshipDelta makeEffects(FusionEnvironment env,
			Substitution subs);

	public SpecVar[] getVars();

	public String toString();

}