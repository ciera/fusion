package edu.cmu.cs.fusion.rawannotations;

import edu.cmu.cs.crystal.annotations.ICrystalAnnotation;

public class RelationAnnotation implements ICrystalAnnotation {
	static private final String typeName = "types";

	private String name;
	private Class[] types;
	
	
	public String getName() {
		return name;
	}


	public Object getObject(String key) {
		if (key.equals(typeName)) 
			return types;
		else
			return null;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setObject(String key, Object value) {
		if (key.equals(typeName)) {
			types = (Class[])value;
		}
	}
}
