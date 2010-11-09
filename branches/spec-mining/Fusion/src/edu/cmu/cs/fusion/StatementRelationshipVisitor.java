package edu.cmu.cs.fusion;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import edu.cmu.cs.crystal.flow.BooleanLabel;
import edu.cmu.cs.crystal.flow.IteratorLabel;

public class StatementRelationshipVisitor extends ASTVisitor {

	private FusionAnalysis fusionAnalysis;
	private StringBuilder buff;

	public StatementRelationshipVisitor(FusionAnalysis fa) {
		fusionAnalysis = fa;
		buff = new StringBuilder();
	}

	@Override
	public boolean visit(AssertStatement node) {
		buff.append(node.toString().trim());
		buff.append("\n");
		buff.append(fusionAnalysis.getRelResultsAfter(node).toString());
		buff.append("\n");
		return false;
	}

	@Override
	public boolean visit(Assignment node) {
		buff.append(node.toString().trim());
		buff.append("\n");
		buff.append(fusionAnalysis.getRelResultsAfter(node).toString());
		buff.append("\n");
		return false;
	}

	@Override
	public boolean visit(BreakStatement node) {
		buff.append(node.toString().trim());
		buff.append("\n");
		buff.append(fusionAnalysis.getRelResultsAfter(node).toString());
		buff.append("\n");
		return false;
	}

	@Override
	public boolean visit(ContinueStatement node) {
		buff.append(node.toString().trim());
		buff.append("\n");
		buff.append(fusionAnalysis.getRelResultsAfter(node).toString());
		buff.append("\n");
		return false;
	}

	@Override
	public boolean visit(DoStatement node) {
		buff.append("do {");
		buff.append("\n");
		buff.append(fusionAnalysis.getSpecificRelResultsAfter(node.getExpression(), BooleanLabel.getBooleanLabel(true)).toString());
		buff.append("\n");
		node.getBody().accept(this);
		buff.append("} while (" + node.getExpression() + ")");
		buff.append("\n");
		buff.append(fusionAnalysis.getSpecificRelResultsAfter(node.getExpression(), BooleanLabel.getBooleanLabel(false)).toString());
		buff.append("\n");
		return false;
	}

	@Override
	public boolean visit(EmptyStatement node) {
		buff.append(node.toString().trim());
		buff.append("\n");
		buff.append(fusionAnalysis.getRelResultsAfter(node).toString());
		buff.append("\n");
		return false;
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		buff.append(fusionAnalysis.getRelResultsBefore(node.getExpression()).toString());
		buff.append("for(" + node.getParameter() + " : " + node.getExpression() + ") {");
		buff.append("\n");
		buff.append(fusionAnalysis.getSpecificRelResultsAfter(node, IteratorLabel.getItrLabel(false)).toString());
		buff.append("\n");
		node.getBody().accept(this);
		buff.append("}");
		buff.append("\n");
		buff.append(fusionAnalysis.getSpecificRelResultsAfter(node, IteratorLabel.getItrLabel(true)).toString());
		buff.append("\n");
		return false;
	}

	@Override
	public boolean visit(ExpressionStatement node) {
		buff.append(node.toString().trim());
		buff.append("\n");
		buff.append(fusionAnalysis.getRelResultsAfter(node).toString());
		buff.append("\n");
		return false;
	}

	@Override
	public boolean visit(ForStatement node) {				
		buff.append("for(" + node.initializers() + "; " + node.getExpression() + "; " + node.updaters() + ") {");
		buff.append("\n");
		if (node.getExpression() != null)
			buff.append(fusionAnalysis.getSpecificRelResultsAfter(node.getExpression(), BooleanLabel.getBooleanLabel(true)).toString());
		buff.append("\n");
		node.getBody().accept(this);
		buff.append("}");
		buff.append("\n");
		if (node.getExpression() != null)
		buff.append(fusionAnalysis.getSpecificRelResultsAfter(node.getExpression(), BooleanLabel.getBooleanLabel(false)).toString());
		buff.append("\n");
		return false;
	}

	@Override
	public boolean visit(IfStatement node) {
		buff.append("if (" + node.getExpression() + ") {");
		buff.append("\n");
		buff.append(fusionAnalysis.getSpecificRelResultsAfter(node, BooleanLabel.getBooleanLabel(true)).toString());
		buff.append("\n");
		node.getThenStatement().accept(this);
		buff.append("}");
		buff.append("\n");
		if (node.getElseStatement() != null) {
			buff.append("else {");
			buff.append("\n");
			buff.append(fusionAnalysis.getSpecificRelResultsAfter(node, BooleanLabel.getBooleanLabel(false)).toString());
			buff.append("\n");
			node.getElseStatement().accept(this);
			buff.append("}");
			buff.append("\n");
		}
		buff.append(fusionAnalysis.getRelResultsAfter(node).toString());
		buff.append("\n");
		return false;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		buff.append(node.getName() + "{");
		buff.append("\n");
		buff.append(fusionAnalysis.getStartingResults(node).toString());
		buff.append("\n");
		
		if (node.getBody() != null) {
			node.getBody().accept(this);
		}
		buff.append("}");
		buff.append("\n");
		buff.append(fusionAnalysis.getRelResultsAfter(node).toString());
		buff.append("//" + node.getName());
		buff.append("\n");
		return false;
	}

	@Override
	public boolean visit(ReturnStatement node) {
		buff.append(node.toString().trim());
		buff.append("\n");
		buff.append(fusionAnalysis.getRelResultsAfter(node).toString());
		buff.append("\n");
		return false;
	}

	@Override
	public boolean visit(SwitchStatement node) {
		buff.append("switch (" + node.getExpression() + ") {");
		buff.append("\n");
		
		buff.append(fusionAnalysis.getRelResultsAfter(node.getExpression()).toString());		
		buff.append("\n");

		for (Statement statement : (List<Statement>)node.statements()) {
			statement.accept(this);
		}
		
		buff.append("}");
		buff.append("\n");
		buff.append(fusionAnalysis.getRelResultsAfter(node).toString());
		buff.append("\n");
		return false;
	}

	@Override
	public boolean visit(ThrowStatement node) {
		buff.append(node.toString().trim());
		buff.append("\n");
		buff.append(fusionAnalysis.getRelResultsAfter(node).toString());
		buff.append("\n");
		return false;
	}

	@Override
	public boolean visit(TryStatement node) {
		buff.append("try {");
		buff.append("\n");
		node.getBody().accept(this);
		buff.append("}");
		buff.append("\n");
		for (CatchClause catcher : (List<CatchClause>)node.catchClauses()) {
			buff.append("catch (" + catcher.getException() + ") {");
			catcher.getBody().accept(this);
			buff.append("\n");
			buff.append("}");
			buff.append("\n");
		}
		
		if (node.getFinally() != null) {
			buff.append("finally {");
			buff.append("\n");
			node.getFinally().accept(this);
		}

		buff.append(fusionAnalysis.getRelResultsAfter(node).toString());
		buff.append("\n");
		return false;
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		buff.append(node.toString().trim());
		buff.append("\n");
		Expression lastInit = null;
		Iterator itr = node.fragments().iterator();
		while (itr.hasNext()) {
			VariableDeclarationFragment last = (VariableDeclarationFragment) itr.next();
			if (last.getInitializer() != null)
				lastInit = last.getInitializer();
		}
		if (lastInit != null)
			buff.append(fusionAnalysis.getRelResultsAfter(lastInit).toString());
		else
			buff.append(fusionAnalysis.getRelResultsAfter(node).toString());
		buff.append("\n");
		return false;
	}

	@Override
	public boolean visit(WhileStatement node) {
		buff.append("while (" + node.getExpression() + ") {");
		buff.append("\n");
		buff.append(fusionAnalysis.getSpecificRelResultsAfter(node.getExpression(), BooleanLabel.getBooleanLabel(true)).toString());
		buff.append("\n");
		node.getBody().accept(this);
		buff.append("}");
		buff.append("\n");
		buff.append(fusionAnalysis.getSpecificRelResultsAfter(node.getExpression(), BooleanLabel.getBooleanLabel(false)).toString());
		buff.append("\n");
		return false;
	}

	public String getResult() {
		return buff.toString();
	}
}
