package edu.cmu.cs.fusion.constraint;

import java.util.LinkedList;
import java.util.List;

import edu.cmu.cs.fusion.constraint.predicates.RelationshipPredicate;

public class InferredRel {

	private Predicate pred;
	private List<Effect> effects;

	/**
	 * @param rel The relationship we want to produce
	 * @param relSub The substitution the relationship is being applied under
	 * @return A list of substitution which contains ONLY the free vars of the effect which
	 * can be produces, or empty if nothing matches.
	 */
	public List<Substitution> canProduce(RelationshipPredicate rel, Substitution relSub) {
		List<Substitution> subs = new LinkedList<Substitution>();
		for (Effect eff : effects) {		
			if (!eff.getRelation().equals(rel.getRelation()))
				continue;
			
			//is it possible for this to produce the right type of effect
			if (!eff.getType().isTest() && eff.getType().isNegative() == rel.isPositive()) 
				continue;
			
			Substitution sub = new Substitution();
			SpecVar[] relVars = rel.getVars();
			SpecVar[] effVars = eff.getVars();
			
			for (int ndx = 0; ndx < relVars.length; ndx++) {
				sub.addSub(effVars[ndx], relSub.getSub(relVars[ndx]));
			}
			subs.add(sub);
		}
		return subs;
	}
	
	public FreeVars getFreeVars() {
		FreeVars fv = pred.getFreeVariables();
		for (Effect eff : effects)
			fv = fv.union(eff.getFreeVariables());
		return fv;
	}

	public Predicate getPredicate() {
		return pred;
	}

	public List<Effect> getEffects() {
		return effects;
	}

}
