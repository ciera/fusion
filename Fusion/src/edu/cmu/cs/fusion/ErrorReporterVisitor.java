package edu.cmu.cs.fusion;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;

import edu.cmu.cs.crystal.IAnalysisReporter;
import edu.cmu.cs.crystal.IAnalysisReporter.SEVERITY;
import edu.cmu.cs.fusion.relationship.FusionErrorReport;

public class ErrorReporterVisitor extends ASTVisitor {

	private FusionErrorStorage errors;
	private IAnalysisReporter reporter;

	public ErrorReporterVisitor(FusionErrorStorage errors, IAnalysisReporter reporter) {
		this.errors = errors;
		this.reporter = reporter;
	}

	@Override
	public void preVisit(ASTNode node) {
		List<FusionErrorReport> errList = errors.getError(node);
		
		for (FusionErrorReport err : errList)
			reporter.reportUserProblem("The constraint " + err.getConstraint().toString() + " was violated.", err.getNode(), err.getVariant().toString(), SEVERITY.WARNING);
	}

}
