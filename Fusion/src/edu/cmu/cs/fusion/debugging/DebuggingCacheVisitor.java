package edu.cmu.cs.fusion.debugging;

import java.util.Iterator;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;

import edu.cmu.cs.fusion.FusionAnalysis;
import edu.cmu.cs.fusion.relationship.RelationshipContext;

public class DebuggingCacheVisitor extends ASTVisitor {
	private FusionCache cache;
	private FusionAnalysis analysis;

	public DebuggingCacheVisitor(FusionAnalysis analysis, FusionCache cache) {
		this.cache = cache;
		this.analysis = analysis;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		RelationshipContext context = analysis.getStartingResults(node);
		CompilationUnit cu = (CompilationUnit) node.getRoot();
	
		//get first statement
		if (node.getBody() != null && !node.getBody().statements().isEmpty()) {
			Statement first = (Statement) node.getBody().statements().get(0);
			int startLine = cu.getLineNumber(first.getStartPosition());
			int endLine = cu.getLineNumber(first.getStartPosition() + first.getLength());
			cache.addResult(startLine, endLine, context);
		}
		
		return true;
	}

	@Override
	public boolean visit(Block node) {
		Iterator<Statement> itr = (Iterator<Statement>) node.statements().iterator();
		int startLine, endLine;
		Statement stmnt;
		RelationshipContext context = null;
				
		if (itr.hasNext()) {
			stmnt = itr.next();
			context = analysis.getRelResultsAfter(stmnt);
		}
		
		while (itr.hasNext()) {
			stmnt = itr.next();
			startLine = getStartLine(stmnt);
			endLine = getEndLine(stmnt);
			cache.addResult(startLine, endLine, context);
			context = analysis.getRelResultsAfter(stmnt);
		}
		return true;
	}
	
	private int getStartLine(ASTNode node) {
		return ((CompilationUnit) node.getRoot()).getLineNumber(node.getStartPosition());
	}
	
	private int getEndLine(ASTNode node) {
		return ((CompilationUnit) node.getRoot()).getLineNumber(node.getStartPosition() + node.getLength());
	}
	

}
