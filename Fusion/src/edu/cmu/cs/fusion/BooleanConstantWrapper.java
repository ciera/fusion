package edu.cmu.cs.fusion;

import java.util.HashMap;
import java.util.Map;

import edu.cmu.cs.crystal.analysis.alias.AliasLE;
import edu.cmu.cs.crystal.analysis.alias.MayAliasAnalysis;
import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.analysis.constant.BooleanConstantLE;
import edu.cmu.cs.crystal.analysis.constant.ConstantAnalysis;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.tac.AssignmentInstruction;
import edu.cmu.cs.crystal.tac.Variable;
import edu.cmu.cs.fusion.BooleanContext;
import edu.cmu.cs.fusion.ThreeValue;

public class BooleanConstantWrapper implements BooleanContext {

	private TupleLatticeElement<Variable, BooleanConstantLE> boolLattice;
	private TupleLatticeElement<Variable, AliasLE> aliasLattice;
	private Map<ObjectLabel, ThreeValue> cache;
	
	public BooleanConstantWrapper(AssignmentInstruction instr,
			ConstantAnalysis booleanAnalysis, MayAliasAnalysis aliasAnalysis, boolean resultValue) {
		boolLattice = booleanAnalysis.getResultsBefore(instr);
		aliasLattice = aliasAnalysis.getResultsBefore(instr);
		cache = new HashMap<ObjectLabel, ThreeValue>();
		
		assert aliasAnalysis.getAfterAliasLabels(instr.getTarget(), instr.getNode()).size() == 1;
		//If there's more than one...something wacky is going on....should still work though....
		for (ObjectLabel returnLabel : aliasAnalysis.getAfterAliasLabels(instr.getTarget(), instr.getNode())) {
			cache.put(returnLabel, resultValue ? ThreeValue.TRUE : ThreeValue.FALSE);
		}
	}

	public ThreeValue getBooleanValue(ObjectLabel label) {
		ThreeValue value = cache.get(label);
		
		if (value == null) {
			BooleanConstantLE labelConstant = BooleanConstantLE.BOTTOM;
			
			for (Variable var : aliasLattice.getKeySet()) {
				if (aliasLattice.get(var).getLabels().contains(label)) {
					BooleanConstantLE varConstant = boolLattice.get(var);
					labelConstant = labelConstant.join(varConstant, null);
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
