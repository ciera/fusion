package edu.cmu.cs.fusion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.cmu.cs.crystal.analysis.alias.AliasLE;
import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.analysis.constant.BooleanConstantLE;
import edu.cmu.cs.crystal.analysis.constant.BooleanConstantLatticeOps;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.tac.TACFlowAnalysis;
import edu.cmu.cs.crystal.tac.model.AssignmentInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;

public class BooleanConstantWrapper implements BooleanContext {

	private TupleLatticeElement<Variable, BooleanConstantLE> boolLattice;
	private TupleLatticeElement<Variable, AliasLE> aliasLattice;
	private Map<ObjectLabel, ThreeValue> cache;
	
	public BooleanConstantWrapper(AssignmentInstruction instr,
			TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>> constants, TACFlowAnalysis<TupleLatticeElement<Variable, AliasLE>> aliasAnalysis) {
		boolLattice = constants.getResultsBefore(instr);
		aliasLattice = aliasAnalysis.getResultsBefore(instr);
		cache = new HashMap<ObjectLabel, ThreeValue>();		
	}

	public BooleanConstantWrapper(AssignmentInstruction instr,
			TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>> constants, TACFlowAnalysis<TupleLatticeElement<Variable, AliasLE>> aliasAnalysis, boolean resultValue) {
		this(instr, constants, aliasAnalysis);
		
		assert getAfterAliasLabels(instr.getTarget(), aliasAnalysis.getResultsAfter(instr)).size() == 1;
		//If there's more than one...something wacky is going on....should still work though....
		for (ObjectLabel returnLabel : getAfterAliasLabels(instr.getTarget(), aliasAnalysis.getResultsAfter(instr))) {
			cache.put(returnLabel, resultValue ? ThreeValue.TRUE : ThreeValue.FALSE);
		}
	}
	
	private Set<ObjectLabel> getAfterAliasLabels(Variable searchVariable, TupleLatticeElement<Variable, AliasLE> le) {
		//TODO: Find a cheaper way of maintaining this context and have the ability
		//to query it at any node. Maybe a Contextual Lattice?
		Set<ObjectLabel> labels = new HashSet<ObjectLabel>();
		for (Variable var : le.getKeySet()) {
			Set<ObjectLabel> aliases = le.get(var).getLabels();
			
			for (ObjectLabel label : aliases) {
				if (searchVariable.resolveType().isCastCompatible(label.getType()))
					labels.add(label);
			}
		}
		return labels;
	}

	public ThreeValue getBooleanValue(ObjectLabel label) {
		ThreeValue value = cache.get(label);
		
		if (value == null) {
			BooleanConstantLE labelConstant = BooleanConstantLE.BOTTOM;
			BooleanConstantLatticeOps boolOps = new BooleanConstantLatticeOps();
			
			for (Variable var : aliasLattice.getKeySet()) {
				if (aliasLattice.get(var).getLabels().contains(label)) {
					BooleanConstantLE varConstant = boolLattice.get(var);
					labelConstant = boolOps.join(labelConstant, varConstant);
				}
			}
			if (labelConstant == BooleanConstantLE.TRUE)
				value = ThreeValue.TRUE;
			else if (labelConstant == BooleanConstantLE.FALSE)
				value = ThreeValue.FALSE;
			else
				value = ThreeValue.UNKNOWN;
			cache.put(label, value);
		}
		
		return value;
	}
}
