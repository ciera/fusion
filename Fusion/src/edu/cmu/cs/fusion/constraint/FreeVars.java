package edu.cmu.cs.fusion.constraint;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents the types of spec variables. Currently does not consider subtyping,
 * but should support this in the future.
 * @author ciera
 *
 */
public class FreeVars implements Iterable<SpecVar>, Cloneable {
	public static final String OBJECT_TYPE = "java.lang.Object";
	public static final String BOOL_TYPE = "boolean";
	
	Map<SpecVar, String> vars;

	public FreeVars() {
		vars = new HashMap<SpecVar, String>();
	}

	public FreeVars(SpecVar[] params, String[] paramTypes) {
		assert(params.length == paramTypes.length);
		vars = new HashMap<SpecVar, String>();
		for (int ndx = 0; ndx < params.length; ndx++)
			vars.put(params[ndx], paramTypes[ndx]);
	}

	public FreeVars union(FreeVars other) {
		FreeVars newVars = (FreeVars) clone();
		newVars.vars.putAll(other.vars);
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
			newVars.vars.put(vars[ndx], types[ndx]);
		return newVars;
	}

	public FreeVars addVar(SpecVar var, String type) {
		FreeVars newVars = (FreeVars) clone();
		newVars.vars.put(var, type);
		return newVars;
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
}