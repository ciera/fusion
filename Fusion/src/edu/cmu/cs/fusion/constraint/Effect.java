package edu.cmu.cs.fusion.constraint;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.relationship.RelationshipDelta;

public interface Effect {
	public RelationshipDelta makeEffects(FusionEnvironment env, Substitution subs);
    public FreeVars getFreeVariables();
}
