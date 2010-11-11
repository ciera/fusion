package edu.cmu.cs.fusion.alias;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import edu.cmu.cs.crystal.analysis.metrics.LoopCounter;
import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.flow.LabeledSingleResult;
import edu.cmu.cs.crystal.tac.AbstractTACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.model.ArrayInitInstruction;
import edu.cmu.cs.crystal.tac.model.BinaryOperation;
import edu.cmu.cs.crystal.tac.model.CastInstruction;
import edu.cmu.cs.crystal.tac.model.CopyInstruction;
import edu.cmu.cs.crystal.tac.model.DotClassInstruction;
import edu.cmu.cs.crystal.tac.model.InstanceofInstruction;
import edu.cmu.cs.crystal.tac.model.LoadArrayInstruction;
import edu.cmu.cs.crystal.tac.model.LoadFieldInstruction;
import edu.cmu.cs.crystal.tac.model.LoadInstruction;
import edu.cmu.cs.crystal.tac.model.LoadLiteralInstruction;
import edu.cmu.cs.crystal.tac.model.MethodCallInstruction;
import edu.cmu.cs.crystal.tac.model.NewArrayInstruction;
import edu.cmu.cs.crystal.tac.model.NewObjectInstruction;
import edu.cmu.cs.crystal.tac.model.SourceVariableDeclaration;
import edu.cmu.cs.crystal.tac.model.UnaryOperation;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.DeclarativeRetriever;
import edu.cmu.cs.fusion.xml.NamedTypeBinding;

/**
 * The transfer functions for a simple aliasing analysis.
 * @author ciera
 *
 *
 * Note: This class has an invariant that all variables do have at least one ObjectLabel that they can point to. This label
 * might be the null label representation, but it always exists. In particular, any call like value.getAliases(instr.getOperand())
 * is ALWAYS non-null and has at least one element. I have added asserts accordingly. If this is ever null, something went
 * wrong much earlier in the transfer functions and should be investigated.
 */
public class MayPointsToTransferFunctions extends AbstractTACBranchSensitiveTransferFunction<MayPointsToAliasContext> {
	private ILatticeOperations<MayPointsToAliasContext> ops;
	private DeclarativeRetriever retriever;
	private LoopCounter loopCounter;
	protected Map<Object, ObjectLabel> knownLiterals;
	private TypeHierarchy types;
	private ObjectLabel voidLabel = new DefaultObjectLabel(new NamedTypeBinding("void"), false);
	
	//This caches the result of the aliasing when going through a loop, so
	//we don't create a new variable a second time.
	//really then, should only store the cache when we are, in fact, in a loop.
	private Map<Variable, Set<ObjectLabel>> loopedVariables;

	public MayPointsToTransferFunctions(DeclarativeRetriever retriever, TypeHierarchy types) {
		ops = new MayPointsToLatticeOps(types);
		loopCounter = new LoopCounter();
		this.retriever = retriever;
		this.types = types;
		knownLiterals =  new HashMap<Object, ObjectLabel>();
		loopedVariables = new HashMap<Variable, Set<ObjectLabel>>();
		knownLiterals.put(null, new LiteralLabel(null, new NamedTypeBinding("java.lang.Object")));
	}
	
	public MayPointsToAliasContext createEntryValue(MethodDeclaration method) {
		MayPointsToAliasContext entry = ops.bottom();
		Variable thisVar = this.getAnalysisContext().getThisVariable();
		
		if (thisVar != null) {
			Set<ObjectLabel> thisAliases = retriever.getStartingAliases(thisVar);
			
			if (thisAliases.isEmpty()) {
				ObjectLabel fresh = new DefaultObjectLabel(thisVar.resolveType(), false);
				entry.addPointsTo(thisVar, fresh);
				entry.addLabel(fresh);
			}
			else
				entry.addPointsTo(thisVar, thisAliases);
		}
		
		entry.addLabel(voidLabel);
		entry.addLabels(retriever.getAllLabels());
		
		//first, create fresh variables for all the parameters
		Iterator itr = method.parameters().iterator();
		while (itr.hasNext()) {
			SingleVariableDeclaration param = (SingleVariableDeclaration) itr.next();
			Variable paramVar = getAnalysisContext().getSourceVariable(param.resolveBinding());
			
			ObjectLabel fresh = new DefaultObjectLabel(param.getType().resolveBinding(), false);
			entry.addPointsTo(paramVar, fresh);
			entry.addLabel(fresh);
		}

		//once we have all starting variables, figure out who might point to who
		itr = method.parameters().iterator();
		while (itr.hasNext()) {
			SingleVariableDeclaration param = (SingleVariableDeclaration) itr.next();
			Variable paramVar = getAnalysisContext().getSourceVariable(param.resolveBinding());
			entry.addPointsToAnySubtype(paramVar, param.getType().resolveBinding());
		}
		
		return entry;
	}

	public ILatticeOperations<MayPointsToAliasContext> getLatticeOperations() {
		return ops;
	}
	
	private MayPointsToAliasContext putFresh(ASTNode node, Variable var, MayPointsToAliasContext value, boolean onlySingleFresh) {
		boolean isInLoop = loopCounter.isInLoop(node);
		MayPointsToAliasContext newValue = value.clone();
		
		newValue.resetPointsTo(var);
		
		if (isInLoop && loopedVariables.get(var) != null) {
			Set<ObjectLabel> aliases = loopedVariables.get(var);
			newValue.addPointsTo(var, aliases);
		}
		else {
			if (var.resolveType().getQualifiedName().equals("void")) {
				newValue.addPointsTo(var, voidLabel);
			}
			else {
				if (!onlySingleFresh)
					newValue.addPointsToAnySubtype(var, var.resolveType());

				ObjectLabel freshLabel = new DefaultObjectLabel(var.resolveType(), isInLoop);
				newValue.addPointsTo(var, freshLabel);
				newValue.addLabel(freshLabel);
			}
			
			if (isInLoop) {
				Set<ObjectLabel> storeAliases = newValue.getAliases(var);
				loopedVariables.put(var, storeAliases);
			}
		}
		return newValue;
	}


	
	
	
	@Override
	public IResult<MayPointsToAliasContext> transfer(
			SourceVariableDeclaration instr, List<ILabel> labels,
			MayPointsToAliasContext value) {
		if (instr.isCaughtVariable())
			return LabeledSingleResult.createResult(putFresh(instr.getNode(), instr.getDeclaredVariable(), value, false), labels);
		else
			return super.transfer(instr, labels, value);
	}

	/**
	 * Handles literal labels. This has no need to track looping since literal labels
	 * are always the same regardless of whether they are in a loop.
	 */
	private MayPointsToAliasContext putLiteral(LoadInstruction instr, Object literal, MayPointsToAliasContext value) {
		ObjectLabel label = knownLiterals.get(literal);
		MayPointsToAliasContext newValue = value.clone();
		
		if (label == null) { //we haven't done this literal yet
			label = new LiteralLabel(literal, instr.getTarget().resolveType());
			knownLiterals.put(literal, label);
			newValue.addLabel(label);
		}
		
		newValue.addPointsTo(instr.getTarget(), label);
		
		return newValue;
	}

	@Override
	public IResult<MayPointsToAliasContext> transfer(
			ArrayInitInstruction instr, List<ILabel> labels,
			MayPointsToAliasContext value) {
		return LabeledSingleResult.createResult(putFresh(instr.getNode(), instr.getTarget(), value, true), labels);
	}

	@Override
	public IResult<MayPointsToAliasContext> transfer(BinaryOperation instr,
			List<ILabel> labels, MayPointsToAliasContext value) {
		return LabeledSingleResult.createResult(putFresh(instr.getNode(), instr.getTarget(), value, true), labels);
	}

	@Override
	public IResult<MayPointsToAliasContext> transfer(CastInstruction instr,
			List<ILabel> labels, MayPointsToAliasContext value) {
		if (instr.getTarget().resolveType().isPrimitive())
			return LabeledSingleResult.createResult(putFresh(instr.getNode(), instr.getTarget(), value, true), labels);

		MayPointsToAliasContext newValue = value.clone();
		newValue.resetPointsTo(instr.getTarget());
		
		String vType = instr.getTarget().resolveType().getQualifiedName();
		
		assert(value.getAliases(instr.getOperand()) != null);
		
		for (ObjectLabel label : value.getAliases(instr.getOperand())) {
			String labType = label.getType().getQualifiedName();		
			if (types.existsCommonSubtype(labType, vType)) { //there exists a way to make this cast ok
				newValue.addPointsTo(instr.getTarget(), label);
			}
		}
		return LabeledSingleResult.createResult(newValue, labels);
	}

	@Override
	public IResult<MayPointsToAliasContext> transfer(CopyInstruction instr,
			List<ILabel> labels, MayPointsToAliasContext value) {
		if (instr.getTarget().resolveType().isPrimitive())
			return LabeledSingleResult.createResult(putFresh(instr.getNode(), instr.getTarget(), value, true), labels);
		
		MayPointsToAliasContext newValue = value.clone();
		newValue.resetPointsTo(instr.getTarget());
		Set<ObjectLabel> aliases = newValue.getAliases(instr.getOperand());
		newValue.addPointsTo(instr.getTarget(), aliases);
		return LabeledSingleResult.createResult(newValue, labels);
	}

	@Override
	public IResult<MayPointsToAliasContext> transfer(DotClassInstruction instr,
			List<ILabel> labels, MayPointsToAliasContext value) {
		return LabeledSingleResult.createResult(putLiteral(instr, instr.getTypeNode().resolveBinding().getQualifiedName(), value), labels);
	}

	@Override
	public IResult<MayPointsToAliasContext> transfer(
			InstanceofInstruction instr, List<ILabel> labels,
			MayPointsToAliasContext value) {
		return LabeledSingleResult.createResult(putFresh(instr.getNode(), instr.getTarget(), value, true), labels);
	}

	@Override
	public IResult<MayPointsToAliasContext> transfer(
			LoadArrayInstruction instr, List<ILabel> labels,
			MayPointsToAliasContext value) {
		if (instr.getTarget().resolveType().isPrimitive())
			return LabeledSingleResult.createResult(putFresh(instr.getNode(), instr.getTarget(), value, true), labels);

		return LabeledSingleResult.createResult(putFresh(instr.getNode(), instr.getTarget(), value, false), labels);
	}

	@Override
	public IResult<MayPointsToAliasContext> transfer(
			LoadFieldInstruction instr, List<ILabel> labels,
			MayPointsToAliasContext value) {
		if (instr.getTarget().resolveType().isPrimitive())
			return LabeledSingleResult.createResult(putFresh(instr.getNode(), instr.getTarget(), value, true), labels);

		return LabeledSingleResult.createResult(putFresh(instr.getNode(), instr.getTarget(), value, false), labels);
	}

	@Override
	public IResult<MayPointsToAliasContext> transfer(
			LoadLiteralInstruction instr, List<ILabel> labels,
			MayPointsToAliasContext value) {		
		return LabeledSingleResult.createResult(putLiteral(instr, instr.getLiteral(), value), labels);
	}

	@Override
	public IResult<MayPointsToAliasContext> transfer(
			MethodCallInstruction instr, List<ILabel> labels,
			MayPointsToAliasContext value) {
		if (instr.getTarget().resolveType().isPrimitive())
			return LabeledSingleResult.createResult(putFresh(instr.getNode(), instr.getTarget(), value, true), labels);

		return LabeledSingleResult.createResult(putFresh(instr.getNode(), instr.getTarget(), value, false), labels);
	}

	@Override
	public IResult<MayPointsToAliasContext> transfer(NewArrayInstruction instr,
			List<ILabel> labels, MayPointsToAliasContext value) {
		return LabeledSingleResult.createResult(putFresh(instr.getNode(), instr.getTarget(), value, true), labels);
	}

	@Override
	public IResult<MayPointsToAliasContext> transfer(
			NewObjectInstruction instr, List<ILabel> labels,
			MayPointsToAliasContext value) {
		return LabeledSingleResult.createResult(putFresh(instr.getNode(), instr.getTarget(), value, true), labels);
	}

	@Override
	public IResult<MayPointsToAliasContext> transfer(UnaryOperation instr,
			List<ILabel> labels, MayPointsToAliasContext value) {
		return LabeledSingleResult.createResult(putFresh(instr.getNode(), instr.getTarget(), value, true), labels);
	}

}
