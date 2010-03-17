package edu.cmu.cs.fusion.relationship;

import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.flow.BooleanLabel;
import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.flow.LabeledResult;
import edu.cmu.cs.crystal.flow.LabeledSingleResult;
import edu.cmu.cs.crystal.tac.AbstractTACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.model.MethodCallInstruction;
import edu.cmu.cs.crystal.tac.model.NewObjectInstruction;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.BooleanConstantWrapper;
import edu.cmu.cs.fusion.BooleanContext;
import edu.cmu.cs.fusion.FusionAnalysis;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.FusionException;
import edu.cmu.cs.fusion.alias.AliasContext;
import edu.cmu.cs.fusion.constraint.ConstraintEnvironment;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.xml.XMLRetriever;


public class RelationshipTransferFunction extends AbstractTACBranchSensitiveTransferFunction<RelationshipContext> {

	private FusionAnalysis mainAnalysis;
	protected TypeHierarchy types;
	private InferenceEnvironment infers;
	private ConstraintChecker checker;
	private XMLRetriever retriever;
	
	/**
	 * Do not use, only for testing purposes.
	 * @throws FusionException
	 */
	public RelationshipTransferFunction(FusionAnalysis relAnalysis) throws FusionException {
		mainAnalysis = relAnalysis;
		checker = new ConstraintChecker(null, null);
	}
	
	public RelationshipTransferFunction(FusionAnalysis relAnalysis, ConstraintEnvironment constraints, InferenceEnvironment inf, TypeHierarchy types, XMLRetriever retriever) throws FusionException {
		mainAnalysis = relAnalysis;
		this.infers = inf;
		this.types = types;
		checker = new ConstraintChecker(constraints, types);
		this.retriever = retriever;
	}

	/**
	 * Get the entry value based on the starting context received from the XML files.
	 */
	public RelationshipContext createEntryValue(MethodDeclaration method) {
//		Variable thisVar = getAnalysisContext().getThisVariable();
//		AliasContext aliases = mainAnalysis.getAliasAnalysis().getInitialAliasContext(method);
		
		RelationshipContext startingContext = retriever.getStartContext();
		
		//TACInstruction instr = new EntryInstruction(method);
		//BooleanContext bContext = new BooleanConstantWrapper(instr, mainAnalysis.getBooleanAnalysis(), mainAnalysis.getAliasAnalysis());
		//FusionEnvironment env = new FusionEnvironment(aContext, startingContext, bContext, types, infers);
		//RelationshipContext startingContext = checker.runGenericTransfer(env, instr);
		//hmm, a lot of work just to apply some silly callbacks. Maybe better to just
		//hack this in separately.
		
		return startingContext;
	}

	public ILatticeOperations<RelationshipContext> getLatticeOperations() {
		return new RelationshipLatticeOperations();
	}

	@Override
	public IResult<RelationshipContext> transfer(MethodCallInstruction instr,
			List<ILabel> labels, RelationshipContext value) {
		//some options:
		//pass in two alias contexts: 1 for before/result, 1 for effects?
		//no, I should be able to just use the after values. The receiver should not have been
		//anywhere else in the expression in a TAC system....right? Can I do x = x.foo()? What about x = y.bar(a, a) (though that has no affect...)
		//if not, use the after value.
		//		However, consider how this might affect reference equality predicates. I suppose since result shouldn't be used
		//		in trigger or predicate, this won't be an issue. Doing that would be dirty.
		//if so: 
		//what if we let the freevars decide this? Upon getting free vars, also state whether it is a "before" or "after"  variable.
		//trig and req produce before. effect produces afters for * and result, but before otherwise?
		//op produces before for everything except result?
		//
		//For now, I'm just going to assume that we use the after value, and result isn't allowed in the trigger or requires preds.

		
		
		//run twice: once assuming return is false, and again assuming return is true.
		AliasContext aContext = mainAnalysis.getAliasAnalysis().getResultsAfter(instr);
		if (labels.contains(BooleanLabel.getBooleanLabel(true)) && labels.contains(BooleanLabel.getBooleanLabel(false))) {
			BooleanContext tBContext = new BooleanConstantWrapper(instr, mainAnalysis.getBooleanAnalysis(), mainAnalysis.getAliasAnalysis(), true);
			FusionEnvironment tEnv = new FusionEnvironment(aContext, value, tBContext, types, infers);
			RelationshipContext tNewContext = checker.runGenericTransfer(tEnv, instr);
			
			BooleanContext fBContext = new BooleanConstantWrapper(instr, mainAnalysis.getBooleanAnalysis(), mainAnalysis.getAliasAnalysis(), false);
			FusionEnvironment fEnv = new FusionEnvironment(aContext, value, fBContext, types, infers);
			RelationshipContext fNewContext = checker.runGenericTransfer(fEnv, instr);
			
			LabeledResult<RelationshipContext> result = LabeledResult.createResult(labels, new RelationshipContext(false));
			result.put(BooleanLabel.getBooleanLabel(true), tNewContext);
			result.put(BooleanLabel.getBooleanLabel(false), fNewContext);
			return result;
		}
		else {
			BooleanContext bContext = new BooleanConstantWrapper(instr, mainAnalysis.getBooleanAnalysis(), mainAnalysis.getAliasAnalysis());
			FusionEnvironment env = new FusionEnvironment(aContext, value, bContext, types, infers);
			RelationshipContext newContext = checker.runGenericTransfer(env, instr);
			
			return LabeledSingleResult.createResult(newContext);
		}
	}

	@Override
	public IResult<RelationshipContext> transfer(NewObjectInstruction instr,
			List<ILabel> labels, RelationshipContext value) {
		AliasContext aContext = mainAnalysis.getAliasAnalysis().getResultsAfter(instr);
		BooleanContext bContext = new BooleanConstantWrapper(instr, mainAnalysis.getBooleanAnalysis(), mainAnalysis.getAliasAnalysis());
		FusionEnvironment env = new FusionEnvironment(aContext, value, bContext, types, infers);
		RelationshipContext newContext = checker.runGenericTransfer(env, instr);
		
		return LabeledSingleResult.createResult(newContext);		
	}
}
