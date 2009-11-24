package edu.cmu.cs.fusion.test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.cmu.cs.fusion.FusionErrorStorage;
import edu.cmu.cs.fusion.Variant;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.relationship.FusionErrorReport;

public class StubFusionErrorStorage extends FusionErrorStorage {
	private Map<Constraint, List<ASTNode>> soundErrors;
	private Map<Constraint, List<ASTNode>> completeErrors;
	private Map<Constraint, List<ASTNode>> pragmaticErrors;

	public StubFusionErrorStorage() {
		super();
		soundErrors = new HashMap<Constraint, List<ASTNode>>();
		completeErrors = new HashMap<Constraint, List<ASTNode>>();
		pragmaticErrors = new HashMap<Constraint, List<ASTNode>>();
	}
	
	@Override
	public void addError(FusionErrorReport err) {
		super.addError(err);
		if (err.getVariant().isComplete())
			addError(completeErrors, err.getConstraint(), err.getNode());
		if (err.getVariant().isSound())
			addError(soundErrors, err.getConstraint(), err.getNode());
		if (err.getVariant().isPragmatic())
			addError(pragmaticErrors, err.getConstraint(), err.getNode());
	}

	private void addError(
			Map<Constraint, List<ASTNode>> errorList,
			Constraint cons, ASTNode node) {
		List<ASTNode> list = errorList.get(cons);
		
		if (list == null) {
			list = new LinkedList<ASTNode>();
			errorList.put(cons, list);
		}
		list.add(node);
	}

	public List<ASTNode> getError(Variant variant, Constraint cons) {
		List<ASTNode> errors = null; 
		if (variant.isComplete())
			errors = completeErrors.get(cons);
		else if (variant.isSound())
			errors = soundErrors.get(cons);
		else
			errors =  pragmaticErrors.get(cons);

		if (errors == null)
			errors = new LinkedList<ASTNode>();
		return errors;
	}
}
