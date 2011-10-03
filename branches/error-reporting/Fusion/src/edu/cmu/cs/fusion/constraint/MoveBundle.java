package edu.cmu.cs.fusion.constraint;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;

import edu.cmu.cs.fusion.FusionAnalysis;
import edu.cmu.cs.fusion.constraint.Move.MoveType;

public class MoveBundle implements Comparable<MoveBundle>{

	MethodDeclaration originalMethodDeclaration, modifiedMethodDeclaration;
	public MoveBundle(Move[] partial, ASTNode failing) {
		//this glue can be improved on demand
		MoveType type =partial[0].getMoveType();
		for(Move m: partial)
			if(m.getMoveType()!=type)
				throw new IllegalStateException("I do not handle mixed moves");
		
		ASTNode node = failing;
		while(node !=null && node instanceof MethodDeclaration ==false)
			node = node.getParent();
		assert(node!=null):"no method declaration parent found?";
		originalMethodDeclaration = (MethodDeclaration)node;
		
		modifiedMethodDeclaration = (MethodDeclaration) ASTNode.copySubtree(node.getAST(), originalMethodDeclaration);
		
		//how to find corresponding failing ast node within new tree
		failing = findByStart(modifiedMethodDeclaration,failing.getStartPosition());
		assert(failing!=null);
		//can assume partial.length>0 && partial[i]!=null forall i
		assert(partial.length>0);
		if(type==Move.MoveType.MAKEBRANCH)
		{
			IfStatement ifStat = failing.getAST().newIfStatement();
			if(partial.length==1)
			{
				ifStat.setExpression(partial[0].getExpression());
			}
			else
			{
				InfixExpression conjs = failing.getAST().newInfixExpression();
				conjs.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
				conjs.setRightOperand(partial[0].getExpression());
				conjs.setRightOperand(partial[1].getExpression());
				for(int i=2;i<partial.length;i++)
				{
					conjs.extendedOperands().add(partial[i].getExpression());
				}
				ifStat.setExpression(conjs);
			}
			Statement containingStat = getParentStatementInBlock(failing);
			if(containingStat==null) throw new IllegalStateException("no enclosing block");
			Block enclosingBlock = (Block) containingStat.getParent();
			int indexOf = enclosingBlock.statements().indexOf(containingStat);
			assert(indexOf!=-1);				
			ifStat.setThenStatement((Statement)ASTNode.copySubtree(failing.getAST(), containingStat));				
			enclosingBlock.statements().add(indexOf,ifStat);
			containingStat.delete();
			
			
		}
		else if(type == MoveType.INSERTEXPRESSION && partial.length==1)
		{
			ExpressionStatement statement = failing.getAST().newExpressionStatement(partial[0].getExpression());			
			Statement containingStat = getParentStatementInBlock(failing);
			if(containingStat==null) throw new IllegalStateException("no enclosing block");
			Block enclosingBlock = (Block) containingStat.getParent();
			int indexOf = enclosingBlock.statements().indexOf(containingStat);
			assert(indexOf!=-1);
			enclosingBlock.statements().add(indexOf,statement);//correct way to add block?
		}
		else if(type == MoveType.REMOVENODE && partial.length==1)
		{
			Statement containingStat = getParentStatementInBlock(failing);
			if(containingStat==null) throw new IllegalStateException("no enclosing block");
			containingStat.delete();
		}		
	}
	private ASTNode findByStart(ASTNode node, final int start) {

		class FindByStartVisitor extends ASTVisitor{
			ASTNode found = null;
			public void endVisit(MethodInvocation method)
			{
				if(method.getStartPosition()==start)
					found = method;
			}			
			public void endVisit(ReturnStatement ret)
			{
				if(ret.getStartPosition()==start)
					found = ret;
			}
			
		}
		FindByStartVisitor visitor = new FindByStartVisitor();
		node.accept(visitor);
		return visitor.found;
	}
	/**
	 * @return node A such that A.getParent() instanceof Block and input node is child of A, or null  
	 */
	private static Statement getParentStatementInBlock(ASTNode node)
	{			
		assert(node instanceof Block ==false);
		while(node != null && node.getParent() instanceof Block==false)
			{
				node = node.getParent();					
			}
//		failing.getStructuralProperty(property)
		return (Statement)node;
	}
	public String toString ()
	{
		return modifiedMethodDeclaration.toString();
	}
	private static FusionAnalysis scorer ;
	//TODO implement
	private int score()
	{
		return 0;
	}
	private int score;
	@Override
	public int compareTo(MoveBundle o) {		
		return o.score - this.score;
	}
}
