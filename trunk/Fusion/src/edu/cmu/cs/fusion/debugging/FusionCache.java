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
	
	private SortedMap<Integer, Pair<AliasContext, RelationshipContext>> cache = new TreeMap<Integer, Pair<AliasContext, RelationshipContext>>();
	
	
	public Pair<AliasContext, RelationshipContext> getResults(ICompilationUnit unit, int lineNum) {
		assert(unit.equals(compUnit));
		return cache.get(lineNum);
	}
	
	public void setCompUnit(ICompilationUnit compUnit) {
		this.compUnit = compUnit;
		cache.clear();
	}

	public void addResult(int startLine, int endLine, Pair<AliasContext, RelationshipContext> pair) {
		cache.put(startLine, pair);
	}
}
