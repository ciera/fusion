package edu.cmu.cs.fusion.alias;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.fusion.Binding;
import edu.cmu.cs.fusion.constraint.Substitution;

public class AliasDelta implements Iterable<Variable> {
	private HashMap<Variable, Set<ObjectLabel>> aliases;
	
	public AliasDelta(ConsList<Binding> boundVars, List<Substitution> subs) {
		aliases = new HashMap<Variable, Set<ObjectLabel>>();
		for (Binding bound : boundVars) {
			Set<ObjectLabel> variables = new HashSet<ObjectLabel>();
			
			for (Substitution sub : subs) {
				if (sub != null)
					variables.add(sub.getSub(bound.getSpec()));
			}
			
			aliases.put(bound.getSource(), variables);
		}
	}

	
	public AliasDelta(HashMap<Variable, ObjectLabel> changes) {
		aliases = new HashMap<Variable, Set<ObjectLabel>>();
		for (Entry<Variable, ObjectLabel> entry : changes.entrySet()) {
			Set<ObjectLabel> variables = new HashSet<ObjectLabel>();
			variables.add(entry.getValue());
			aliases.put(entry.getKey(), variables);
		}
	}
	

	public AliasDelta() {
		aliases = new HashMap<Variable, Set<ObjectLabel>>();
	}

	public Iterator<Variable> iterator() {
		return aliases.keySet().iterator();
	}
	
	public Set<ObjectLabel> getChanges(Variable variable) {
		Set<ObjectLabel> values = aliases.get(variable);
		if (values == null)
			return null;
		else
			return new HashSet<ObjectLabel>(values);
	}
	
	public void addChange(Variable source, ObjectLabel sub) {
		Set<ObjectLabel> variables = aliases.get(source);
		if (variables == null) {
			variables = new HashSet<ObjectLabel>();
			aliases.put(source, variables);
			
		}
		variables.add(sub);
	}

	public static AliasDelta join(List<AliasDelta> aliasDeltas) {
		AliasDelta combined = new AliasDelta();
		for (AliasDelta delta : aliasDeltas) {
			for (Entry<Variable, Set<ObjectLabel>> entry : delta.aliases.entrySet()) {		
				Set<ObjectLabel> existing = combined.aliases.get(entry.getKey());
				if (existing == null) {
					existing = new HashSet<ObjectLabel>(entry.getValue());
					combined.aliases.put(entry.getKey(), existing);
				}
				else
					existing.retainAll(entry.getValue());
			}
		}
		return combined;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aliases == null) ? 0 : aliases.hashCode());
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
		AliasDelta other = (AliasDelta) obj;
		if (aliases == null) {
			if (other.aliases != null)
				return false;
		} else if (!aliases.equals(other.aliases))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String str = "{";
		for (Entry<Variable, Set<ObjectLabel>> entry : aliases.entrySet()) {
			str += entry.getKey() + "=>";
			str += entry.getValue().toString();
			str += ", ";
		}
		return str + "}";
	}


}
