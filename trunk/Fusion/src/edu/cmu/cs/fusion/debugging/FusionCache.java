package edu.cmu.cs.fusion.debugging;

import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.jdt.core.ICompilationUnit;

import edu.cmu.cs.fusion.relationship.RelationshipContext;

public class FusionCache {
	static private FusionCache THE_CACHE = new FusionCache();
	static public FusionCache getCache() {return THE_CACHE;}
	
	private ICompilationUnit compUnit;
	
	private SortedMap<Integer, RelationshipContext> cache = new TreeMap<Integer, RelationshipContext>();
	
	
	public RelationshipContext getResults(ICompilationUnit unit, int lineNum) {
		assert(unit.equals(compUnit));
		return cache.get(lineNum);
	}
	
	public void setCompUnit(ICompilationUnit compUnit) {
		this.compUnit = compUnit;
		cache.clear();
	}

	public void addResult(int startLine, int endLine, RelationshipContext context) {
		cache.put(startLine, context);
	}
}
