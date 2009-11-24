package edu.cmu.cs.fusion;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.cmu.cs.fusion.relationship.FusionErrorReport;

public class FusionErrorStorage {
	Map<ASTNode, List<FusionErrorReport>> errors;
	
	public FusionErrorStorage() {
		errors = new HashMap<ASTNode, List<FusionErrorReport>>();
	}
	
	public void addError(FusionErrorReport error) {
		List<FusionErrorReport> errList = errors.get(error.getNode());
		
		if (errList == null) {
			errList = new LinkedList<FusionErrorReport>();
			errors.put(error.getNode(), errList);
			errList.add(error);
		}
		else {
			for (FusionErrorReport existing : errList) {
				if (existing.getConstraint().equals(error.getConstraint())) {
					//change the existing variant to be the 
					//combination of it and the new error
					existing.setVariant(existing.getVariant().merge(error.getVariant()));
					//return now that we've found something
					return;
				}
			}
			//no duplicate was found, or one was found and this one
			//was more precise
			errList.add(error);
		}
	}
	
	public List<FusionErrorReport> getError(ASTNode node) {
		List<FusionErrorReport> errList = errors.get(node);
		if (errList == null)
			return new LinkedList<FusionErrorReport>();
		else
			return errList;
	}
}
