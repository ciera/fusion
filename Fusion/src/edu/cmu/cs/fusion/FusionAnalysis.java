package edu.cmu.cs.fusion;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;
import edu.cmu.cs.crystal.IAnalysisReporter.SEVERITY;
import edu.cmu.cs.crystal.analysis.alias.AliasLE;
import edu.cmu.cs.crystal.analysis.alias.MayAliasTransferFunction;
import edu.cmu.cs.crystal.analysis.constant.BooleanConstantLE;
import edu.cmu.cs.crystal.analysis.constant.ConstantTransferFunction;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.tac.TACFlowAnalysis;
import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.ConstraintEnvironment;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.relationship.RelationshipContext;
import edu.cmu.cs.fusion.relationship.RelationshipTransferFunction;
import edu.cmu.cs.fusion.relationship.RelationshipTransferFunction.Variant;


public class FusionAnalysis extends AbstractCrystalMethodAnalysis {
	private TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>> constants;
	private TACFlowAnalysis<TupleLatticeElement<Variable, AliasLE>> aliases;
	private TACFlowAnalysis<RelationshipContext> fa;
	private ConstraintEnvironment constraints;
	private Variant variant;
	private Logger log;
	private InferenceEnvironment infers;
	private RelationsEnvironment rels;
	
	/**
	 * Default constructor which Crystal will use to create the entire analysis.
	 */
	public FusionAnalysis() {
		this(Variant.PRAGMATIC);
	}

	/**
	 * Constructor used only for the purposes of the unit tests of fusion.
	 * 
	 * @param variant
	 */
	public FusionAnalysis(Variant variant) {
		this.variant = variant;
		log = Logger.getLogger("edu.cmu.cs.fusion");
		log.setLevel(Level.CONFIG);
	}

	public void beforeAllCompilationUnits() {
		rels = new RelationsEnvironment();
		constraints = new ConstraintEnvironment();
		infers = new InferenceEnvironment();
		try {
			rels.populate(null);
			constraints.populate(rels, null);
			infers.populate(rels, null);
		} catch (CoreException err) {
			log.log(Level.SEVERE, "Error while parsing relations", err);
		}
	}


	public String getName() {
		return "FusionAnalysis";
	}
	
	public void analyzeMethod(MethodDeclaration methodDecl) {
		RelationshipTransferFunction tfR = new RelationshipTransferFunction(this, constraints, infers, variant);
		fa = new TACFlowAnalysis<RelationshipContext>(tfR, this.analysisInput.getComUnitTACs().unwrap());
		
		MayAliasTransferFunction tfA = new MayAliasTransferFunction(this);
		aliases = new TACFlowAnalysis<TupleLatticeElement<Variable, AliasLE>>(tfA, this.analysisInput.getComUnitTACs().unwrap());
		aliases.getResultsAfter(methodDecl);
		
		ConstantTransferFunction tfC = new ConstantTransferFunction();
		constants = new TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>>(tfC, this.analysisInput.getComUnitTACs().unwrap());
		constants.getResultsAfter(methodDecl);
		
		// must call getResultsAfter at least once on this method,
		// or the analysis won't be run on this method
		RelationshipContext finalLattice = fa.getEndResults(methodDecl);
		
		StatementRelationshipVisitor debugger = new StatementRelationshipVisitor(fa);
		methodDecl.accept(debugger);
		log.log(Level.INFO, debugger.getResult());
		//report the errors here...
	}
	
	public RelationshipContext getResultsBefore(ASTNode node) {
		return fa.getResultsBefore(node);
	}
	
	public RelationshipContext getResultsAfter(ASTNode node) {
		return fa.getResultsAfter(node);		
	}
	
	public void reportError(Variant variant, Constraint cons, TACInstruction instr) {
		//TODO: to avoid duplicates, store the errors, then report them.
		reporter.reportUserProblem("The constraint " + cons.toString() + " was violated.", instr.getNode(), this.getName() + ": " + variant.toString(), SEVERITY.WARNING);
	}

	public TACFlowAnalysis<TupleLatticeElement<Variable, AliasLE>> getAliasAnalysis() {
		return aliases;
	}

	public TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>> getBooleanAnalysis() {
		return constants;
	}
}
