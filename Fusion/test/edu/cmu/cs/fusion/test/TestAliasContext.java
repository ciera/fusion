package edu.cmu.cs.fusion.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.fusion.AliasContext;

public class TestAliasContext implements AliasContext {
	public Set<ObjectLabel> objectLabels;
	public Map<Variable, Set<ObjectLabel>> aliases;
	
	public TestAliasContext() {
		objectLabels = new HashSet<ObjectLabel>();
		aliases = new HashMap<Variable, Set<ObjectLabel>>();
	}
	
	public void addAlias(Variable var, ObjectLabel label) {
		Set<ObjectLabel> varAl = aliases.get(var);
		
		if (varAl == null) {
			varAl = new HashSet<ObjectLabel>();
			aliases.put(var, varAl);
		}
		varAl.add(label);
		objectLabels.add(label);
	}
	
	public Set<ObjectLabel> getAliases(Variable var) {
		return aliases.get(var);
	}

	public Set<ObjectLabel> getAllAliases() {
		return objectLabels;
	}

}
