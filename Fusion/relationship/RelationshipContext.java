package edu.cmu.cs.crystal.analysis.relationship;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.cmu.cs.crystal.analysis.relationship.ThreeValue;

public class RelationshipContext {
	private Set<Relationship> trueRels;
	private Set<Relationship> falseRels;	
	
	public RelationshipContext() {
		trueRels = new HashSet<Relationship>();
		falseRels = new HashSet<Relationship>();
	}
	
	public Set<Relationship> getTrueRels() {
		return new HashSet<Relationship>(trueRels);
	}
	
	public Set<Relationship> getFalseRels() {
		return new HashSet<Relationship>(falseRels);
	}

	public void setRelationship(Relationship rel, ThreeValue tv) {
		if (tv == ThreeValue.TRUE) {
			falseRels.remove(rel);
			trueRels.add(rel);
		}
		else if (tv == ThreeValue.FALSE) {
			trueRels.remove(rel);
			falseRels.add(rel);
		}
		else {
			trueRels.remove(rel);
			falseRels.remove(rel);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((falseRels == null) ? 0 : falseRels.hashCode());
		result = prime * result
				+ ((trueRels == null) ? 0 : trueRels.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final RelationshipContext other = (RelationshipContext) obj;
		if (falseRels == null) {
			if (other.falseRels != null)
				return false;
		} else if (!falseRels.equals(other.falseRels))
			return false;
		if (trueRels == null) {
			if (other.trueRels != null)
				return false;
		} else if (!trueRels.equals(other.trueRels))
			return false;
		return true;
	}

	
	public String toString() {
		String str = "{";
		
		for (Relationship rel : trueRels)
			str += rel.toString() + ", ";

		for (Relationship rel : falseRels)
			str += "!" + rel.toString() + ", ";
		str += "}";
		
		return str;
	}
}
