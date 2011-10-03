package edu.cmu.cs.fusion.test.constraint.predicates;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Test;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.constraint.Predicate;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.constraint.predicates.AtomicPredicate;
import edu.cmu.cs.fusion.constraint.predicates.PredicateSatMap;


public class TestSAT {
	
	@Test
	public void testMapMinimality(Predicate p, FusionEnvironment env, Substitution sub)
	{
		assert(env.getOverrideMap()!=null);
		ThreeValue target = p.getTruth(env, sub);
		HashMap<AtomicPredicate, ThreeValue> map = env.getOverrideMap().getMap(), 
		map2= new HashMap<AtomicPredicate,ThreeValue>();
		assert(map.size()<64);
		long N= (1l<<map.size())-1;
		for(long b=0;b<N;b++)
		{
			long i=0;
			for(Entry<AtomicPredicate, ThreeValue> entry: map.entrySet())
			{
				if (((b>>(long)i)&1l)==1l)//cast necessary?
				{
					map2.put(entry.getKey(),entry.getValue());
				}
			}
			
			PredicateSatMap satmap2 = new PredicateSatMap(map2);
			env.setOverrideMap(satmap2);
			ThreeValue truth = p.getTruth(env, sub);
			env.setOverrideMap(null);
			map2.clear();
			
			assertEquals(true, b==N-1||target!=truth);
		}
	}
}
