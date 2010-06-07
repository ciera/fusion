package edu.cmu.cs.fusion.alias;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import edu.cmu.cs.crystal.tac.model.Variable;

public class AliasDelta implements Iterable<Variable> {
	public HashMap<Variable, Element> aliases = new HashMap<Variable, Element>();
	
	private static class Element {
		Set<ObjectLabel> set = new HashSet<ObjectLabel>();
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Element other = (Element) obj;
			if (other.set.size() != set.size())
				return false;
			for (ObjectLabel label : set)
				if (!other.set.contains(label))
					return false;
			return true;
		}
		
		@Override
		public String toString() {
			return set.toString();
		}
	}
	
	public Iterator<Variable> iterator() {
		return aliases.keySet().iterator();
	}
	
	public Set<ObjectLabel> getChanges(Variable variable) {
		Element element = aliases.get(variable);
		if (element == null || element == TOP_ELEMENT || element == BOT_ELEMENT)
			return null;
		else
			return new HashSet<ObjectLabel>(element.set);
	}
	
	static private Element TOP_ELEMENT = new Element();
	static private Element BOT_ELEMENT = new Element();
	
	public void setTop(Variable source) {
		aliases.put(source, TOP_ELEMENT);
	}

	public void setBottom(Variable source) {
		aliases.put(source, BOT_ELEMENT);
	}

	public void addChange(Variable source, ObjectLabel sub) {
		Element element = aliases.get(source);
		if (element == null) {
			element = new Element();
			aliases.put(source, element);
			
		}
		element.set.add(sub);
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
		
		if (aliases.size() != other.aliases.size())
			return false;
		for (Entry<Variable, Element> entry : aliases.entrySet()) {
			Element otherEl = other.aliases.get(entry.getKey());
			
			if (otherEl == null || !otherEl.equals(entry.getValue()))
				return false;
		}
		return true;
	}

	public static AliasDelta join(List<AliasDelta> specDeltas) {
		AliasDelta combined = new AliasDelta();
		for (AliasDelta delta : specDeltas) {
			for (Entry<Variable, Element> entry : delta.aliases.entrySet()) {
				Element existing = combined.aliases.get(entry.getKey());
				if (existing == null) {
					combined.aliases.put(entry.getKey(), entry.getValue());
				}
				else {
					combined.aliases.put(entry.getKey(), AliasDelta.join(existing, entry.getValue()));
				}
			}
		}
		return combined;
	}

	static private Element join(Element left, Element right) {
		if (left == TOP_ELEMENT || right == TOP_ELEMENT)
			return TOP_ELEMENT;
		else if (left == BOT_ELEMENT)
			return right;
		else if (right == BOT_ELEMENT)
			return left;
		else {
			Element element = new Element();
			element.set.addAll(left.set);
			element.set.addAll(right.set);
			return element;
		}	
	}
	
	@Override
	public String toString() {
		String str = "{";
		for (Entry<Variable, Element> entry : aliases.entrySet()) {
			str += entry.getKey() + "=>";
			if (entry == TOP_ELEMENT)
				str += "TOP";
			else if (entry == BOT_ELEMENT)
				str += "BOT";
			else
				str += entry.getValue().toString();
			str += ", ";
		}
		return str + "}";
	}
}
