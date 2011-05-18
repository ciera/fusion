package edu.cmu.cs.fusion.relationship;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import edu.cmu.cs.crystal.flow.BooleanLabel;
import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.flow.LabeledResult;
import edu.cmu.cs.crystal.flow.LabeledSingleResult;
import edu.cmu.cs.crystal.flow.NormalLabel;
import edu.cmu.cs.crystal.tac.AbstractTACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.ITACAnalysisContext;
import edu.cmu.cs.crystal.tac.model.ArrayInitInstruction;
import edu.cmu.cs.crystal.tac.model.BinaryOperation;
import edu.cmu.cs.crystal.tac.model.CastInstruction;
import edu.cmu.cs.crystal.tac.model.ConstructorCallInstruction;
import edu.cmu.cs.crystal.tac.model.CopyInstruction;
import edu.cmu.cs.crystal.tac.model.DotClassInstruction;
import edu.cmu.cs.crystal.tac.model.EnhancedForConditionInstruction;
import edu.cmu.cs.crystal.tac.model.InstanceofInstruction;
import edu.cmu.cs.crystal.tac.model.LoadArrayInstruction;
import edu.cmu.cs.crystal.tac.model.LoadFieldInstruction;
import edu.cmu.cs.crystal.tac.model.LoadLiteralInstruction;
import edu.cmu.cs.crystal.tac.model.MethodCallInstruction;
import edu.cmu.cs.crystal.tac.model.NewArrayInstruction;
import edu.cmu.cs.crystal.tac.model.NewObjectInstruction;
import edu.cmu.cs.crystal.tac.model.ReturnInstruction;
import edu.cmu.cs.crystal.tac.model.SourceVariableDeclaration;
import edu.cmu.cs.crystal.tac.model.SourceVariableReadInstruction;
import edu.cmu.cs.crystal.tac.model.StoreArrayInstruction;
import edu.cmu.cs.crystal.tac.model.StoreFieldInstruction;
import edu.cmu.cs.crystal.tac.model.UnaryOperation;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.BooleanConstantWrapper;
import edu.cmu.cs.fusion.BooleanContext;
import edu.cmu.cs.fusion.DeclarativeRetriever;
import edu.cmu.cs.fusion.FusionAnalysis;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.FusionException;
import edu.cmu.cs.fusion.FusionLattice;
import edu.cmu.cs.fusion.FusionLatticeOps;
import edu.cmu.cs.fusion.Method;
import edu.cmu.cs.fusion.alias.AliasContext;
import edu.cmu.cs.fusion.constraint.ConstraintEnvironment;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;


public class RelationshipTransferFunction<AC extends AliasContext> extends AbstractTACBranchSensitiveTransferFunction<FusionLattice<AC>> {

	private FusionAnalysis<AC> mainAnalysis;
	protected TypeHierarchy types;
	private InferenceEnvironment infers;
	private ConstraintChecker checker;
	private DeclarativeRetriever retriever;
	private AbstractTACBranchSensitiveTransferFunction<AC> aliasTF;
	private ILatticeOperations<AC> aliasOps;
	private ConstraintEnvironment constraints;
	
	/**
	 * Do not use, only for testing purposes.
	 * @throws FusionException
	 */
	public RelationshipTransferFunction(FusionAnalysis relAnalysis, AbstractTACBranchSensitiveTransferFunction<AC> aliasTF) throws FusionException {
		mainAnalysis = relAnalysis;
		this.aliasTF = aliasTF;
		checker = new ConstraintChecker(null, null, relAnalysis.getVariant(), null);
	}
	
	public RelationshipTransferFunction(FusionAnalysis relAnalysis, ConstraintEnvironment constraints, InferenceEnvironment inf, TypeHierarchy types, DeclarativeRetriever retriever, AbstractTACBranchSensitiveTransferFunction<AC> aliasTF, ILatticeOperations<AC> aliasOps) throws FusionException {
		mainAnalysis = relAnalysis;
		this.infers = inf;
		this.types = types;
		this.retriever = retriever;
		this.aliasTF = aliasTF;
		this.aliasOps = aliasOps;
		this.constraints = constraints;
	}
	
	@Override
	public void setAnalysisContext(ITACAnalysisContext analysisContext) {
		super.setAnalysisContext(analysisContext);
		aliasTF.setAnalysisContext(analysisContext);
	}
	
	public ConstraintChecker getConstraintChecker() {
		return checker;
	}

	/**
	 * Get the entry value based on the starting context received from the XML files.
	 * Also use any callbacks, so go ahead and run the constraint checker here with an entry instruction.
	 */
	public FusionLattice<AC> createEntryValue(MethodDeclaration method) {
		checker = new ConstraintChecker(constraints, types, mainAnalysis.getVariant(), createTACMethod(method));
		
		AC aliases = aliasTF.createEntryValue(method);
		RelationshipContext startingContext = retriever.getStartContext();	
		
		if (method.getBody() != null) {
			Variable thisVar = this.getAnalysisContext().getThisVariable();
			List<Variable> params = new ArrayList<Variable>();
			Iterator<SingleVariableDeclaration> itr = method.parameters().iterator();
			while (itr.hasNext()) {
				params.add(getAnalysisContext().getSourceVariable(itr.next().resolveBinding()));
			}		
			EntryInstruction entry = new EntryInstruction(thisVar, params, method.resolveBinding());
			
			BooleanContext bContext = new BooleanConstantWrapper(method.getBody(), mainAnalysis.getBooleanAnalysis(), aliases);
			FusionEnvironment<AC> env = new FusionEnvironment<AC>(aliases, startingContext, bContext, types, infers, mainAnalysis.getVariant());
			
			return convertToLattice(checker.runGenericTransfer(env, entry), aliases);
		}
		else {
			return new FusionLattice<AC>(startingContext, aliases, aliases);
		}
	}
	
	private Method createTACMethod(MethodDeclaration decl) {
		Variable thisVar = getAnalysisContext().getThisVariable();
		Variable[] params = new Variable[decl.parameters().size()];
		Iterator<SingleVariableDeclaration> itr = decl.parameters().iterator();
		int ndx = 0;
		while (itr.hasNext()) {
			params[ndx] = getAnalysisContext().getSourceVariable(itr.next().resolveBinding());
			ndx++;
		}
		return new Method(params, thisVar, decl.resolveBinding());
	}


	public ILatticeOperations<FusionLattice<AC>> getLatticeOperations() {
		return new FusionLatticeOps<AC>(aliasOps);
	}
	

	@Override
	public IResult<FusionLattice<AC>> transfer(MethodCallInstruction instr,
			List<ILabel> labels, FusionLattice<AC> value) {		
		//run twice: once assuming return is false, and again assuming return is true.
		AC aBeforeContext = value.getAliasContext();
		RelationshipContext relsContext = value.getRelContext();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));
		
		
		if (labels.contains(BooleanLabel.getBooleanLabel(true)) && labels.contains(BooleanLabel.getBooleanLabel(false))) {
			ILabel trueLabel = BooleanLabel.getBooleanLabel(true);
			ILabel falseLabel = BooleanLabel.getBooleanLabel(false);
			
			//true branch
			AC aAfterContextTrue = aliasResults.get(trueLabel);
			BooleanContext tBContext = new BooleanConstantWrapper(instr, mainAnalysis.getBooleanAnalysis(), aBeforeContext, aAfterContextTrue, true);
			FusionEnvironment<AC> tEnv = new FusionEnvironment<AC>(aAfterContextTrue, relsContext, tBContext, types, infers, mainAnalysis.getVariant());
			FusionLattice<AC> tPair = convertToLattice(checker.runGenericTransfer(tEnv, instr), aAfterContextTrue);
			
			//false branch
			AC aAfterContextFalse = aliasResults.get(falseLabel);
			BooleanContext fBContext = new BooleanConstantWrapper(instr, mainAnalysis.getBooleanAnalysis(), aBeforeContext, aAfterContextFalse, false);
			FusionEnvironment<AC> fEnv = new FusionEnvironment<AC>(aAfterContextFalse, relsContext, fBContext, types, infers, mainAnalysis.getVariant());
			FusionLattice<AC> fPair = convertToLattice(checker.runGenericTransfer(fEnv, instr), aAfterContextFalse);

			
			FusionLattice<AC> defPair = new FusionLattice<AC>(new RelationshipContext(false), aliasResults.get(NormalLabel.getNormalLabel()), aliasResults.get(NormalLabel.getNormalLabel()));
		
			LabeledResult<FusionLattice<AC>> result = LabeledResult.createResult(labels, defPair);
			result.put(trueLabel, tPair);
			result.put(falseLabel, fPair);
			return result;
		}
		else {
			AC aAfterContext = aliasResults.get(NormalLabel.getNormalLabel());
			BooleanContext bContext = new BooleanConstantWrapper(instr, mainAnalysis.getBooleanAnalysis(), aAfterContext);
			FusionEnvironment<AC> env = new FusionEnvironment<AC>(aAfterContext, relsContext, bContext, types, infers, mainAnalysis.getVariant());
			FusionLattice<AC> pair = convertToLattice(checker.runGenericTransfer(env, instr), aAfterContext);
			
			return LabeledSingleResult.createResult(pair, labels);
		}
	}


	private FusionLattice<AC> convertToLattice(Pair<AC, RelationshipContext> results, AC triggerAliases) {
		return new FusionLattice<AC>(results.snd(), triggerAliases, results.fst());
	}

	@Override
	public IResult<FusionLattice<AC>> transfer(NewObjectInstruction instr,
			List<ILabel> labels, FusionLattice<AC> value) {
		
		AC aBeforeContext = value.getAliasContext();
		RelationshipContext relsContext = value.getRelContext();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));
		AC aAfterContext = aliasResults.get(NormalLabel.getNormalLabel());

		BooleanContext bContext = new BooleanConstantWrapper(instr, mainAnalysis.getBooleanAnalysis(), aAfterContext);
		FusionEnvironment<AC> env = new FusionEnvironment<AC>(aAfterContext, relsContext, bContext, types, infers, mainAnalysis.getVariant());
		FusionLattice<AC> pair = convertToLattice(checker.runGenericTransfer(env, instr), aAfterContext);
	
		return LabeledSingleResult.createResult(pair, labels);
	}

	private IResult<FusionLattice<AC>> mapOverResults(
			IResult<AC> aliasResults, List<ILabel> labels,
			RelationshipContext relsContext) {
		FusionLattice<AC> lattice = new FusionLattice<AC>(relsContext, aliasResults.get(null), aliasResults.get(null));
		LabeledResult<FusionLattice<AC>> result = LabeledResult.createResult(labels, lattice);

		for (ILabel label : labels)
			result.put(label, new FusionLattice<AC>(relsContext, aliasResults.get(label), aliasResults.get(null)));
		
		return result;
	}

	@Override
	public IResult<FusionLattice<AC>> transfer(
			ArrayInitInstruction instr, List<ILabel> labels,
			FusionLattice<AC> value) {
		AC aBeforeContext = value.getAliasContext();
		RelationshipContext relsContext = value.getRelContext();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}


	@Override
	public IResult<FusionLattice<AC>> transfer(
			BinaryOperation instr, List<ILabel> labels,
			FusionLattice<AC> value) {
		AC aBeforeContext = value.getAliasContext();
		RelationshipContext relsContext = value.getRelContext();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<FusionLattice<AC>> transfer(
			CastInstruction instr, List<ILabel> labels,
			FusionLattice<AC> value) {
		AC aBeforeContext = value.getAliasContext();
		RelationshipContext relsContext = value.getRelContext();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<FusionLattice<AC>> transfer(
			ConstructorCallInstruction instr, List<ILabel> labels,
			FusionLattice<AC> value) {
		AC aBeforeContext = value.getAliasContext();
		RelationshipContext relsContext = value.getRelContext();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<FusionLattice<AC>> transfer(
			CopyInstruction instr, List<ILabel> labels,
			FusionLattice<AC> value) {
		AC aBeforeContext = value.getAliasContext();
		RelationshipContext relsContext = value.getRelContext();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<FusionLattice<AC>> transfer(
			DotClassInstruction instr, List<ILabel> labels,
			FusionLattice<AC> value) {
		AC aBeforeContext = value.getAliasContext();
		RelationshipContext relsContext = value.getRelContext();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<FusionLattice<AC>> transfer(
			EnhancedForConditionInstruction instr, List<ILabel> labels,
			FusionLattice<AC> value) {
		AC aBeforeContext = value.getAliasContext();
		RelationshipContext relsContext = value.getRelContext();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<FusionLattice<AC>> transfer(
			InstanceofInstruction instr, List<ILabel> labels,
			FusionLattice<AC> value) {
		AC aBeforeContext = value.getAliasContext();
		RelationshipContext relsContext = value.getRelContext();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<FusionLattice<AC>> transfer(
			LoadArrayInstruction instr, List<ILabel> labels,
			FusionLattice<AC> value) {
		AC aBeforeContext = value.getAliasContext();
		RelationshipContext relsContext = value.getRelContext();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<FusionLattice<AC>> transfer(
			LoadFieldInstruction instr, List<ILabel> labels,
			FusionLattice<AC> value) {
		AC aBeforeContext = value.getAliasContext();
		RelationshipContext relsContext = value.getRelContext();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<FusionLattice<AC>> transfer(
			LoadLiteralInstruction instr, List<ILabel> labels,
			FusionLattice<AC> value) {
		AC aBeforeContext = value.getAliasContext();
		RelationshipContext relsContext = value.getRelContext();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<FusionLattice<AC>> transfer(
			NewArrayInstruction instr, List<ILabel> labels,
			FusionLattice<AC> value) {
		AC aBeforeContext = value.getAliasContext();
		RelationshipContext relsContext = value.getRelContext();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<FusionLattice<AC>> transfer(
			ReturnInstruction instr, List<ILabel> labels,
			FusionLattice<AC> value) {
		AC aBeforeContext = value.getAliasContext();
		RelationshipContext relsContext = value.getRelContext();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<FusionLattice<AC>> transfer(
			SourceVariableDeclaration instr, List<ILabel> labels,
			FusionLattice<AC> value) {
		AC aBeforeContext = value.getAliasContext();
		RelationshipContext relsContext = value.getRelContext();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<FusionLattice<AC>> transfer(
			SourceVariableReadInstruction instr, List<ILabel> labels,
			FusionLattice<AC> value) {
		AC aBeforeContext = value.getAliasContext();
		RelationshipContext relsContext = value.getRelContext();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<FusionLattice<AC>> transfer(
			StoreArrayInstruction instr, List<ILabel> labels,
			FusionLattice<AC> value) {
		AC aBeforeContext = value.getAliasContext();
		RelationshipContext relsContext = value.getRelContext();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<FusionLattice<AC>> transfer(
			StoreFieldInstruction instr, List<ILabel> labels,
			FusionLattice<AC> value) {
		AC aBeforeContext = value.getAliasContext();
		RelationshipContext relsContext = value.getRelContext();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<FusionLattice<AC>> transfer(
			UnaryOperation instr, List<ILabel> labels,
			FusionLattice<AC> value) {
		AC aBeforeContext = value.getAliasContext();
		RelationshipContext relsContext = value.getRelContext();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}
}
