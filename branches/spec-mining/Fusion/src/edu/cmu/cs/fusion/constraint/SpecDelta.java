package edu.cmu.cs.fusion.constraint;

import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.fusion.Binding;
import edu.cmu.cs.fusion.alias.AliasDelta;

public class SpecDelta {
	
	public static SpecDelta createSubstitutionSpecDelta(Substitution sub) {
		return new SpecDelta(sub, Type.NORMAL);
	}
	
	public static SpecDelta createBottomSpecDelta(Substitution sub) {
		return new SpecDelta(sub, Type.BOT);
	}
	
	public static SpecDelta createTopSpecDelta(Substitution sub) {
		return new SpecDelta(sub, Type.TOP);
	}
	
	private enum Type {TOP, BOT, NORMAL};
	private Substitution subs;
	private Type type;
	
	private SpecDelta(Substitution subs, Type type) {
		this.subs = subs;
		this.type = type;
	}
	
	public AliasDelta turnToSource(ConsList<Binding> boundVars) {
		AliasDelta delta = new AliasDelta();
		for (Binding bound : boundVars) {
			if (type == Type.TOP) {
				delta.setTop(bound.getSource());
			}
			else if (type == Type.BOT) {
				delta.setBottom(bound.getSource());
			}
			else {
				delta.addChange(bound.getSource(), subs.getSub(bound.getSpec()));
			}
		}
		return delta;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subs == null) ? 0 : subs.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		SpecDelta other = (SpecDelta) obj;
		if (subs == null) {
			if (other.subs != null)
				return false;
		} else if (!subs.equals(other.subs))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		if (this.type == Type.TOP)
			return "TOP";
		if (this.type == Type.BOT)
			return "BOT";
		return subs.toString();
	}
}
