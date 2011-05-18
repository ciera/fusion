package edu.cmu.cs.fusion;

import edu.cmu.cs.fusion.alias.AliasContext;
import edu.cmu.cs.fusion.relationship.RelationshipContext;

public class FusionLattice<AC extends AliasContext> {
	private AC aliasesForTrigger;
	private AC aliasesPostRestrict;
	private RelationshipContext relContext;
	
	public FusionLattice(RelationshipContext relContext, AC aliasesForTrigger, AC aliasesPostRestrict) {
		super();
		this.aliasesForTrigger = aliasesForTrigger;
		this.aliasesPostRestrict = aliasesPostRestrict;
		this.relContext = relContext;
	}
	
	public AC getAliasesForTrigger() {
		return aliasesForTrigger;
	}
	public AC getAliasContext() {
		return aliasesPostRestrict;
	}
	public RelationshipContext getRelContext() {
		return relContext;
	}
}
