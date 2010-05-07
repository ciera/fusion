package edu.cmu.cs.fusion.constraint;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.relationship.RelationshipDelta;

public class AliasEffect implements Effect {
	private SpecVar var;
	private SpecVar right;
	
	public AliasEffect(SpecVar var) {
		assert(!var.isWildCard());
		this.var = var;
	}
	
	public FreeVars getFreeVariables() {
		return new FreeVars().addVar(var, FreeVars.OBJECT_TYPE);
	}

	public SpecVar[] getVars() {
		return new SpecVar[] {var};
	}

	public FreeVars getWildCards() {
		return new FreeVars();
	}

	public RelationshipDelta makeEffects(FusionEnvironment env,
			Substitution subs) {
		RelationshipDelta delta = new RelationshipDelta();
		delta = delta.addUpdate(var, subs.getSub(var));
		
		return delta;
	}

}
