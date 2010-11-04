package edu.cmu.cs.fusion.debugging;

import java.util.Iterator;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;

import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.fusion.FusionAnalysis;
import edu.cmu.cs.fusion.alias.AliasContext;
import edu.cmu.cs.fusion.relationship.RelationshipContext;

public class DebuggingCacheVisitor extends ASTVisitor {
	private FusionCache cache;
	private FusionAnalysis analysis;

	public DebuggingCacheVisitor(FusionAnalysis analysis, FusionCache cache) {
		this.cache = cache;
		this.analysis = analysis;
	}

	@Override
	public boolean visit(Block node) {
		Iterator<Statement> itr = (Iterator<Statement>) node.statements().iterator();
		int startLine, endLine;
		Statement stmnt;
		Pair<? extends AliasContext, RelationshipContext> pair = null;
			
		while (itr.hasNext()) {
			stmnt = itr.next();
			startLine = getStartLine(stmnt);
			endLine = getEndLine(stmnt);
			pair = analysis.getResultsBeforeAST(stmnt);
			cache.addResult(startLine, endLine, new DebugInfo(stmnt.toString(), startLine, pair.fst(), pair.snd()));
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
