package edu.cmu.cs.fusion;

import edu.cmu.cs.fusion.alias.AliasContext;
import edu.cmu.cs.fusion.relationship.RelationshipContext;

public class FusionLattice<AC extends AliasContext> {
//	private AC aliasesForTrigger;
//	private AC aliasesPostRestrict;
	private AC aliases;
	private RelationshipContext relContext;
	
	public FusionLattice(RelationshipContext relContext, AC aliases) {// aliasesForTrigger, AC aliasesPostRestrict) {
		super();
		this.aliases = aliases;
//		this.aliasesPostRestrict = aliasesPostRestrict;
		this.relContext = relContext;
	}
	
//	public AC getAliasesForTrigger() {
//		return aliasesForTrigger;
//	}
	public AC getAliasContext() {
		return aliases;
	}
	public RelationshipContext getRelContext() {
		return relContext;
	}
}
