package edu.cmu.cs.fusion;

import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.fusion.constraint.SpecVar;

public class Binding {
	private SpecVar spec;
	private Variable source;
	
	public Binding(SpecVar spec, Variable source) {
		super();
		this.spec = spec;
		this.source = source;
	}

	public SpecVar getSpec() {
		return spec;
	}
	
	public Variable getSource() {
		return source;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((spec == null) ? 0 : spec.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Binding other = (Binding) obj;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (spec == null) {
			if (other.spec != null)
				return false;
		} else if (!spec.equals(other.spec))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return spec.toString() + "->" + source.toString();
	}
}
