package edu.cmu.cs.fusion;

import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.relationship.RelationshipContext;

public interface DeclarativeRetriever extends Observer {

	/**
	 * Will receive a callback for every fusion file found.
	 */
	public void update(Observable o, Object arg);

	/**
	 * Retrieves the relationships for all the XML files in the resource. This must be called before
	 * calling getStartContext. For each XML file found, it will find the schema to use to retrieve the relationships
	 * and load them up.
	 * 
	 * Every call to this will reset the loaded data. It should be reloaded whenever the type hierarchy changes.
	 * 
	 * @param resource the top of a resource tree
	 * @param tHierarchy the type hierarchy to use
	 * @throws CoreException
	 */
	public void retrieveRelationships(IResource resource,
			TypeHierarchy tHierarchy) throws CoreException;

	public Set<ObjectLabel> getStartingAliases(Variable var);

	public RelationshipContext getStartContext();

	/**
	 * Make sure retrieveRelationships has already been called before calling this method.
	 * 
	 * Returns all of the ObjectLabels that are known, either by way of 
	 */
	public Set<ObjectLabel> getAllLabels();

}