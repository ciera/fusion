package edu.cmu.cs.fusion.test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.cmu.cs.crystal.analysis.alias.MayAliasAnalysis;
import edu.cmu.cs.crystal.analysis.constant.ConstantAnalysis;
import edu.cmu.cs.crystal.tac.TACInstruction;
import edu.cmu.cs.fusion.FusionAnalysis;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.relationship.RelationshipTransferFunction.Variant;

public class StubFusionAnalysis extends FusionAnalysis {
	private Map<Constraint, List<TACInstruction>> soundErrors;
	private Map<Constraint, List<TACInstruction>> completeErrors;
	private Map<Constraint, List<TACInstruction>> pragmaticErrors;

	public StubFusionAnalysis() {
		super(null);
		soundErrors = new HashMap<Constraint, List<TACInstruction>>();
		completeErrors = new HashMap<Constraint, List<TACInstruction>>();
		pragmaticErrors = new HashMap<Constraint, List<TACInstruction>>();
	}

	@Override
	public void reportError(Variant variant, Constraint cons,
			TACInstruction instr) {
		switch (variant) {
		case COMPLETE:
			addError(completeErrors, cons, instr);
			break;
		case SOUND:
			addError(soundErrors, cons, instr);
			break;
		case PRAGMATIC:
			addError(pragmaticErrors, cons, instr);
			break;
		}
	}

	private void addError(
			Map<Constraint, List<TACInstruction>> errorList,
			Constraint cons, TACInstruction instr) {
		List<TACInstruction> list = errorList.get(cons);
		
		if (list == null) {
			list = new LinkedList<TACInstruction>();
			errorList.put(cons, list);
		}
		list.add(instr);
	}
	
	public List<TACInstruction> getError(Variant variant, Constraint cons) {
		List<TACInstruction> errors = null; 
		switch (variant) {
		case COMPLETE:
			errors = completeErrors.get(cons);
			break;
		case SOUND:
			errors = soundErrors.get(cons);
			break;
		case PRAGMATIC:
			errors =  pragmaticErrors.get(cons);
			break;
		}
		if (errors == null)
			errors = new LinkedList<TACInstruction>();
		return errors;
	}
}
