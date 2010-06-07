package edu.cmu.cs.fusion;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.cmu.cs.crystal.analysis.constant.BooleanConstantLE;
import edu.cmu.cs.crystal.analysis.constant.BooleanConstantLatticeOps;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.tac.TACFlowAnalysis;
import edu.cmu.cs.crystal.tac.model.AssignmentInstruction;
import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.fusion.alias.AliasContext;
import edu.cmu.cs.fusion.alias.ObjectLabel;

public class BooleanConstantWrapper implements BooleanContext {

	private TupleLatticeElement<Variable, BooleanConstantLE> boolLattice;
	private AliasContext aliasLattice;
	private Map<ObjectLabel, ThreeValue> cache;
	
	public BooleanConstantWrapper(TACInstruction instr,
			TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>> constants, AliasContext aliases) {
		boolLattice = constants.getResultsBefore(instr);
		aliasLattice = aliases;
		cache = new HashMap<ObjectLabel, ThreeValue>();		
	}

	public BooleanConstantWrapper(ASTNode node,
			TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>> constants, AliasContext aliases) {
		boolLattice = constants.getResultsBefore(node);
		aliasLattice = aliases;
		cache = new HashMap<ObjectLabel, ThreeValue>();		
	}

	
	public BooleanConstantWrapper(AssignmentInstruction instr,
			TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>> constants, AliasContext aliasesBefore, AliasContext aliasesAfter, boolean resultValue) {
		this(instr, constants, aliasesBefore);
		
		//Need to fill in the cache with the returned value as well as what we normally have (which uses before results
		Set<ObjectLabel> labels = aliasesAfter.getAliases(instr.getTarget());
		for (ObjectLabel returnLabel : labels) {
			cache.put(returnLabel, resultValue ? ThreeValue.TRUE : ThreeValue.FALSE);
		}
	}


	@Deprecated
	public BooleanConstantWrapper(TACInstruction instr,
			TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>> constants, TACFlowAnalysis<? extends AliasContext> aliasAnalysis) {
		boolLattice = constants.getResultsBefore(instr);
		aliasLattice = aliasAnalysis.getResultsBefore(instr);
		cache = new HashMap<ObjectLabel, ThreeValue>();		
	}

	@Deprecated
	public BooleanConstantWrapper(ASTNode node,
			TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>> constants, TACFlowAnalysis<? extends AliasContext> aliasAnalysis) {
		boolLattice = constants.getResultsBefore(node);
		aliasLattice = aliasAnalysis.getResultsBefore(node);
		cache = new HashMap<ObjectLabel, ThreeValue>();		
	}

	@Deprecated
	public BooleanConstantWrapper(AssignmentInstruction instr,
			TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>> constants, TACFlowAnalysis<? extends AliasContext> aliasAnalysis, boolean resultValue) {
		this(instr, constants, aliasAnalysis);
		
		//Need to fill in the cache with the returned value as well as what we normally have (which uses before results
		Set<ObjectLabel> labels = aliasAnalysis.getResultsAfter(instr).getAliases(instr.getTarget());
		for (ObjectLabel returnLabel : labels) {
			cache.put(returnLabel, resultValue ? ThreeValue.TRUE : ThreeValue.FALSE);
		}
	}

	public ThreeValue getBooleanValue(ObjectLabel label) {
		ThreeValue value = cache.get(label);
		
		if (value == null) {
			BooleanConstantLE labelConstant = BooleanConstantLE.BOTTOM;
			BooleanConstantLatticeOps boolOps = new BooleanConstantLatticeOps();
			
			for (Variable var : aliasLattice.getVariables()) {
				if (aliasLattice.getAliases(var).contains(label)) {
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
