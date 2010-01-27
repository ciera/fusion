package edu.cmu.cs.fusion;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.cmu.cs.crystal.analysis.alias.AliasLE;
import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.tac.TACFlowAnalysis;
import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;

public class MayAliasWrapper implements AliasContext {
	
	private TupleLatticeElement<Variable, AliasLE> lattice;

	public MayAliasWrapper(TACInstruction instr, TACFlowAnalysis<TupleLatticeElement<Variable, AliasLE>> flowAnalysis) {
		lattice = flowAnalysis.getResultsAfter(instr);
	}

	public MayAliasWrapper(ASTNode node, TACFlowAnalysis<TupleLatticeElement<Variable, AliasLE>> flowAnalysis) {
		lattice = flowAnalysis.getResultsAfter(node);
	}
	
	public MayAliasWrapper(TupleLatticeElement<Variable, AliasLE> lattice) {
		this.lattice = lattice;
	}
	
	public Set<ObjectLabel> getAliases(Variable var) {
		return lattice.get(var).getLabels();
	}

	public Set<ObjectLabel> getAllAliases() {
		Set<ObjectLabel> allLabels = new HashSet<ObjectLabel>();
		
		for (Variable var : lattice.getKeySet()) {
			AliasLE aliases = lattice.get(var);
			allLabels.addAll(aliases.getLabels());
		}
		
		return allLabels;
	}
	
	public String toString() {
		String str = "Points-to: " + lattice.toString() + "\nTypes: {";
		for (ObjectLabel label : getAllAliases()) {
			str += label + ":" + label.getType().getQualifiedName() + ", ";
		}
		str += "}";
		return str;
	}

}
