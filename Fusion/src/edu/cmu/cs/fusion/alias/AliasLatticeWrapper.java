package edu.cmu.cs.fusion.alias;

import java.util.HashSet;
import java.util.Set;

import edu.cmu.cs.crystal.analysis.alias.AliasLE;
import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.tac.model.Variable;

@Deprecated
public class AliasLatticeWrapper implements AliasContext {
	
	private TupleLatticeElement<Variable, AliasLE> lattice;

	public AliasLatticeWrapper(TupleLatticeElement<Variable, AliasLE> lattice) {
		this.lattice = lattice;
	}
	
	public Set<ObjectLabel> getAliases(Variable var) {
		return lattice.get(var).getLabels();
	}

	public Set<ObjectLabel> getAllAliases() {
		Set<ObjectLabel> allLabels = new HashSet<ObjectLabel>();
		
		for (Variable var : lattice.getKeySet()) {
			AliasLE aliases = lattice.get(var);
			allLabels.addAll(aliases.getLabels());
		}
		
		return allLabels;
	}
	
	public String toString() {
		String str = "Points-to: " + lattice.toString() + "\nTypes: {";
		for (ObjectLabel label : getAllAliases()) {
			str += label + ":" + label.getType().getQualifiedName() + ", ";
		}
		str += "}";
		return str;
	}

	public Set<Variable> getVariables() {
		// TODO Auto-generated method stub
		return null;
	}

}
