package edu.cmu.cs.fusion.constraint.predicates;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.sun.xml.internal.bind.annotation.OverrideAnnotationOf;

import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.SpecVar;

/**
 * 
 * @author Ernesto Alfonso
 * Immutable class representing a mapping of atomic predicates to 
 * ThreeValues. 
 */
public class PredicateSatMap {
	
	private HashMap<AtomicPredicate,ThreeValue> map;
	public static PredicateSatMap EMPTY = new PredicateSatMap();
	
	public PredicateSatMap() {
		map = new HashMap<AtomicPredicate, ThreeValue>();
	}
	public PredicateSatMap(HashMap<AtomicPredicate,ThreeValue> map) {
		this.map = map;
	}
	public PredicateSatMap(AtomicPredicate x,ThreeValue y) {
		this();
		this.addRule(x,y);
	}	
	private boolean addRule(AtomicPredicate x, ThreeValue y)
	{
		ThreeValue oldY = map.get(x);
		if (oldY==null)
			map.put(x, y);
		else if (oldY != y)
				return false;
		return true;
	}	
	//TODO do away with
	public HashMap<AtomicPredicate,ThreeValue> getMap()
	{
		return map;
	}
	/**
	 * 
	 * @param A
	 * @param B
	 * @return The union of the rules in A and B if they are non conflicting, 
	 * null otherwise
	 */
	@SuppressWarnings("unchecked")
	public static PredicateSatMap union(PredicateSatMap A, PredicateSatMap B)
	{
		PredicateSatMap union = new PredicateSatMap((HashMap<AtomicPredicate, ThreeValue>) A.map.clone());
		for(Entry<AtomicPredicate, ThreeValue> e: B.map.entrySet())
		{
			if (!union.addRule(e.getKey(),e.getValue()))
				return null;
		}
		return union;
	}
	public String toString()
	{
		return this.map.toString();
	}
	public ThreeValue get(AtomicPredicate atomicPredicate) {
		return map.get(atomicPredicate);
	}
	@Override	
	public int hashCode()
	{
		return map.hashCode();
	}
	@Override 
	public boolean equals(Object other)
	{
		return other instanceof PredicateSatMap && ((PredicateSatMap)other).map.equals(this.map);		
	}	
}
