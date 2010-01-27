package edu.cmu.cs.fusion;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.cmu.cs.crystal.analysis.alias.AliasLE;
import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.analysis.constant.BooleanConstantLE;
import edu.cmu.cs.crystal.analysis.constant.BooleanConstantLatticeOps;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.tac.TACFlowAnalysis;
import edu.cmu.cs.crystal.tac.model.AssignmentInstruction;
import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;

public class BooleanConstantWrapper implements BooleanContext {

	private TupleLatticeElement<Variable, BooleanConstantLE> boolLattice;
	private TupleLatticeElement<Variable, AliasLE> aliasLattice;
	private Map<ObjectLabel, ThreeValue> cache;
	
	public BooleanConstantWrapper(TACInstruction instr,
			TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>> constants, TACFlowAnalysis<TupleLatticeElement<Variable, AliasLE>> aliasAnalysis) {
		boolLattice = constants.getResultsBefore(instr);
		aliasLattice = aliasAnalysis.getResultsBefore(instr);
		cache = new HashMap<ObjectLabel, ThreeValue>();		
	}

	public BooleanConstantWrapper(ASTNode node,
			TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>> constants, TACFlowAnalysis<TupleLatticeElement<Variable, AliasLE>> aliasAnalysis) {
		boolLattice = constants.getResultsBefore(node);
		aliasLattice = aliasAnalysis.getResultsBefore(node);
		cache = new HashMap<ObjectLabel, ThreeValue>();		
	}

	public BooleanConstantWrapper(AssignmentInstruction instr,
			TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>> constants, TACFlowAnalysis<TupleLatticeElement<Variable, AliasLE>> aliasAnalysis, boolean resultValue) {
		this(instr, constants, aliasAnalysis);
		
		//Need to fill in the cache with the returned value as well as what we normally have (which uses before results
		AliasLE retAliases = aliasAnalysis.getResultsAfter(instr).get(instr.getTarget());
		for (ObjectLabel returnLabel : retAliases.getLabels()) {
			cache.put(returnLabel, resultValue ? ThreeValue.TRUE : ThreeValue.FALSE);
		}
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
