package edu.cmu.cs.fusion;

import org.eclipse.jdt.core.dom.IMethodBinding;

import edu.cmu.cs.crystal.tac.model.Variable;

public class Method {
	private Variable[] params;
	private Variable thisVar;
	private IMethodBinding binding;
	
	public Method(Variable[] params, Variable thisVar, IMethodBinding binding) {
		this.params = params;
		this.thisVar = thisVar;
		this.binding = binding;
	}
	
	public Variable[] getParams() {
		return params;
	}
	public Variable getThisVar() {
		return thisVar;
	}
	public IMethodBinding getBinding() {
		return binding;
	}
	
}
