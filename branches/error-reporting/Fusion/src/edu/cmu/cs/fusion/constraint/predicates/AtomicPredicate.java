package edu.cmu.cs.fusion.constraint.predicates;

import java.util.List;

import edu.cmu.cs.crystal.tac.model.SourceVariable;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.alias.ObjectLabel;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.Substitution;

public abstract class AtomicPredicate implements NegatablePredicate{	

	@Override
	public List<PredicateSatMap> getSAT(FusionEnvironment env, Substitution sub, ThreeValue target) {
		ThreeValue truth = this.getTruth(env, sub);
		List<PredicateSatMap> list = new java.util.LinkedList<PredicateSatMap>();
		
		if(target == truth)
		{
			list.add(PredicateSatMap.EMPTY);
		}
		else
		{
			list.add(new PredicateSatMap(this,target));
		}
		return list;
	}
	@Override
	public final ThreeValue getTruth(FusionEnvironment env, Substitution sub)
	{
		PredicateSatMap map = env.getOverrideMap();
		ThreeValue truth = null; 
		if(map!=null) 
			truth = map.get(this);
		if (truth == null)
			truth = this.getRawTruth(env, sub); 
		return truth;
	}
	public abstract  ThreeValue getRawTruth(FusionEnvironment env, Substitution sub);
	
	
	
}
