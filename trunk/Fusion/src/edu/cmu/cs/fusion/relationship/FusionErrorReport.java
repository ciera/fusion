package edu.cmu.cs.fusion.relationship;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.fusion.Variant;
import edu.cmu.cs.fusion.constraint.Constraint;

public class FusionErrorReport {
	private Variant variant;
	private Constraint cons;
	private ASTNode node;
	
	public FusionErrorReport(Variant variant, Constraint cons,
			TACInstruction instr) {
		super();
		this.variant = variant;
		this.cons = cons;
		node = instr.getNode();
	}

	public ASTNode getNode() {
		return node;
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
		result = prime * result + ((node == null) ? 0 : node.hashCode());
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
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		if (variant == null) {
			if (other.variant != null)
				return false;
		} else if (!variant.equals(other.variant))
			return false;
		return true;
	}	
}
