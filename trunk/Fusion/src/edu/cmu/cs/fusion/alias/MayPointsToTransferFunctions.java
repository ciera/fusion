package edu.cmu.cs.fusion.alias;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import edu.cmu.cs.crystal.analysis.alias.DefaultObjectLabel;
import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.analysis.metrics.LoopCounter;
import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.flow.LabeledSingleResult;
import edu.cmu.cs.crystal.tac.AbstractTACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.model.ArrayInitInstruction;
import edu.cmu.cs.crystal.tac.model.AssignmentInstruction;
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
import edu.cmu.cs.crystal.tac.model.UnaryOperation;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.DeclarativeRetriever;
import edu.cmu.cs.fusion.xml.NamedTypeBinding;

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
	
	private MayPointsToAliasContext putFresh(AssignmentInstruction instr, MayPointsToAliasContext value, boolean onlySingleFresh) {
		boolean isInLoop = loopCounter.isInLoop(instr.getNode());
		MayPointsToAliasContext newValue = value.clone();
		
		newValue.resetPointsTo(instr.getTarget());
		
		if (isInLoop && loopedVariables.get(instr.getTarget()) != null) {
			Set<ObjectLabel> aliases = loopedVariables.get(instr.getTarget());
			newValue.addPointsTo(instr.getTarget(), aliases);
		}
		else {
			if (instr.getTarget().resolveType().getQualifiedName().equals("void")) {
				newValue.addPointsTo(instr.getTarget(), voidLabel);
			}
			else {
				if (!onlySingleFresh)
					newValue.addPointsToAnySubtype(instr.getTarget(), instr.getTarget().resolveType());

				ObjectLabel freshLabel = new DefaultObjectLabel(instr.getTarget().resolveType(), isInLoop);
				newValue.addPointsTo(instr.getTarget(), freshLabel);
				newValue.addLabel(freshLabel);
			}
			
			if (isInLoop) {
				Set<ObjectLabel> storeAliases = newValue.getAliases(instr.getTarget());
				loopedVariables.put(instr.getTarget(), storeAliases);
			}
		}
		return newValue;
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
		return LabeledSingleResult.createResult(putFresh(instr, value, true), labels);
	}

	@Override
	public IResult<MayPointsToAliasContext> transfer(BinaryOperation instr,
			List<ILabel> labels, MayPointsToAliasContext value) {
		return LabeledSingleResult.createResult(putFresh(instr, value, true), labels);
	}

	@Override
	public IResult<MayPointsToAliasContext> transfer(CastInstruction instr,
			List<ILabel> labels, MayPointsToAliasContext value) {
		MayPointsToAliasContext newValue = value.clone();
		newValue.resetPointsTo(instr.getTarget());
		
		String vType = instr.getTarget().resolveType().getQualifiedName();
		
		for (ObjectLabel label : value.getAliases(instr.getOperand())) {
			String labType = label.getType().getQualifiedName();
			
			if (types.isSubtypeCompatible(labType, vType)) { //an upcast
				newValue.addPointsTo(instr.getTarget(), label);
			}
			else if (types.isSubtypeCompatible(vType, labType)) { //a downcast
				newValue.addPointsTo(instr.getTarget(), label);
			}
		}
		return LabeledSingleResult.createResult(newValue, labels);
	}

	@Override
	public IResult<MayPointsToAliasContext> transfer(CopyInstruction instr,
			List<ILabel> labels, MayPointsToAliasContext value) {
		MayPointsToAliasContext newValue = value.clone();
		newValue.resetPointsTo(instr.getTarget());
		newValue.addPointsTo(instr.getTarget(), newValue.getAliases(instr.getOperand()));
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
		return LabeledSingleResult.createResult(putFresh(instr, value, true), labels);
	}

	@Override
	public IResult<MayPointsToAliasContext> transfer(
			LoadArrayInstruction instr, List<ILabel> labels,
			MayPointsToAliasContext value) {
		return LabeledSingleResult.createResult(putFresh(instr, value, false), labels);
	}

	@Override
	public IResult<MayPointsToAliasContext> transfer(
			LoadFieldInstruction instr, List<ILabel> labels,
			MayPointsToAliasContext value) {
		return LabeledSingleResult.createResult(putFresh(instr, value, false), labels);
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
		return LabeledSingleResult.createResult(putFresh(instr, value, false), labels);
	}

	@Override
	public IResult<MayPointsToAliasContext> transfer(NewArrayInstruction instr,
			List<ILabel> labels, MayPointsToAliasContext value) {
		return LabeledSingleResult.createResult(putFresh(instr, value, true), labels);
	}

	@Override
	public IResult<MayPointsToAliasContext> transfer(
			NewObjectInstruction instr, List<ILabel> labels,
			MayPointsToAliasContext value) {
		return LabeledSingleResult.createResult(putFresh(instr, value, true), labels);
	}

	@Override
	public IResult<MayPointsToAliasContext> transfer(UnaryOperation instr,
			List<ILabel> labels, MayPointsToAliasContext value) {
		return LabeledSingleResult.createResult(putFresh(instr, value, true), labels);
	}

}
