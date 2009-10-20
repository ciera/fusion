package edu.cmu.cs.fusion.parsers;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import edu.cmu.cs.crystal.util.Utilities;
import edu.cmu.cs.fusion.constraint.Operation;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.operations.MethodInvocationOp;

public class OperationParser {

	/**
	 * Parses an operation. Right now, it only parses a method invocation operation
	 * @param opString The operation to parse. This assumes that it is actually parseable, and may
	 * throw errors if it is not actually parseable.
	 * @param contextType The IType which we should use as the context to resolve qualified names
	 * @return The operation represented by opString, under the given context.
	 * @throws JavaModelException
	 */
	public Operation parse(String opString, IType contextType) throws JavaModelException {
		String receiverType = opString.substring(0, opString.indexOf('.'));
		String opName = opString.substring(opString.indexOf('.') + 1, opString.indexOf('('));
		String[] paramList = opString.substring(opString.indexOf('(') + 1, opString.indexOf(')')).split(",");
		SpecVar[] vars = new SpecVar[paramList.length];
		String[] paramTypes = new String[paramList.length];
		String returnType = opString.substring(opString.indexOf(':')).trim();
		
		receiverType = Utilities.resolveType(contextType, receiverType);
		returnType = Utilities.resolveType(contextType, returnType);
		
		for (int ndx = 0; ndx < paramList.length; ndx++) {
			int space = paramList[ndx].indexOf(' ');
			vars[ndx] = new SpecVar(paramList[ndx].substring(0, space));
			paramTypes[ndx] = Utilities.resolveType(contextType, paramList[ndx].substring(space + 1));
		}
		
		
		return new MethodInvocationOp(opName, receiverType, vars, paramTypes, returnType);
	}

}
