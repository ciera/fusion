package edu.cmu.cs.fusion.relationship;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.crystal.tac.AbstractTACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.MethodCallInstruction;
import edu.cmu.cs.crystal.tac.Variable;
import edu.cmu.cs.fusion.FusionAnalysis;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.ThreeValue;


public class RelationshipTransferFunction extends AbstractTACBranchSensitiveTransferFunction<RelationshipContext> {

	private FusionAnalysis mainAnalysis;


	public RelationshipTransferFunction(FusionAnalysis relAnalysis) {
		mainAnalysis = relAnalysis;
	}

	public RelationshipContext createEntryValue(MethodDeclaration method) {
		return new RelationshipContext();
	}



	public ILatticeOperations<RelationshipContext> createLatticeOperations(
			MethodDeclaration method) {
		return new RelationshipLatticeOperations();
	}

/*
	public IResult<RelationshipContext> transfer(MethodCallInstruction instr,
			List<ILabel> labels, RelationshipContext value) {
		IMethodBinding binding = instr.resolveBinding();
		AnnotationSummary sum = annoDB.getSummaryForMethod(binding);
		RelationshipContext tValue = null, fValue = null;
		boolean hasBoolLabels;
		
		if (sum == null)
			return LabeledSingleResult.createResult(value, labels);

		value = value.copy();
		
		hasBoolLabels = labels.contains(BooleanLabel.getBooleanLabel(true)) &&
		  labels.contains(BooleanLabel.getBooleanLabel(false));
		
		if (hasBoolLabels) {
			tValue = value.copy();
			fValue = value.copy();			
		}
			
		
		for (ICrystalAnnotation anno : sum.getReturn()) {
			if (anno instanceof RelationshipAnnotation) {
				RelationshipAnnotation relAnno = (RelationshipAnnotation)anno;
				Variable[] boundVars = getVariables(instr, relAnno, sum.getParameterNames());
				boolean isDistinctOrWild = isDistinctOrWild(boundVars, instr.getNode());
				Set<Relationship> relationships = new HashSet();
				
				createAllRelationships(relationships, new ArrayList(), boundVars, 0,
				  relAnno.getRelationshipName(), instr);
				
				for (Relationship rel : relationships) {
					//we have relevant aliases, so we can't assume anything at all.
					if (!isDistinctOrWild)
						setAllLEs(rel, ThreeValue.UNKNOWN, hasBoolLabels, value, tValue, fValue);
					//add the relationship
					else if (relAnno.isAdd())
						setAllLEs(rel, ThreeValue.TRUE, hasBoolLabels, value, tValue, fValue);
					//remove the relationship
					else if (relAnno.isRemove())
						setAllLEs(rel, ThreeValue.FALSE, hasBoolLabels, value, tValue, fValue);
					//must set the relationship based on another value
					else {
						String setString = relAnno.getSetVar();
						Variable setVar = getVariable(setString, instr, sum.getParameterNames());
						
						//split on the return value, IF we have true/false labels
						if (setVar == instr.getTarget()) {
							value.setRelationship(rel, ThreeValue.UNKNOWN);
							if (hasBoolLabels) {
								tValue.setRelationship(rel, ThreeValue.TRUE);
								fValue.setRelationship(rel, ThreeValue.FALSE);
							}
						}
						//use the boolean constants analysis to get the value
						else {
							ThreeValue boolInfo = mainAnalysis.getBoolInfo(setVar, instr.getNode());
							setAllLEs(rel, boolInfo, hasBoolLabels, value, tValue, fValue);
						}
					}
				}
			}
		}
		
		if (hasBoolLabels) {
			LabeledResult<RelationshipContext> result = new LabeledResult<RelationshipContext>(value);
			result.put(BooleanLabel.getBooleanLabel(true), tValue);
			result.put(BooleanLabel.getBooleanLabel(false), fValue);
			return result;
		}
		else
			return LabeledSingleResult.createResult(value, labels);
	}
*/

	static final private void setAllLEs(Relationship rel, ThreeValue tv, boolean hasLabels, RelationshipContext defVal, RelationshipContext trueVal, RelationshipContext falseVal) {
		defVal.setRelationship(rel, tv);
		if (hasLabels) {
			trueVal.setRelationship(rel, tv);
			falseVal.setRelationship(rel, tv);			
		}
	}
		
	/**
	 * Get all the relationships we can create given the bound variables. This means we get a relationship for
	 * every proper combination. So:
	 * v1 : a, b
	 * v2: c
	 * v3: a, d
	 * gets us (a, c, a), (b, c, a), (a, c, d), (b, c, d).
	 * Eventually, we should also add typing here too.
	 * Also, this does not currently handle wildcards.
	 * @param rels relationship set (output parameter)
	 * @param prevLabels For recursive purposes, send in an empty list
	 * @param vars The bound variables
	 * @param ndx For recursive purposes, send in 0
	 * @param name The name of the relationship
	 * @param instr The method call that started this damn thing.
	 */
/*	private void createAllRelationships(Set<Relationship> rels, List<ObjectLabel> prevLabels, Variable[] vars, int ndx, String name, MethodCallInstruction instr) {
		if (ndx == vars.length) {
			ObjectLabel[] arr = new ObjectLabel[prevLabels.size()];
			int lndx = 0;
			for (ObjectLabel label : prevLabels) {
				arr[lndx] = label;
				lndx++;
			}
			rels.add(new Relationship(name, arr));
		}
		else {
			Set<ObjectLabel> newLabels;

			if (vars[ndx] != null)
				newLabels = mainAnalysis.getLabels(vars[ndx], instr.getNode());
			else 
				newLabels = mainAnalysis.getLabels(name, vars.length, ndx, instr.getNode());
				
			for (ObjectLabel label : newLabels) {
				prevLabels.add(label);
				createAllRelationships(rels, prevLabels, vars, ndx + 1, name, instr);
				prevLabels.remove(label);
			}
		}
	}
*/
	/**
	 * Determine whether there are relevant aliases for the bound variables,
	 * where a relevant alias is one which will affect whether the relationship
	 * can be set to a definite value. In particular, wildcard aliases do not affect us since
	 * we apply the relationship to all of the, but other aliases do because we do not know for
	 * sure which object is a part of the relationship.
	 * @param boundVars The list of variables to check
	 * @param node The ast node where we are checking at.
	 * @return true if the variables are all distinct or a wildcard, false if we aren't sure what it is.
	 */
	private boolean isDistinctOrWild(Variable[] boundVars, ASTNode node) {
		for (Variable var : boundVars) {
			if (var == null)
				continue;
			if (mainAnalysis.getLabels(var, node).size() > 1)
				return false;
		}
		return true;
	}

	/**
	 * Get the list of variables for this relationship annotation, given the method call to retrieve variables
	 * from and the ordered list of parameter names.
	 * All variables in anno will match up to a parameter, or they will be the receiver or target of the
	 * method invocation.
	 * It is possible that the relationship annotation uses a wildcard, in which case, there will be null
	 * in this slot
	 * @param instr The method call which has the arguments
	 * @param anno The relationship annotation we want to bind up
	 * @param paramNames The ordered parameter names for instr
	 * @return The variables, in the order they appear in anno.
	 */
/*	private Variable[] getVariables(MethodCallInstruction instr, RelationshipAnnotation anno, String[] paramNames) {
		Variable[] vars = new Variable[anno.getParams().length];
		int ndx = 0;
		
		for (String param : anno.getParams()) {
			vars[ndx] = getVariable(param, instr, paramNames);
			ndx++;
		}
		return vars;
	}
*/	

	/**
	 * Get the Variable for a parameter name
	 * @param param The parameter name we are searching for
	 * @param instr The method call which has the arguments
	 * @param params The list of possible parameter names, in the correct order.
	 * @return The variable which corresponds to param
	 */
/*	private Variable getVariable(String param, MethodCallInstruction instr, String[] paramNames) {
		if (param.equals(RelationshipAnnotation.RETURN))
			return instr.getTarget();
		else if (param.equals(RelationshipAnnotation.THIS))
			return instr.getReceiverOperand();
		else if (param.equals(RelationshipAnnotation.ANY))
			return null;
		else {	
			assert(paramNames.length == instr.getArgOperands().size());
			for (int ndx = 0; ndx < paramNames.length; ndx++) {
				if (param.equals(paramNames[ndx]))
					return ((List<Variable>)instr.getArgOperands()).get(ndx);
			}
			return null;
		}
	}
*/


}
