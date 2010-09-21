package edu.cmu.cs.fusion.debugging;

import edu.cmu.cs.fusion.alias.AliasContext;
import edu.cmu.cs.fusion.relationship.RelationshipContext;

/**
 * Stores the results from before a statement on a particular line of code.
 * @author ciera
 *
 */
public class DebugInfo {
	private String statement;
	private int lineNumber;
	private AliasContext aliases;
	private RelationshipContext rels;
	
	public DebugInfo(String statement, int lineNumber, AliasContext aliases,
			RelationshipContext rels) {
		super();
		this.statement = statement;
		this.lineNumber = lineNumber;
		this.aliases = aliases;
		this.rels = rels;
	}
	public String getStatement() {
		return statement;
	}
	public int getLineNumber() {
		return lineNumber;
	}
	public AliasContext getAliases() {
		return aliases;
	}
	public RelationshipContext getRels() {
		return rels;
	}
	
	
}
