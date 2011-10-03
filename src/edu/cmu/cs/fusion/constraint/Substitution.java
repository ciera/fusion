package edu.cmu.cs.fusion.constraint;

import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.fusion.alias.ObjectLabel;

/**
 * Represents a single substitution from spec variables to object labels.
 * 
 * This is an immutable class.
 * @author ciera
 *
 */
public class Substitution {
	ConsList<Pair<SpecVar, ObjectLabel>> subs;
	
	public Substitution() {
		subs = ConsList.empty();
	}
	
	private Substitution(ConsList<Pair<SpecVar, ObjectLabel>> subs) {
		this.subs = subs;
	}

	/**
	 * Assumes that var does not already exist in subs
	 * @param var
	 * @param label
	 */
	public Substitution addSub(SpecVar var, ObjectLabel label) {
		assert(var != null);
		assert(!subs.contains(var));
		return new Substitution(ConsList.cons(new Pair<SpecVar, ObjectLabel>(var, label), subs));
	}
	
	/**
	 * Assumes that vars.first does not already exist in subs
	 * @param vars A list of substitutions to concat with ours. This assumes that all elements
	 * of vars.first are new to subs so that we won't have duplicates.
	 */
	public Substitution addSeveralSub(ConsList<Pair<SpecVar, ObjectLabel>> vars) {
		return new Substitution(ConsList.concat(vars, subs));
	}

	public ObjectLabel getSub(SpecVar var) {
		for (Pair<SpecVar, ObjectLabel> pair : subs) {
			if (pair.fst().equals(var))
				return pair.snd();
		}
		return null;
	}

	public int size() {
		return subs.size();
	}

	public String toString() {return subs.toString();}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subs == null) ? 0 : subs.hashCode());
		return result;
	}
	/**
	 * Answers whether "other" is distinguishable from "this" when considering only 
	 * "relevant" SpecVars 
	 * @param other
	 * @param relevant
	 * @return true iff exists (x,y) in "this", (x,z) in "other", s.t. relevant.contains(x)
	 */
	public boolean distinguishes(Substitution other, java.util.HashSet<SpecVar> relevant)
	{
		assert(other.size()==this.size());
		for (Pair<SpecVar, ObjectLabel> pair : subs) {
			
			ObjectLabel lab = other.getSub(pair.fst());
			if (lab!=pair.snd() && relevant.contains(pair.fst()))
				return false;
		}
		return true;
	}
	/**
	 * Restricts the domain of substitutions in "subs" to the "relevant" variables,
	 *  eliminating any two subs that are not distinguishable under the new domain.
	 *  suboptimally O(n^2)
	 * @param subs
	 * @param relevant
	 * @return (x_1 ... x_n) such that for all 1<i<j<=n, x_i.distinguishes(x_j) == true
	 */
	public static java.util.List<Substitution> collapseDomain(java.util.List<Substitution> subs, SpecVar[] relevant)
	{
		java.util.List<Substitution> collapsed = new java.util.LinkedList<Substitution>();
		java.util.HashSet<SpecVar> relevantSet = new java.util.HashSet<SpecVar>();
		for(SpecVar spec: relevant)relevantSet.add(spec);
		if(subs.size()>0)
		{
			collapsed.add(subs.get(0));
			nextSub:
			for(int i=1;i<subs.size();i++)
			{
				
				Substitution possBranch = subs.get(i);
				for(Substitution sub: collapsed)
					if(sub.distinguishes(possBranch, relevantSet)==false)
						continue nextSub;
				collapsed.add(possBranch);
			}
		}
		return collapsed;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Substitution other = (Substitution) obj;
		if (subs == null) {
			if (other.subs != null)
				return false;
		} else if (!subs.equals(other.subs))
			return false;
		return true;
	}
}
