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
import edu.cmu.cs.fusion.alias.AliasContext;
import edu.cmu.cs.fusion.constraint.ConstraintEnvironment;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;


public class RelationshipTransferFunction<AC extends AliasContext> extends AbstractTACBranchSensitiveTransferFunction<Pair<AC, RelationshipContext>> {

	private FusionAnalysis mainAnalysis;
	protected TypeHierarchy types;
	private InferenceEnvironment infers;
	private ConstraintChecker checker;
	private DeclarativeRetriever retriever;
	private AbstractTACBranchSensitiveTransferFunction<AC> aliasTF;
	private ILatticeOperations<AC> aliasOps;
	
	/**
	 * Do not use, only for testing purposes.
	 * @throws FusionException
	 */
	public RelationshipTransferFunction(FusionAnalysis relAnalysis, AbstractTACBranchSensitiveTransferFunction<AC> aliasTF) throws FusionException {
		mainAnalysis = relAnalysis;
		checker = new ConstraintChecker(null, null, relAnalysis.getVariant());
		this.aliasTF = aliasTF;
	}
	
	public RelationshipTransferFunction(FusionAnalysis relAnalysis, ConstraintEnvironment constraints, InferenceEnvironment inf, TypeHierarchy types, DeclarativeRetriever retriever, AbstractTACBranchSensitiveTransferFunction<AC> aliasTF, ILatticeOperations<AC> aliasOps) throws FusionException {
		mainAnalysis = relAnalysis;
		this.infers = inf;
		this.types = types;
		checker = new ConstraintChecker(constraints, types, relAnalysis.getVariant());
		this.retriever = retriever;
		this.aliasTF = aliasTF;
		this.aliasOps = aliasOps;
	}

	@Override
	public void setAnalysisContext(ITACAnalysisContext analysisContext) {
		super.setAnalysisContext(analysisContext);
		aliasTF.setAnalysisContext(analysisContext);
	}

	/**
	 * Get the entry value based on the starting context received from the XML files.
	 */
	public Pair<AC, RelationshipContext> createEntryValue(MethodDeclaration method) {		
		AC aliases = aliasTF.createEntryValue(method);
		RelationshipContext startingContext = retriever.getStartContext();		
		return new Pair<AC, RelationshipContext>(aliases, startingContext);
	}

	public ILatticeOperations<Pair<AC, RelationshipContext>> getLatticeOperations() {
		return new PairLatticeOps<AC, RelationshipContext>(aliasOps, new RelationshipLatticeOperations());
	}
	

	@Override
	public IResult<Pair<AC, RelationshipContext>> transfer(MethodCallInstruction instr,
			List<ILabel> labels, Pair<AC, RelationshipContext> value) {		
		//run twice: once assuming return is false, and again assuming return is true.
		AC aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));
		
		
		if (labels.contains(BooleanLabel.getBooleanLabel(true)) && labels.contains(BooleanLabel.getBooleanLabel(false))) {
			ILabel trueLabel = BooleanLabel.getBooleanLabel(true);
			ILabel falseLabel = BooleanLabel.getBooleanLabel(false);
			
			//true branch
			AC aAfterContextTrue = aliasResults.get(trueLabel);
			BooleanContext tBContext = new BooleanConstantWrapper(instr, mainAnalysis.getBooleanAnalysis(), aBeforeContext, aAfterContextTrue, true);
			FusionEnvironment<AC> tEnv = new FusionEnvironment<AC>(aAfterContextTrue, relsContext, tBContext, types, infers);
			Pair<AC, RelationshipContext> tPair = checker.runGenericTransfer(tEnv, instr);
			
			//false branch
			AC aAfterContextFalse = aliasResults.get(falseLabel);
			BooleanContext fBContext = new BooleanConstantWrapper(instr, mainAnalysis.getBooleanAnalysis(), aBeforeContext, aAfterContextFalse, false);
			FusionEnvironment<AC> fEnv = new FusionEnvironment<AC>(aAfterContextFalse, relsContext, fBContext, types, infers);
			Pair<AC, RelationshipContext> fPair = checker.runGenericTransfer(fEnv, instr);

			
			Pair<AC, RelationshipContext> defPair = new Pair<AC, RelationshipContext>(aliasResults.get(NormalLabel.getNormalLabel()), new RelationshipContext(false));
		
			LabeledResult<Pair<AC, RelationshipContext>> result = LabeledResult.createResult(labels, defPair);
			result.put(trueLabel, tPair);
			result.put(falseLabel, fPair);
			return result;
		}
		else {
			AC aAfterContext = aliasResults.get(NormalLabel.getNormalLabel());
			BooleanContext bContext = new BooleanConstantWrapper(instr, mainAnalysis.getBooleanAnalysis(), aAfterContext);
			FusionEnvironment<AC> env = new FusionEnvironment<AC>(aAfterContext, relsContext, bContext, types, infers);
			Pair<AC, RelationshipContext> pair = checker.runGenericTransfer(env, instr);
			
			return LabeledSingleResult.createResult(pair, labels);
		}
	}


	@Override
	public IResult<Pair<AC, RelationshipContext>> transfer(NewObjectInstruction instr,
			List<ILabel> labels, Pair<AC, RelationshipContext> value) {
		
		AC aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));
		AC aAfterContext = aliasResults.get(NormalLabel.getNormalLabel());

		BooleanContext bContext = new BooleanConstantWrapper(instr, mainAnalysis.getBooleanAnalysis(), aAfterContext);
		FusionEnvironment<AC> env = new FusionEnvironment<AC>(aAfterContext, relsContext, bContext, types, infers);
		Pair<AC, RelationshipContext> pair = checker.runGenericTransfer(env, instr);
	
		return LabeledSingleResult.createResult(pair, labels);
	}

	private IResult<Pair<AC, RelationshipContext>> mapOverResults(
			IResult<AC> aliasResults, List<ILabel> labels,
			RelationshipContext relsContext) {
		Pair<AC, RelationshipContext> defPair = new Pair<AC, RelationshipContext>(aliasResults.get(null), relsContext);
		LabeledResult<Pair<AC, RelationshipContext>> result = LabeledResult.createResult(labels, defPair);

		for (ILabel label : labels)
			result.put(label, new Pair<AC, RelationshipContext>(aliasResults.get(label), relsContext));
		
		return result;
	}

	@Override
	public IResult<Pair<AC, RelationshipContext>> transfer(
			ArrayInitInstruction instr, List<ILabel> labels,
			Pair<AC, RelationshipContext> value) {
		AC aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}


	@Override
	public IResult<Pair<AC, RelationshipContext>> transfer(
			BinaryOperation instr, List<ILabel> labels,
			Pair<AC, RelationshipContext> value) {
		AC aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<AC, RelationshipContext>> transfer(
			CastInstruction instr, List<ILabel> labels,
			Pair<AC, RelationshipContext> value) {
		AC aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<AC, RelationshipContext>> transfer(
			ConstructorCallInstruction instr, List<ILabel> labels,
			Pair<AC, RelationshipContext> value) {
		AC aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<AC, RelationshipContext>> transfer(
			CopyInstruction instr, List<ILabel> labels,
			Pair<AC, RelationshipContext> value) {
		AC aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<AC, RelationshipContext>> transfer(
			DotClassInstruction instr, List<ILabel> labels,
			Pair<AC, RelationshipContext> value) {
		AC aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<AC, RelationshipContext>> transfer(
			EnhancedForConditionInstruction instr, List<ILabel> labels,
			Pair<AC, RelationshipContext> value) {
		AC aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<AC, RelationshipContext>> transfer(
			InstanceofInstruction instr, List<ILabel> labels,
			Pair<AC, RelationshipContext> value) {
		AC aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<AC, RelationshipContext>> transfer(
			LoadArrayInstruction instr, List<ILabel> labels,
			Pair<AC, RelationshipContext> value) {
		AC aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<AC, RelationshipContext>> transfer(
			LoadFieldInstruction instr, List<ILabel> labels,
			Pair<AC, RelationshipContext> value) {
		AC aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<AC, RelationshipContext>> transfer(
			LoadLiteralInstruction instr, List<ILabel> labels,
			Pair<AC, RelationshipContext> value) {
		AC aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<AC, RelationshipContext>> transfer(
			NewArrayInstruction instr, List<ILabel> labels,
			Pair<AC, RelationshipContext> value) {
		AC aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<AC, RelationshipContext>> transfer(
			ReturnInstruction instr, List<ILabel> labels,
			Pair<AC, RelationshipContext> value) {
		AC aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<AC, RelationshipContext>> transfer(
			SourceVariableDeclaration instr, List<ILabel> labels,
			Pair<AC, RelationshipContext> value) {
		AC aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<AC, RelationshipContext>> transfer(
			SourceVariableReadInstruction instr, List<ILabel> labels,
			Pair<AC, RelationshipContext> value) {
		AC aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<AC, RelationshipContext>> transfer(
			StoreArrayInstruction instr, List<ILabel> labels,
			Pair<AC, RelationshipContext> value) {
		AC aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<AC, RelationshipContext>> transfer(
			StoreFieldInstruction instr, List<ILabel> labels,
			Pair<AC, RelationshipContext> value) {
		AC aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}

	@Override
	public IResult<Pair<AC, RelationshipContext>> transfer(
			UnaryOperation instr, List<ILabel> labels,
			Pair<AC, RelationshipContext> value) {
		AC aBeforeContext = value.fst();
		RelationshipContext relsContext = value.snd();
		IResult<AC> aliasResults = aliasTF.transfer(instr, labels, aliasOps.copy(aBeforeContext));

		return mapOverResults(aliasResults, labels, relsContext);
	}
}
