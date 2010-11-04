package edu.cmu.cs.fusion.xml;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.functions.ExtensionFunctionCall;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.SingletonIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.BuiltInAtomicType;
import net.sf.saxon.value.BooleanValue;
import edu.cmu.cs.crystal.util.TypeHierarchy;

/**
 * A Fusion-defined call that can be used by the XQueries. It will use the type hierarchy
 * to check for subtyping.
 * @author ciera
 *
 */
public class TypeComparisonCall extends ExtensionFunctionCall {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TypeHierarchy types;

	public TypeComparisonCall(TypeHierarchy types) {
		this.types = types;
	}

	@Override
	public SequenceIterator call(SequenceIterator[] args, XPathContext arg1)
			throws XPathException {
		String subType = args[0].next().getStringValue();
		String baseType = args[1].next().getStringValue();
		
		boolean isSubType = types.isSubtypeCompatible(subType, baseType);
		
		Item result = new BooleanValue(isSubType, BuiltInAtomicType.BOOLEAN);
		
		return SingletonIterator.makeIterator(result);
	}

}
