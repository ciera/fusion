package edu.cmu.cs.fusion;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
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
import edu.cmu.cs.crystal.tac.eclipse.EclipseTAC;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.crystal.util.typehierarchy.CachedTypeHierarchy;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.ConstraintEnvironment;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.relationship.ConstraintChecker;
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
	private TypeHierarchy types;
	private boolean majorErrorOccured = false;

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
			
			for (Constraint cons : constraints) {
				log.log(Level.INFO, cons.toString());
			}
			
		} catch (CoreException err) {
			log.log(Level.SEVERE, "Error while parsing relations", err);
			majorErrorOccured = true;
		}
	}

	

	@Override
	public void beforeAllMethods(ICompilationUnit compUnit,
			CompilationUnit rootNode) {

		if (project == null || !project.equals(compUnit.getJavaProject())) {
			//we have a new project. reset the type hierarchy
			project = compUnit.getJavaProject();
			try {
				IProgressMonitor monitor = getInput().getProgressMonitor().isNone() ? null : getInput().getProgressMonitor().unwrap();
				types = new CachedTypeHierarchy(project, monitor);
				FreeVars.setHierarchy(types);
			} catch (JavaModelException e) {
				log.log(Level.SEVERE, "Could not create type hierarchy", e);
			}
		}
	}

	public String getName() {
		return "FusionAnalysis";
	}
	
	public void analyzeMethod(MethodDeclaration methodDecl) {
		if (types == null || majorErrorOccured) {
			log.log(Level.SEVERE, "something was wrong in initial setup, check log above");
			return;
		}
		try {
			RelationshipTransferFunction tfR = new RelationshipTransferFunction(this, constraints, infers, types);
			fa = new TACFlowAnalysis<RelationshipContext>(tfR, this.analysisInput.getComUnitTACs().unwrap());
			
			MayAliasTransferFunction tfA = new MayAliasTransferFunction(this);
			aliases = new TACFlowAnalysis<TupleLatticeElement<Variable, AliasLE>>(tfA, this.analysisInput.getComUnitTACs().unwrap());
			
			ConstantTransferFunction tfC = new ConstantTransferFunction();
			constants = new TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>>(tfC, this.analysisInput.getComUnitTACs().unwrap());
			
			RelationshipContext finalLattice = fa.getEndResults(methodDecl);
			
			StatementRelationshipVisitor debugger = new StatementRelationshipVisitor(fa);
			methodDecl.accept(debugger);
			log.log(Level.INFO, debugger.getResult());
			
			EclipseTAC tac = this.getInput().getComUnitTACs().unwrap().getMethodTAC(methodDecl);
			ErrorReporterVisitor errVisitor = new ErrorReporterVisitor(this, new ConstraintChecker(constraints, types), reporter, tac);
			methodDecl.accept(errVisitor);
		
		} catch (FusionException e) {
			log.log(Level.SEVERE, "Error in Fusion analysis", e);
		}
	}
	
	public TypeHierarchy getHierarchy() {
		return types;
	}
	
	public InferenceEnvironment getInfers() {
		return infers;
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
