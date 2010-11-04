package edu.cmu.cs.fusion.constraint;

import java.util.LinkedList;
import java.util.List;

import edu.cmu.cs.fusion.constraint.predicates.RelationshipPredicate;

public class InferredRel {

	private Predicate pred;
	private List<RelEffect> effects;
	
	public InferredRel(Predicate trigger, List<RelEffect> effects) {
		pred = trigger;
		this.effects = effects;
	}

	/**
	 * @param rel The relationship we want to produce
	 * @param relSub The substitution the relationship is being applied under
	 * @return A list of substitution which contains ONLY the free vars of the effect which
	 * can be produced, or empty if nothing matches. That is, it contains the free vars which
	 * we need to substitute to make the rel in question, but not any other free vars which might
	 * be needed for infer. These subs may overlap with relSub if relSub prescribed some already.
	 */
	public List<Substitution> canProduce(RelationshipPredicate rel, Substitution relSub) {
		List<Substitution> subs = new LinkedList<Substitution>();
		for (RelEffect eff : effects) {		
			if (!eff.getRelation().equals(rel.getRelation()))
				continue;
			
			//is it possible for this to produce the right type of effect
			if (!eff.getType().isTest() && (eff.getType().isNegative() == rel.isPositive())) 
				continue;	
			
			Substitution sub = new Substitution();
			SpecVar[] relVars = rel.getVars();
			SpecVar[] effVars = eff.getVars();
			
			for (int ndx = 0; ndx < relVars.length; ndx++) {
				sub = sub.addSub(effVars[ndx], relSub.getSub(relVars[ndx]));
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

	public List<RelEffect> getEffects() {
		return effects;
	}
	
	public String toString() {
		return pred.toString() + " IMPLIES \n" + effects.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((effects == null) ? 0 : effects.hashCode());
		result = prime * result + ((pred == null) ? 0 : pred.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InferredRel other = (InferredRel) obj;
		if (effects == null) {
			if (other.effects != null)
				return false;
		} else if (!effects.equals(other.effects))
			return false;
		if (pred == null) {
			if (other.pred != null)
				return false;
		} else if (!pred.equals(other.pred))
			return false;
		return true;
	}
}
