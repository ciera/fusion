package edu.cmu.cs.fusion.xml;

import net.sf.saxon.functions.ExtensionFunctionCall;
import net.sf.saxon.functions.ExtensionFunctionDefinition;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.value.SequenceType;
import edu.cmu.cs.crystal.util.TypeHierarchy;

/**
 * The definition for an external call in XQuery to check for subtyping.
 * @author ciera
 *
 */
public class TypeComparisonDefinition extends ExtensionFunctionDefinition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TypeHierarchy types;

	public TypeComparisonDefinition(TypeHierarchy types) {
		this.types = types;
	}
	@Override
	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] {SequenceType.SINGLE_STRING, SequenceType.SINGLE_STRING};
	}

	@Override
	public StructuredQName getFunctionQName() {
		return new StructuredQName("fusion", "http://code.google.com/p/fusion", "isSubtype");
	}

	@Override
	public int getMinimumNumberOfArguments() {
		return 2;
	}

	@Override
	public SequenceType getResultType(SequenceType[] arg0) {
		return SequenceType.SINGLE_BOOLEAN;
	}
	
	@Override
	public boolean trustResultType() {return false;}

	@Override
	public ExtensionFunctionCall makeCallExpression() {
		return new TypeComparisonCall(types);
	}

}
