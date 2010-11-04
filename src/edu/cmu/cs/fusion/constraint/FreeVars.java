package edu.cmu.cs.fusion.constraint;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.crystal.util.TypeHierarchy;


/**
 * Represents the types of spec variables. Currently does not consider subtyping,
 * but should support this in the future.
 * @author ciera
 *
 */
public class FreeVars implements Iterable<SpecVar>, Cloneable {
	public static final String OBJECT_TYPE = "java.lang.Object";
	public static final String BOOL_TYPE = "boolean";
	public static final String VOID_TYPE = "void";
	
	Map<SpecVar, String> vars;
	private static TypeHierarchy hierarchy;

	public static void setHierarchy(TypeHierarchy types) {
		hierarchy = types;
	}
	
	public FreeVars() {
		vars = new HashMap<SpecVar, String>();
	}

	public FreeVars(SpecVar[] params, String[] paramTypes) {
		assert(params.length == paramTypes.length);
		vars = new HashMap<SpecVar, String>();
		for (int ndx = 0; ndx < params.length; ndx++)
			vars.put(params[ndx], paramTypes[ndx]);
	}
	
	public ConsList<Pair<SpecVar, String>> convertToConsList() {
		ConsList<Pair<SpecVar, String>> list = ConsList.empty();
		
		for (Map.Entry<SpecVar, String> entry : vars.entrySet())
			list = ConsList.cons(new Pair<SpecVar, String>(entry.getKey(), entry.getValue()), list);
		
		return list;
	}

	public FreeVars union(FreeVars other) {
		FreeVars newVars = (FreeVars) clone();
		
		for (Entry<SpecVar, String> entry : other.vars.entrySet()) 
			newVars.vars.put(entry.getKey(), getRightType(entry.getKey(), entry.getValue()));
		
		return newVars;
	}

	public FreeVars subtract(FreeVars other) {
		FreeVars newVars = (FreeVars) clone();
		for (SpecVar var : other.vars.keySet())
			newVars.vars.remove(var);
		return newVars;
	}

	public FreeVars addVars(SpecVar[] vars, String[] types) {
		assert vars.length == types.length;
		FreeVars newVars = (FreeVars)clone();
		for (int ndx = 0; ndx < vars.length; ndx++)
			newVars.vars.put(vars[ndx], getRightType(vars[ndx], types[ndx]));
		return newVars;
	}

	public FreeVars addVar(SpecVar var, String type) {
		FreeVars newVars = (FreeVars) clone();
		
		newVars.vars.put(var, getRightType(var, type));

		return newVars;
	}
	
	private String getRightType(SpecVar var, String newType) {
		String oldType = vars.get(var);
		if (oldType == null || hierarchy.isSubtypeCompatible(oldType, newType)) 
			return newType;
		else if (hierarchy.isSubtypeCompatible(newType, oldType))
			return oldType;
		else
			return FreeVars.OBJECT_TYPE;
	}
	
	public String getType(SpecVar var) {
		return vars.get(var);
	}

	public Iterator<SpecVar> iterator() {
		return vars.keySet().iterator();
	}

	public Object clone() {
		FreeVars newVars = new FreeVars();
		newVars.vars.putAll(vars);
		return newVars;
	}

	public boolean isEmpty() {
		return vars.isEmpty();
	}
	
	public int size() {
		return vars.size();
	}
	
	public String toString() {
		return vars.toString();
	}
}
