package edu.cmu.cs.fusion.alias;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;

public interface ASTTranslatable {
	
	public ASTNode getAST(AST ast);
	
}
