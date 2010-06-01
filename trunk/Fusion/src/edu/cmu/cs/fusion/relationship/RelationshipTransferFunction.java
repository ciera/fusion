package edu.cmu.cs.fusion.relationship;

import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

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
import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.BooleanConstantWrapper;
import edu.cmu.cs.fusion.BooleanContext;
import edu.cmu.cs.fusion.DeclarativeRetriever;
import edu.cmu.cs.fusion.FusionAnalysis;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.FusionException;
import edu.cmu.cs.fusion.PairLatticeOps;
import edu.cmu.cs.fusion.alias.MayPointsToAliasContext;
import edu.cmu.cs.fusion.alias.MayPointsToLatticeOps;
import edu.cmu.cs.fusion.alias.MayPointsToTransferFunctions;
import edu.cmu.cs.fusion.constraint.ConstraintEnvironment;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;


public class RelationshipTransferFunction extends AbstractTACBranchSensitiveTransferFunction<Pair<MayPointsToAliasContext, RelationshipContext>> {

	private FusionAnalysis mainAnalysis;
	protected TypeHierarchy types;
	private InferenceEnvironment infers;
	private ConstraintChecker checker;
	private DeclarativeRetriever retriever;
	private MayPointsToTransferFunctions aliasTF;
	private MayPointsToLatticeOps aliasOps;
	
	/**
	 * Do not use, only for testing purposes.
	 * @throws FusionException
	 */
	public RelationshipTransferFunction(FusionAnalysis relAnalysis) throws FusionException {
		mainAnalysis = relAnalysis;
		checker = new ConstraintChecker(null, null, relAnalysis.getVariant());
		this.aliasTF = new MayPointsToTransferFunctions(null, null);
	}
	
	public RelationshipTransferFunction(FusionAnalysis relAnalysis, ConstraintEnvironment constraints, InferenceEnvironment inf, TypeHierarchy types, DeclarativeRetriever retriever) throws FusionException {
		mainAnalysis = relAnalysis;
		this.infers = inf;
		this.types = types;
		checker = new ConstraintChecker(constraints, types, relAnalysis.getVariant());
		this.retriever = retriever;
		this.aliasTF = new MayPointsToTransferFunctions(retriever, types);
		this.aliasOps = new MayPointsToLatticeOps(types);
	}

	@Override
	public void setAnalysisContext(ITACAnalysisContext analysisContext) {
		super.setAnalysisContext(analysisContext);
		aliasTF.setAnalysisContext(analysisContext);
	}

	/**
	 * Get the entry value based on the starting context received from the XML files.
	 */
	public Pair<MayPointsToAliasContext, RelationshipContext> createEntryValue(MethodDeclaration method) {		
		MayPointsToAliasContext aliases = aliasTF.createEntryValue(method);
		RelationshipContext startingContext = retriever.getStartContext();		
		return new Pair<MayPointsToAliasContext, RelationshipContext>(aliases, startingContext);
	}

	public ILatticeOperations<Pair<MayPointsToAliasContext, RelationshipContext>> getLatticeOperations() {
		return new PairLatticeOps<MayPointsToAliasContext, RelationshipContext>(aliasOps, new RelationshipLatticeOperations());
	}
	

	@Override
	public IResult<Pair<MayPointsToAliasContext, RelationshipContext>> transfer(MethodCallInstruction instr,
			List<ILabel> labels, Pair<MayPointsToAliasContext, RelationshipContext> value) {		
		//run twice: once assuming return is false, and again assuming return is true.
		MayPointsToAliasContext aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<MayPointsToAliasContext> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));
		
		
		if (labels.contains(BooleanLabel.getBooleanLabel(true)) && labels.contains(BooleanLabel.getBooleanLabel(false))) {
			ILabel trueLabel = BooleanLabel.getBooleanLabel(true);
			ILabel falseLabel = BooleanLabel.getBooleanLabel(false);
			
			//true branch
			MayPointsToAliasContext aAfterContextTrue = aliasResults.get(trueLabel);
			BooleanContext tBContext = new BooleanConstantWrapper(instr, mainAnalysis.getBooleanAnalysis(), aBeforeContext, aAfterContextTrue, true);
			FusionEnvironment tEnv = new FusionEnvironment(aAfterContextTrue, relsContext, tBContext, types, infers);
			RelationshipContext tNewContext = checker.runGenericTransfer(tEnv, instr).fst();
			
			//false branch
			MayPointsToAliasContext aAfterContextFalse = aliasResults.get(falseLabel);
			BooleanContext fBContext = new BooleanConstantWrapper(instr, mainAnalysis.getBooleanAnalysis(), aBeforeContext, aAfterContextFalse, false);
			FusionEnvironment fEnv = new FusionEnvironment(aAfterContextFalse, relsContext, fBContext, types, infers);
			RelationshipContext fNewContext = checker.runGenericTransfer(fEnv, instr).fst();
			
			Pair<MayPointsToAliasContext, RelationshipContext> defPair = new Pair<MayPointsToAliasContext, RelationshipContext>(aliasResults.get(NormalLabel.getNormalLabel()), new RelationshipContext(false));
			Pair<MayPointsToAliasContext, RelationshipContext> tPair = new Pair<MayPointsToAliasContext, RelationshipContext>(aAfterContextTrue, tNewContext);
			Pair<MayPointsToAliasContext, RelationshipContext> fPair = new Pair<MayPointsToAliasContext, RelationshipContext>(aAfterContextFalse, fNewContext);
		
			LabeledResult<Pair<MayPointsToAliasContext, RelationshipContext>> result = LabeledResult.createResult(labels, defPair);
			result.put(trueLabel, tPair);
			result.put(falseLabel, fPair);
			return result;
		}
		else {
			MayPointsToAliasContext aAfterContext = aliasResults.get(NormalLabel.getNormalLabel());
			BooleanContext bContext = new BooleanConstantWrapper(instr, mainAnalysis.getBooleanAnalysis(), aAfterContext);
			FusionEnvironment env = new FusionEnvironment(aAfterContext, relsContext, bContext, types, infers);
			RelationshipContext newContext = checker.runGenericTransfer(env, instr).fst();
			Pair<MayPointsToAliasContext, RelationshipContext> pair = new Pair<MayPointsToAliasContext, RelationshipContext>(aAfterContext, newContext);

			return LabeledSingleResult.createResult(pair, labels);
		}
	}


	@Override
	public IResult<Pair<MayPointsToAliasContext, RelationshipContext>> transfer(NewObjectInstruction instr,
			List<ILabel> labels, Pair<MayPointsToAliasContext, RelationshipContext> value) {
		
		MayPointsToAliasContext aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<MayPointsToAliasContext> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));
		MayPointsToAliasContext aAfterContext = aliasResults.get(NormalLabel.getNormalLabel());

		BooleanContext bContext = new BooleanConstantWrapper(instr, mainAnalysis.getBooleanAnalysis(), aAfterContext);
		FusionEnvironment env = new FusionEnvironment(aAfterContext, relsContext, bContext, types, infers);
		RelationshipContext newContext = checker.runGenericTransfer(env, instr).fst();
	
		Pair<MayPointsToAliasContext, RelationshipContext> pair = new Pair<MayPointsToAliasContext, RelationshipContext>(aAfterContext, newContext);

		return LabeledSingleResult.createResult(pair, labels);
	}

	private IResult<Pair<MayPointsToAliasContext, RelationshipContext>> mapOverResults(
			IResult<MayPointsToAliasContext> aliasResults, List<ILabel> labels,
			RelationshipContext relsContext) {
		Pair<MayPointsToAliasContext, RelationshipContext> defPair = new Pair<MayPointsToAliasContext, RelationshipContext>(aliasResults.get(null), relsContext);
		LabeledResult<Pair<MayPointsToAliasContext, RelationshipContext>> result = LabeledResult.createResult(labels, defPair);

		for (ILabel label : labels)
			result.put(label, new Pair<MayPointsToAliasContext, RelationshipContext>(aliasResults.get(label), relsContext));
		
		return result;
	}

	@Override
	public IResult<Pair<MayPointsToAliasContext, RelationshipContext>> transfer(
			ArrayInitInstruction instr, List<ILabel> labels,
			Pair<MayPointsToAliasContext, RelationshipContext> value) {
		MayPointsToAliasContext aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<MayPointsToAliasContext> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}


	@Override
	public IResult<Pair<MayPointsToAliasContext, RelationshipContext>> transfer(
			BinaryOperation instr, List<ILabel> labels,
			Pair<MayPointsToAliasContext, RelationshipContext> value) {
		MayPointsToAliasContext aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<MayPointsToAliasContext> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<MayPointsToAliasContext, RelationshipContext>> transfer(
			CastInstruction instr, List<ILabel> labels,
			Pair<MayPointsToAliasContext, RelationshipContext> value) {
		MayPointsToAliasContext aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<MayPointsToAliasContext> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<MayPointsToAliasContext, RelationshipContext>> transfer(
			ConstructorCallInstruction instr, List<ILabel> labels,
			Pair<MayPointsToAliasContext, RelationshipContext> value) {
		MayPointsToAliasContext aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<MayPointsToAliasContext> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<MayPointsToAliasContext, RelationshipContext>> transfer(
			CopyInstruction instr, List<ILabel> labels,
			Pair<MayPointsToAliasContext, RelationshipContext> value) {
		MayPointsToAliasContext aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<MayPointsToAliasContext> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<MayPointsToAliasContext, RelationshipContext>> transfer(
			DotClassInstruction instr, List<ILabel> labels,
			Pair<MayPointsToAliasContext, RelationshipContext> value) {
		MayPointsToAliasContext aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<MayPointsToAliasContext> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<MayPointsToAliasContext, RelationshipContext>> transfer(
			EnhancedForConditionInstruction instr, List<ILabel> labels,
			Pair<MayPointsToAliasContext, RelationshipContext> value) {
		MayPointsToAliasContext aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<MayPointsToAliasContext> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<MayPointsToAliasContext, RelationshipContext>> transfer(
			InstanceofInstruction instr, List<ILabel> labels,
			Pair<MayPointsToAliasContext, RelationshipContext> value) {
		MayPointsToAliasContext aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<MayPointsToAliasContext> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<MayPointsToAliasContext, RelationshipContext>> transfer(
			LoadArrayInstruction instr, List<ILabel> labels,
			Pair<MayPointsToAliasContext, RelationshipContext> value) {
		MayPointsToAliasContext aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<MayPointsToAliasContext> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<MayPointsToAliasContext, RelationshipContext>> transfer(
			LoadFieldInstruction instr, List<ILabel> labels,
			Pair<MayPointsToAliasContext, RelationshipContext> value) {
		MayPointsToAliasContext aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<MayPointsToAliasContext> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<MayPointsToAliasContext, RelationshipContext>> transfer(
			LoadLiteralInstruction instr, List<ILabel> labels,
			Pair<MayPointsToAliasContext, RelationshipContext> value) {
		MayPointsToAliasContext aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<MayPointsToAliasContext> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<MayPointsToAliasContext, RelationshipContext>> transfer(
			NewArrayInstruction instr, List<ILabel> labels,
			Pair<MayPointsToAliasContext, RelationshipContext> value) {
		MayPointsToAliasContext aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<MayPointsToAliasContext> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<MayPointsToAliasContext, RelationshipContext>> transfer(
			ReturnInstruction instr, List<ILabel> labels,
			Pair<MayPointsToAliasContext, RelationshipContext> value) {
		MayPointsToAliasContext aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<MayPointsToAliasContext> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<MayPointsToAliasContext, RelationshipContext>> transfer(
			SourceVariableDeclaration instr, List<ILabel> labels,
			Pair<MayPointsToAliasContext, RelationshipContext> value) {
		MayPointsToAliasContext aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<MayPointsToAliasContext> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<MayPointsToAliasContext, RelationshipContext>> transfer(
			SourceVariableReadInstruction instr, List<ILabel> labels,
			Pair<MayPointsToAliasContext, RelationshipContext> value) {
		MayPointsToAliasContext aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<MayPointsToAliasContext> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<MayPointsToAliasContext, RelationshipContext>> transfer(
			StoreArrayInstruction instr, List<ILabel> labels,
			Pair<MayPointsToAliasContext, RelationshipContext> value) {
		MayPointsToAliasContext aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<MayPointsToAliasContext> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<MayPointsToAliasContext, RelationshipContext>> transfer(
			StoreFieldInstruction instr, List<ILabel> labels,
			Pair<MayPointsToAliasContext, RelationshipContext> value) {
		MayPointsToAliasContext aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<MayPointsToAliasContext> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<MayPointsToAliasContext, RelationshipContext>> transfer(
			UnaryOperation instr, List<ILabel> labels,
			Pair<MayPointsToAliasContext, RelationshipContext> value) {
		MayPointsToAliasContext aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<MayPointsToAliasContext> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}
}
