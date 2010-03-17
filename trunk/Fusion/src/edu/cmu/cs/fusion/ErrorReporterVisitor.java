package edu.cmu.cs.fusion;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

import edu.cmu.cs.crystal.IAnalysisReporter;
import edu.cmu.cs.crystal.IAnalysisReporter.SEVERITY;
import edu.cmu.cs.crystal.tac.eclipse.EclipseTAC;
import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.fusion.alias.AliasContext;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.relationship.ConstraintChecker;
import edu.cmu.cs.fusion.relationship.FusionErrorReport;
import edu.cmu.cs.fusion.relationship.RelationshipContext;
import edu.cmu.cs.fusion.test.constraint.DefaultReturnInstruction;

public class ErrorReporterVisitor extends ASTVisitor {

	private IAnalysisReporter reporter;
	private ConstraintChecker checker;
	private EclipseTAC tac;
	private FusionAnalysis fa;
	private Logger log;

	public ErrorReporterVisitor(FusionAnalysis analysis, ConstraintChecker constraintChecker, IAnalysisReporter reporter, EclipseTAC tac, Logger log) {
		this.reporter = reporter;
		this.checker = constraintChecker;
		this.fa = analysis;
		this.tac = tac;
		this.log = log;
	}

	
	@Override
	public void endVisit(ClassInstanceCreation node) {
		check(node);
	}

	@Override
	public void endVisit(ConstructorInvocation node) {
		check(node);
	}
	
	@Override
	public void endVisit(MethodDeclaration node) {
		TACInstruction instr = new DefaultReturnInstruction();
		AliasContext aliases = fa.getPointsToResultsAfter(node.getBody());
		RelationshipContext rels = fa.getRelResultsAfter(node.getBody());
		BooleanContext bools = new BooleanConstantWrapper(node.getBody(), fa.getBooleanAnalysis(), aliases);
		FusionEnvironment env = new FusionEnvironment(aliases, rels , bools, fa.getHierarchy(), fa.getInfers());
		
		List<FusionErrorReport> errors = checker.checkForErrors(env, instr);
		
		for (FusionErrorReport err : errors) {
			if (!(err.getVariant().contains(fa.getVariant())))
				continue;
			SEVERITY sev = err.getVariant().isComplete() ? SEVERITY.ERROR : SEVERITY.WARNING;
			boolean hasStatements = node.getBody() != null && node.getBody().statements().size() > 0;
			ASTNode reportOn = !hasStatements ? node : (ASTNode)node.getBody().statements().get(node.getBody().statements().size() - 1);
			reporter.reportUserProblem("Broken constraint:" + err.getConstraint().toErrorString(), reportOn, fa.getName(), sev);	
			log.log(Level.INFO, "Broken constraint:" + err.getConstraint());
			log.log(Level.INFO, "Variant:" + err.getVariant().toString());			
			log.log(Level.INFO, "Failing alias env " + err.getFailingEnvironment().printAllAliases());
			for (Substitution failure : err.getFailingVars())
				log.log(Level.INFO, "Failing subtitution " + failure.toString());
		}
	}


	@Override
	public void endVisit(MethodInvocation node) {
		check(node);
	}

	@Override
	public void endVisit(ReturnStatement node) {
		check(node);
	}

	@Override
	public void endVisit(SuperConstructorInvocation node) {
		check(node);
	}

	@Override
	public void endVisit(SuperMethodInvocation node) {
		check(node);
	}

	private void check(ASTNode node) {
		TACInstruction instr = tac.instruction(node);
		AliasContext aliases = fa.getPointsToResultsAfter(node);
		RelationshipContext rels = fa.getRelResultsBefore(node);
		BooleanContext bools = new BooleanConstantWrapper(node, fa.getBooleanAnalysis(), aliases);
		FusionEnvironment env = new FusionEnvironment(aliases, rels , bools, fa.getHierarchy(), fa.getInfers());
		
		List<FusionErrorReport> errors = checker.checkForErrors(env, instr);
		
		for (FusionErrorReport err : errors) {
			if (!(err.getVariant().contains(fa.getVariant())))
				continue;
			SEVERITY sev = err.getVariant().isComplete() ? SEVERITY.ERROR : SEVERITY.WARNING;
			reporter.reportUserProblem("Broken constraint:" + err.getConstraint().toErrorString(), node, fa.getName(), sev);	
			log.log(Level.INFO, "Broken constraint:" + err.getConstraint());
			log.log(Level.INFO, "Variant:" + err.getVariant().toString());			
			log.log(Level.INFO, "Failing alias env " + err.getFailingEnvironment().printAllAliases());
			for (Substitution failure : err.getFailingVars())
				log.log(Level.INFO, "Failing subtitution " + failure.toString());
		}
	}

}
