package edu.cmu.cs.fusion.alias;

import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.TypeHierarchy;

public class MayPointsToLatticeOps implements ILatticeOperations<MayPointsToAliasContext> {
	
	private TypeHierarchy types;

	public MayPointsToLatticeOps(TypeHierarchy types) {
		this.types = types;
	}

	/**
	 * @return true if info has more precise aliasing information than reference. That is, info is a strict subset of reference
	 */
	public boolean atLeastAsPrecise(MayPointsToAliasContext info, MayPointsToAliasContext reference, ASTNode node) {
		//info must know a subset of reference's aliases
		if (!(reference.getAllAliases().containsAll(info.getAllAliases())))
			return false;
		
		//info must have a subset of the variables, which
		//also must have a subset of references.
		for (Entry<Variable, Set<ObjectLabel>> entry : info) {
			Set<ObjectLabel> infoLabels = entry.getValue();
			Set<ObjectLabel> referenceLabels = reference.getAliases(entry.getKey());
			if (referenceLabels == null)
				return false;
			if (!(referenceLabels.containsAll(infoLabels)))
				return false;
		}
		
		return true;
	}

	public MayPointsToAliasContext bottom() {
		return new MayPointsToAliasContext(types);
	}

	public MayPointsToAliasContext copy(MayPointsToAliasContext original) {
		return original.clone();
	}

	public MayPointsToAliasContext join(MayPointsToAliasContext someInfo,
			MayPointsToAliasContext otherInfo, ASTNode node) {
		
		MayPointsToAliasContext joined = someInfo.clone();
		
		joined.addLabels(otherInfo.getAllAliases());
		
		for (Entry<Variable, Set<ObjectLabel>> entry : otherInfo)
			joined.addPointsTo(entry.getKey(), entry.getValue());
		
		return joined;
	}

}
