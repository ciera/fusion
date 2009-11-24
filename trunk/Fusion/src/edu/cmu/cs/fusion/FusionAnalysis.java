package edu.cmu.cs.fusion;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;
import edu.cmu.cs.crystal.analysis.alias.AliasLE;
import edu.cmu.cs.crystal.analysis.alias.MayAliasTransferFunction;
import edu.cmu.cs.crystal.analysis.constant.BooleanConstantLE;
import edu.cmu.cs.crystal.analysis.constant.ConstantTransferFunction;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.tac.TACFlowAnalysis;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.fusion.constraint.ConstraintEnvironment;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.relationship.RelationshipContext;
import edu.cmu.cs.fusion.relationship.RelationshipTransferFunction;


public class FusionAnalysis extends AbstractCrystalMethodAnalysis {
	private TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>> constants;
	private TACFlowAnalysis<TupleLatticeElement<Variable, AliasLE>> aliases;
	private TACFlowAnalysis<RelationshipContext> fa;
	private ConstraintEnvironment constraints;
	private Variant variant;
	private Logger log;
	private InferenceEnvironment infers;
	private RelationsEnvironment rels;
	private IJavaProject project;
	private FusionErrorStorage errors;
	
	/**
	 * Default constructor which Crystal will use to create the entire analysis.
	 */
	public FusionAnalysis() {
		this(new Variant(Variant.PRAGMATIC));
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

	

	@Override
	public void beforeAllMethods(ICompilationUnit compUnit,
			CompilationUnit rootNode) {
		project = compUnit.getJavaProject();
	}

	public String getName() {
		return "FusionAnalysis";
	}
	
	public void analyzeMethod(MethodDeclaration methodDecl) {
		try {
			errors = new FusionErrorStorage();
			IProgressMonitor monitor = getInput().getProgressMonitor().isNone() ? null : getInput().getProgressMonitor().unwrap();
			RelationshipTransferFunction tfR = new RelationshipTransferFunction(this, errors, constraints, infers, variant, project, monitor);
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
			
			ErrorReporterVisitor errVisitor = new ErrorReporterVisitor(errors, reporter);
			methodDecl.accept(errVisitor);
			errors = null;
			
			//report the errors here...
		} catch (FusionException e) {
			log.log(Level.SEVERE, "Error in Fusion analysis", e);
		}
	}
	
	public RelationshipContext getResultsBefore(ASTNode node) {
		return fa.getResultsBefore(node);
	}
	
	public RelationshipContext getResultsAfter(ASTNode node) {
		return fa.getResultsAfter(node);		
	}

	public TACFlowAnalysis<TupleLatticeElement<Variable, AliasLE>> getAliasAnalysis() {
		return aliases;
	}

	public TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>> getBooleanAnalysis() {
		return constants;
	}
}
