package edu.cmu.cs.fusion.constraint;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;


public class Move {
	public static enum MoveType 
	{
		MAKEBRANCH,INSERTEXPRESSION,REMOVENODE,NOTHING;
		public String toString()
		{
			switch(this)
			{
			case MAKEBRANCH: return "MAKEBRANCH";
			case INSERTEXPRESSION: return "INSERTEXPRESSION";
			case REMOVENODE: return "REMOVENODE";
			case NOTHING: return "NOTHING";
			default: throw new IllegalStateException();
			}
		}
	};
	private ASTNode moveNode;
	private MoveType moveType;
	public static Move IDENTITY = new Move(null,MoveType.NOTHING);
	public Move(ASTNode moveNode, MoveType moveType) {
		super();
		this.moveNode = moveNode;
		this.moveType = moveType;
	}
	public MoveType getMoveType()
	{
		return moveType;
	}
	/**
	 * Move must be insertexpression or makebranch
	 * @return
	 */
	public Expression getExpression()
	{
		return (Expression)moveNode;
	}
	public String toString()	
	{
		return this.moveType +"("+moveNode+")";
	}
}
