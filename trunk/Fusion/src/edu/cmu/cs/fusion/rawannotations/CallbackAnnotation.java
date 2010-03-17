package edu.cmu.cs.fusion.rawannotations;

import edu.cmu.cs.crystal.annotations.ICrystalAnnotation;

public class CallbackAnnotation implements ICrystalAnnotation {
	static private final String NAME = "name";

	private String name;
	
	public String getName() {
		return name;
	}


	public Object getObject(String key) {
		if (key.equals(NAME)) 
			return name;
		else
			return null;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setObject(String key, Object value) {
		if (key.equals(NAME)) {
			name = (String)value;
		}
	}
}
