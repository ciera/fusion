package edu.cmu.cs.fusion;

import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.alias.ObjectLabel;
import edu.cmu.cs.fusion.relationship.RelationshipContext;

public class DefaultRetriever implements DeclarativeRetriever {

	public Set<ObjectLabel> getAllLabels() {
		return new HashSet<ObjectLabel>();
	}

	public RelationshipContext getStartContext() {
		return new RelationshipContext(false);
	}

	public Set<ObjectLabel> getStartingAliases(Variable var) {
		return new HashSet<ObjectLabel>();
	}

	public void retrieveRelationships(IResource resource,
			TypeHierarchy tHierarchy) throws CoreException {
	}

	public void update(Observable o, Object arg) {
	}

	public boolean visit(IResource resource) throws CoreException {
		return false;
	}
}
