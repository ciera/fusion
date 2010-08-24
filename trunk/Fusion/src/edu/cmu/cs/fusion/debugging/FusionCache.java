package edu.cmu.cs.fusion.debugging;

import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.jdt.core.ICompilationUnit;

import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.fusion.alias.AliasContext;
import edu.cmu.cs.fusion.relationship.RelationshipContext;

public class FusionCache {
	static private FusionCache THE_CACHE = new FusionCache();
	static public FusionCache getCache() {return THE_CACHE;}
	
	private ICompilationUnit compUnit;
	
	private SortedMap<Integer, Pair<? extends AliasContext, RelationshipContext>> cache = new TreeMap<Integer, Pair<? extends AliasContext, RelationshipContext>>();
	
	
	public Pair<? extends AliasContext, RelationshipContext> getResults(ICompilationUnit unit, int lineNum) {
		assert(unit.equals(compUnit));
		return cache.get(lineNum);
	}
	
	public void setCompUnit(ICompilationUnit compUnit) {
		this.compUnit = compUnit;
		cache.clear();
	}

	public void addResult(int startLine, int endLine, Pair<? extends AliasContext, RelationshipContext> pair) {
		cache.put(startLine, pair);
	}
}
