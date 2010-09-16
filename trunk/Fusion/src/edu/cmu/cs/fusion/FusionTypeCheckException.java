package edu.cmu.cs.fusion;



/**
 * For reporting typechecking errors. Use this when the specification parses fine, but the type
 * is not correct.
 * @author ciera
 *
 */
public class FusionTypeCheckException extends Exception {

	public FusionTypeCheckException(Relation relation, int ndx, String type,
			String param) {
		super("Relationhip " + relation.getName() + " needs type " + relation.getFullyQualifiedTypes()[ndx] + " at param " + ndx + " but received type " + type + " from param " + param + ".");
	}

	public FusionTypeCheckException(Exception e) {
		super(e);
	}

}
