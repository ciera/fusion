package edu.cmu.cs.fusion.rawannotations;

import edu.cmu.cs.crystal.annotations.ICrystalAnnotation;

public class ConstraintAnnotation implements ICrystalAnnotation {
	static private final String opName = "op";
	static private final String triggerName = "trigger";
	static private final String requiresName = "requires";
	static private final String effectName = "effect";

	private String name;
	private String opString;
	private String triggerString;
	private String requiresString;
	private String[] effectStrings;
	
	
	public String getName() {
		return name;
	}


	public Object getObject(String key) {
		if (key.equals(opName)) 
			return opString;
		else if (key.equals(triggerName)) 
			return triggerString;
		else if (key.equals(requiresName))
			return requiresString;
		else if (key.equals(effectName))
			return effectStrings;
		else
			return null;
	}


	public void setName(String name) {
		this.name = name;
	}

	public void setObject(String key, Object value) {
		if (key.equals(opName)) {
			opString = (String)value;
		}
		else if (key.equals(triggerName)) {
			triggerString = (String)value;
		}
		else if (key.equals(requiresName)){
			requiresString = (String)value;
		}
		else if (key.equals(effectName)) {
			effectStrings = (String[])value;
		}
	}
}
