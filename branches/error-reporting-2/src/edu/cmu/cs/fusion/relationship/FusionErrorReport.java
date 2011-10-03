package edu.cmu.cs.fusion.relationship;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.constraint.predicates.AtomicPredicate;
import edu.cmu.cs.fusion.constraint.predicates.PredicateSatMap;

public class FusionErrorReport {
	private Constraint cons;
	private List<Substitution> failingVars;
	private FusionEnvironment<?> failingEnvironment;
	private boolean restrictedAliases;
	
	public FusionErrorReport(Constraint cons, List<Substitution> failingSubs, FusionEnvironment<?> env, boolean restrictedAliases) {
		this.cons = cons;
		this.failingVars = failingSubs;
		this.failingEnvironment = env;
		this.restrictedAliases = restrictedAliases;
	}

	public List<Substitution> getFailingVars() {
		return failingVars;
	}

	public FusionEnvironment<?> getFailingEnvironment() {
		return failingEnvironment;
	}

	public boolean causedRemovalOfAllAliases() {
		return restrictedAliases;
	}

	public Constraint getConstraint() {
		return cons;
	}
	/**
	 * 
	 * @param sub
	 * @param paths
	 * @return A list of strings describing each atomic predicate map for a given substitution
	 */
	public List<String> getSubPathMessages(Substitution sub, List<PredicateSatMap> paths)
	{
		List<String> strings = new java.util.LinkedList<String>();
		for(PredicateSatMap sat: paths)
		{
			//make unmodifiable or implement iterator
			StringBuffer buf = new StringBuffer();
			int i=0;
			for(Entry<AtomicPredicate, ThreeValue> entry: sat.getMap().entrySet())
			{
				buf.append(String.format("%d. %s => %s ",
						i++,entry.getKey().toHumanReadable(failingEnvironment,sub ),entry.getValue()));
			}
			strings.add(buf.toString());
		}
		return strings;		
	}
	/**
	 * 
	 * @return For each substitution   
	 * a list of strings describing each predicate map for that substitution. 
	 */
	public List<String> getAllSubPathMessages()
	{
		List<String> strings = new java.util.LinkedList<String>();
		for(Substitution sub: this.failingVars)
		{
			strings.addAll(getSubPathMessages(sub,getMaps(sub)));					
		}
		return strings;		
	}
	/**
	 * For a given substitution, a list of maps which make this error go away
	 * @param sub
	 * @return
	 */
	public List<PredicateSatMap> getMaps(Substitution sub) {
		//depends on pragmatic, sound, complete
		/*for(PredicateSatMap map:
			cons.getRequires().getSAT(failingEnvironment, sub, ThreeValue.TRUE))
		{
			if(maps.add(map))
			{
				strings.add(getSubPathMessages(sub, paths));					
			}
		}*/
		return cons.getRequires().getSAT(failingEnvironment, sub, ThreeValue.TRUE);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cons == null) ? 0 : cons.hashCode());
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
		FusionErrorReport other = (FusionErrorReport) obj;
		if (cons == null) {
			if (other.cons != null)
				return false;
		} else if (!cons.equals(other.cons))
			return false;
		return true;
	}
	


}
