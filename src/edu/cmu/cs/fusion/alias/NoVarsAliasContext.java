package edu.cmu.cs.fusion.alias;

import java.util.HashSet;
import java.util.Set;

import edu.cmu.cs.crystal.tac.model.Variable;

public class NoVarsAliasContext implements AliasContext {
	Set<ObjectLabel> labels;
	
	public NoVarsAliasContext(Set<ObjectLabel> labels) {
		this.labels = new HashSet<ObjectLabel>(labels);
	}
	
	public NoVarsAliasContext clone() {
		return new NoVarsAliasContext(labels);
	}

	
	@Override
	public void cleanPotentialLabels() {
	}

	@Override
	public Set<ObjectLabel> getAliases(Variable var) {
		return new HashSet<ObjectLabel>();
	}

	@Override
	public Set<ObjectLabel> getAllAliases() {
		return labels;
	}

	@Override
	public Set<Variable> getVariables() {
		return new HashSet<Variable>();
	}

	@Override
	public void reset(Variable var, Set<ObjectLabel> labels) {
	}

}
