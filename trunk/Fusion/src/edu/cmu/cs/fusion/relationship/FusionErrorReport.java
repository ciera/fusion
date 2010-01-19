package edu.cmu.cs.fusion.relationship;

import java.util.List;

import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.Variant;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.Substitution;

public class FusionErrorReport {
	private Variant variant;
	private Constraint cons;
	private List<Substitution> failingVars;
	private FusionEnvironment failingEnvironment;
	
	public FusionErrorReport(Variant variant, Constraint cons) {
		this.variant = variant;
		this.cons = cons;
	}

	public FusionErrorReport(Variant variant, Constraint cons,
			List<Substitution> failingSubs, FusionEnvironment env) {
		this.variant = variant;
		this.cons = cons;
		this.failingVars = failingSubs;
		this.failingEnvironment = env;
	}
	

	public List<Substitution> getFailingVars() {
		return failingVars;
	}

	public FusionEnvironment getFailingEnvironment() {
		return failingEnvironment;
	}

	public Variant getVariant() {
		return variant;
	}
	
	public void setVariant(Variant var) {
		variant = var;
	}

	public Constraint getConstraint() {
		return cons;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cons == null) ? 0 : cons.hashCode());
		result = prime * result + ((variant == null) ? 0 : variant.hashCode());
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
		FusionErrorReport other = (FusionErrorReport) obj;
		if (cons == null) {
			if (other.cons != null)
				return false;
		} else if (!cons.equals(other.cons))
			return false;
		if (variant == null) {
			if (other.variant != null)
				return false;
		} else if (!variant.equals(other.variant))
			return false;
		return true;
	}	
}
