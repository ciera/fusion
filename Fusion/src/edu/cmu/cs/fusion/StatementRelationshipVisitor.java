package edu.cmu.cs.fusion;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import edu.cmu.cs.crystal.tac.TACFlowAnalysis;
import edu.cmu.cs.fusion.relationship.RelationshipContext;

public class StatementRelationshipVisitor extends ASTVisitor {

	private TACFlowAnalysis<RelationshipContext> fusionAnalysis;
	private Logger logger;
	private Level level;

	public StatementRelationshipVisitor(TACFlowAnalysis<RelationshipContext> fa, Logger log, Level level) {
		fusionAnalysis = fa;
		logger = log;
		this.level = level;
	}

	@Override
	public boolean visit(AssertStatement node) {
		logger.log(level, node.toString().trim());
		logger.log(level, node.toString().trim());
		logger.log(level, fusionAnalysis.getResultsAfter(node).toString());
		return false;
	}

	@Override
	public boolean visit(Assignment node) {
		logger.log(level, node.toString().trim());
		logger.log(level, fusionAnalysis.getResultsAfter(node).toString());
		return false;
	}

	@Override
	public boolean visit(BreakStatement node) {
		logger.log(level, node.toString().trim());
		logger.log(level, fusionAnalysis.getResultsAfter(node).toString());
		return false;
	}

	@Override
	public boolean visit(ContinueStatement node) {
		logger.log(level, node.toString().trim());
		logger.log(level, fusionAnalysis.getResultsAfter(node).toString());
		return false;
	}

	@Override
	public boolean visit(DoStatement node) {
		logger.log(level, "do {");
		logger.log(level, fusionAnalysis.getResultsBefore(node.getBody()).toString());		
		node.getBody().accept(this);
		logger.log(level, "} while (" + node.getExpression() + ")");
		logger.log(level, fusionAnalysis.getResultsAfter(node).toString());
		return false;
	}

	@Override
	public boolean visit(EmptyStatement node) {
		logger.log(level, node.toString().trim());
		logger.log(level, fusionAnalysis.getResultsAfter(node).toString());
		return false;
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		logger.log(level, "for(" + node.getParameter() + " : " + node.getExpression() + ") {");
		logger.log(level, fusionAnalysis.getResultsBefore(node.getBody()).toString());		
		node.getBody().accept(this);
		logger.log(level, "}");
		logger.log(level, fusionAnalysis.getResultsAfter(node).toString());
		return false;
	}

	@Override
	public boolean visit(ExpressionStatement node) {
		logger.log(level, node.toString().trim());
		logger.log(level, fusionAnalysis.getResultsAfter(node).toString());
		return false;
	}

	@Override
	public boolean visit(ForStatement node) {
		logger.log(level, "for(" + node.initializers() + "; " + node.getExpression() + "; " + node.updaters() + ") {");
		logger.log(level, fusionAnalysis.getResultsBefore(node.getBody()).toString());		
		node.getBody().accept(this);
		logger.log(level, "}");
		logger.log(level, fusionAnalysis.getResultsAfter(node).toString());
		return false;
	}

	@Override
	public boolean visit(IfStatement node) {
		logger.log(level, "if (" + node.getExpression() + ") {");
		logger.log(level, fusionAnalysis.getResultsBefore(node.getThenStatement()).toString());		
		node.getThenStatement().accept(this);
		logger.log(level, "}");
		if (node.getElseStatement() != null) {
			logger.log(level, "else {");
			logger.log(level, fusionAnalysis.getResultsBefore(node.getElseStatement()).toString());		
			node.getElseStatement().accept(this);
			logger.log(level, "}");
		}
		logger.log(level, fusionAnalysis.getResultsAfter(node).toString());
		return false;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		logger.log(level, node.getName() + "{");
		if (node.getBody() == null || node.getBody().statements().isEmpty())
			logger.log(level, fusionAnalysis.getResultsBefore(node).toString());	
		else
			logger.log(level, fusionAnalysis.getResultsBefore((Statement)node.getBody().statements().get(0)).toString());
		
		if (node.getBody() != null) {
			node.getBody().accept(this);
		}
		logger.log(level, "}");
		logger.log(level, "//" + node.getName());
		return false;
	}

	@Override
	public boolean visit(ReturnStatement node) {
		logger.log(level, node.toString().trim());
		logger.log(level, fusionAnalysis.getResultsAfter(node).toString());
		return false;
	}

	@Override
	public boolean visit(SwitchStatement node) {
		logger.log(level, "switch (" + node.getExpression() + ") {");
		
		if (!node.statements().isEmpty())
			logger.log(level, fusionAnalysis.getResultsBefore((Statement)node.statements().get(0)).toString());		

		for (Statement statement : (List<Statement>)node.statements()) {
			statement.accept(this);
		}
		
		logger.log(level, "}");
		logger.log(level, fusionAnalysis.getResultsAfter(node).toString());
		return false;
	}

	@Override
	public boolean visit(ThrowStatement node) {
		logger.log(level, node.toString().trim());
		logger.log(level, fusionAnalysis.getResultsAfter(node).toString());
		return false;
	}

	@Override
	public boolean visit(TryStatement node) {
		logger.log(level, "try {");
		logger.log(level, fusionAnalysis.getResultsBefore(node.getBody()).toString());		
		node.getBody().accept(this);
		logger.log(level, "}");
		for (CatchClause catcher : (List<CatchClause>)node.catchClauses()) {
			logger.log(level, "catch (" + catcher.getException() + ") {");
			logger.log(level, fusionAnalysis.getResultsBefore(catcher.getBody()).toString());		
			catcher.getBody().accept(this);
			logger.log(level, "}");
		}
		
		if (node.getFinally() != null) {
			logger.log(level, "finally {");
			logger.log(level, fusionAnalysis.getResultsBefore(node.getFinally()).toString());		
			node.getFinally().accept(this);
		}

		logger.log(level, fusionAnalysis.getResultsAfter(node).toString());
		return false;
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		logger.log(level, node.toString().trim());
		logger.log(level, fusionAnalysis.getResultsAfter(node).toString());
		return false;
	}

	@Override
	public boolean visit(WhileStatement node) {
		logger.log(level, "while (" + node.getExpression() + ") {");
		logger.log(level, fusionAnalysis.getResultsBefore(node.getBody()).toString());		
		node.getBody().accept(this);
		logger.log(level, "}");
		logger.log(level, fusionAnalysis.getResultsAfter(node).toString());
		return false;
	}

}
