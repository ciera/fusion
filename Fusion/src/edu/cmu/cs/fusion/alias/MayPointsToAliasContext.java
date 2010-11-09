package edu.cmu.cs.fusion.alias;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jdt.core.dom.ITypeBinding;

import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.TypeHierarchy;

/**
 * This is a mutable lattice, be sure to clone.
 * @author ciera
 *
 */
public class MayPointsToAliasContext implements AliasContext, Iterable<Entry<Variable, Set<ObjectLabel>>> {
	private Map<Variable, Set<ObjectLabel>> pointsTo;
	private Set<ObjectLabel> allLabels;
	private TypeHierarchy types;
	
	public MayPointsToAliasContext(TypeHierarchy types) {
		pointsTo = new HashMap<Variable, Set<ObjectLabel>>();
		allLabels = new HashSet<ObjectLabel>();
		this.types = types;
	}
	
	public Set<ObjectLabel> getAliases(Variable var) {
		return pointsTo.get(var);
	}

	public Set<ObjectLabel> getAllAliases() {
		return allLabels;
	}
	
	public Iterator<Entry<Variable, Set<ObjectLabel>>> iterator() {
		return pointsTo.entrySet().iterator();
	}


	public void addPointsTo(Variable var, ObjectLabel label) {
		Set<ObjectLabel> labels = pointsTo.get(var);
		
		if (labels == null) {
			labels = new HashSet<ObjectLabel>();
			pointsTo.put(var, labels);
		}
		labels.add(label);
	}

	public void reset(Variable var, Set<ObjectLabel> labels) {
		Set<ObjectLabel> varLabels = pointsTo.get(var);
		if (varLabels != null)
			pointsTo.get(var).clear();
		else {
			varLabels = new HashSet<ObjectLabel>();
			pointsTo.put(var, varLabels);
		}
		varLabels.addAll(labels);
	}
	

	public void resetPointsTo(Variable var) {
		Set<ObjectLabel> labels = pointsTo.get(var);
		if (labels != null)
			pointsTo.get(var).clear();
	}
	
	public void addLabel(ObjectLabel label) {
		allLabels.add(label);
	}

	/**
	 * Adds the items in aliases to the points-to list of var.
	 * @param var
	 * @param aliases
	 */
	public void addPointsTo(Variable var, Set<ObjectLabel> aliases) {
		assert(aliases != null);
		Set<ObjectLabel> labels = pointsTo.get(var);
		
		if (labels == null) {
			labels = new HashSet<ObjectLabel>();
			pointsTo.put(var, labels);
		}
		labels.addAll(aliases);
	}

	public void addLabels(Set<ObjectLabel> labels) {
		allLabels.addAll(labels);
	}
	
	public MayPointsToAliasContext clone() {
		MayPointsToAliasContext clone = new MayPointsToAliasContext(types);
		clone.allLabels.addAll(allLabels);
		for (Entry<Variable, Set<ObjectLabel>> entry : pointsTo.entrySet())
			clone.pointsTo.put(entry.getKey(), new HashSet<ObjectLabel>(entry.getValue()));
		return clone;
	}

	public void addPointsToAnySubtype(Variable paramVar, ITypeBinding varType) {
		Set<ObjectLabel> varLabels = pointsTo.get(paramVar);
		
		if (varLabels == null) {
			varLabels = new HashSet<ObjectLabel>();
			pointsTo.put(paramVar, varLabels);
		}
		
		for (ObjectLabel label : allLabels) {
			if (types.isSubtypeCompatible(label.getType().getQualifiedName(), varType.getQualifiedName()))
				varLabels.add(label);
		}
	}

	public Set<Variable> getVariables() {
		return pointsTo.keySet();
	}
	
	public String toString() {
		return "All labels: " + allLabels.toString() + "\nPoints-to: " + pointsTo.toString();
	}

	public void cleanPotentialLabels() {
		List<ObjectLabel> toRemove = new LinkedList<ObjectLabel>();
		
		OUTER: for (ObjectLabel label : allLabels) {
			if (!label.isTemporary())
				continue OUTER;
			INNER: for (Set<ObjectLabel> pointedTo : pointsTo.values()) {
				if (pointedTo.contains(label)) {
					label.makePermanent();
					continue OUTER;
				}
			}
			toRemove.add(label);
		}
		
		for (ObjectLabel label : toRemove)
			allLabels.remove(label);
	}
}
