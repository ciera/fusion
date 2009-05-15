package edu.cmu.cs.fusion;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;

public interface BooleanContext {
	public ThreeValue getBooleanValue(ObjectLabel label);
}
