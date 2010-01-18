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
import edu.cmu.cs.crystal.tac.eclipse.EclipseTAC;
import edu.cmu.cs.crystal.tac.model.TACInstruction;
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
		BooleanContext bools = new BooleanConstantWrapper(node.getBody(), fa.getBooleanAnalysis(), fa.getAliasAnalysis());
		AliasContext aliases = new MayAliasWrapper(node.getBody(), fa.getAliasAnalysis());
		RelationshipContext rels = fa.getResultsAfter(node.getBody());
		FusionEnvironment env = new FusionEnvironment(aliases, rels , bools, fa.getHierarchy(), fa.getInfers());
		
		List<FusionErrorReport> errors = checker.checkForErrors(env, instr);
		
		for (FusionErrorReport err : errors) {
			reporter.reportUserProblem("Broken constraint:" + err.getConstraint(), node, err.getVariant().toString());	
			log.log(Level.INFO, "Broken constraint:" + err.getConstraint());
			log.log(Level.INFO, "Variant:" + err.getVariant().toString());			
			log.log(Level.INFO, "Failing alias env " + err.getFailingEnvironment().printAllAliases());
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
		BooleanContext bools = new BooleanConstantWrapper(node, fa.getBooleanAnalysis(), fa.getAliasAnalysis());
		AliasContext aliases = new MayAliasWrapper(node, fa.getAliasAnalysis());
		RelationshipContext rels = fa.getResultsBefore(node);
		FusionEnvironment env = new FusionEnvironment(aliases, rels , bools, fa.getHierarchy(), fa.getInfers());
		
		List<FusionErrorReport> errors = checker.checkForErrors(env, instr);
		
		for (FusionErrorReport err : errors) {
			reporter.reportUserProblem("Broken constraint:" + err.getConstraint(), node, err.getVariant().toString());	
			log.log(Level.INFO, "Broken constraint:" + err.getConstraint());
			log.log(Level.INFO, "Variant:" + err.getVariant().toString());			
			log.log(Level.INFO, "Failing alias env " + err.getFailingEnvironment().printAllAliases());
		}
	}

}
