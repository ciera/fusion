package edu.cmu.cs.fusion.constraint.requestors;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.core.search.TypeReferenceMatch;

import edu.cmu.cs.crystal.util.Utilities;
import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.RelationsEnvironment;
import edu.cmu.cs.fusion.ReportingUtility;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.Effect;
import edu.cmu.cs.fusion.constraint.Operation;
import edu.cmu.cs.fusion.constraint.RelEffect;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.operations.BeginOfMethodOp;
import edu.cmu.cs.fusion.constraint.predicates.TruePredicate;

public class CallbackRequestor extends SearchRequestor {

	public CallbackRequestor(RelationsEnvironment rels) {
		this.rels = rels;
		parsed = new HashSet<IAnnotation>();
		constraints = new LinkedList<Constraint>();
	}
	
	private RelationsEnvironment rels;
	private HashSet<IAnnotation> parsed;
	private List<Constraint> constraints;
	
	@Override
	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		if (match.getAccuracy() == SearchMatch.A_INACCURATE)
			return;
		TypeReferenceMatch refMatch = (TypeReferenceMatch)match;
		
		IMethod methodType = (IMethod) refMatch.getElement();			
		IAnnotation callback = (IAnnotation) refMatch.getLocalElement();
		
		if (parsed.contains(callback))
			return; //SCREW YOU ECLIPSE!!!!
		
		parseCallback(callback, methodType);
		parsed.add(callback);
	}

	private void parseCallback(IAnnotation callback, IMethod methodType) throws JavaModelException {
		IType declaringType = methodType.getDeclaringType();
		String relName = (String)callback.getMemberValuePairs()[0].getValue();
		Relation relation = rels.findRelation(Utilities.resolveType(declaringType, relName));
		
		if (relation == null) {
			ReportingUtility.reportParseError(callback.getResource(), callback.getNameRange(), "Unknown relation " + relName);
			return;
		}
		
		if (relation.getFullyQualifiedTypes().length != 1) {
			ReportingUtility.reportParseError(callback.getResource(), callback.getNameRange(), "Callback can only be used with one parameter");
			return;
		}
		
		String[] paramTypes = new String[methodType.getParameterTypes().length];
		SpecVar[] params = new SpecVar[paramTypes.length];
		for (int ndx = 0; ndx < paramTypes.length; ndx++) {
			paramTypes[ndx] = Utilities.resolveBinaryType(declaringType, methodType.getParameterTypes()[ndx]);
			params[ndx] = new SpecVar(methodType.getParameterNames()[ndx]);
		}
		
		boolean isStatic = Flags.isStatic(methodType.getFlags());
		
		Operation op = new BeginOfMethodOp(declaringType.getFullyQualifiedName(), methodType.getElementName(), params, paramTypes, isStatic);
		List<Effect> effects = new LinkedList<Effect>();
		effects.add(RelEffect.createAddEffect(relation, new SpecVar[] {Constraint.RECEIVER}));
		
		String owner = methodType.getDeclaringType().getFullyQualifiedName();
		constraints.add(new Constraint(owner, op, new TruePredicate(), new TruePredicate(), new TruePredicate(), effects));
	}

	public List<Constraint> getConstraints() {
		return constraints;
	}
	

}
