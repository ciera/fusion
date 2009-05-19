package edu.cmu.cs.fusion;

import java.util.HashSet;
import java.util.Set;

import edu.cmu.cs.crystal.analysis.alias.AliasLE;
import edu.cmu.cs.crystal.analysis.alias.MayAliasAnalysis;
import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.tac.MethodCallInstruction;
import edu.cmu.cs.crystal.tac.TACFlowAnalysis;
import edu.cmu.cs.crystal.tac.TACInstruction;
import edu.cmu.cs.crystal.tac.Variable;

public class MayAliasWrapper implements AliasContext {
	
	private TupleLatticeElement<Variable, AliasLE> lattice;

	public MayAliasWrapper(TACInstruction instr, TACFlowAnalysis<TupleLatticeElement<Variable, AliasLE>> flowAnalysis) {
		lattice = flowAnalysis.getResultsAfter(instr);
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

}
