package edu.cmu.cs.fusion;

import edu.cmu.cs.fusion.annot.Relation.Effect;



/**
 * For reporting typechecking errors. Use this when the specification parses fine, but the type
 * is not correct.
 * @author ciera
 *
 */
public class FusionTypeCheckException extends Exception {

	public FusionTypeCheckException(String relName) {
		super("Could not find a relation with name " + relName);
	}
    
	public FusionTypeCheckException(Relation relation, int ndx, String type,
			String param) {
		super("Relationhip " + relation.getName() + " needs type " + relation.getFullyQualifiedTypes()[ndx] + " at param " + ndx + " but received type " + type + " from param " + param + ".");
	}

	public FusionTypeCheckException(Exception e) {
		super(e);
	}

	public FusionTypeCheckException(Effect effect) {
		super("Effect can only be ADD or REMOVE");
	}

	public FusionTypeCheckException(Relation relation, int expectedNumParams, int actualNumParams) {
		super("Relationship " + relation.getName() + "expected " + expectedNumParams + " params, but received " + actualNumParams);
	}

}
