package edu.cmu.cs.fusion;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;
import edu.cmu.cs.crystal.analysis.constant.BooleanConstantLE;
import edu.cmu.cs.crystal.analysis.constant.ConstantTransferFunction;
import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.tac.TACFlowAnalysis;
import edu.cmu.cs.crystal.tac.eclipse.EclipseTAC;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.alias.AliasContext;
import edu.cmu.cs.fusion.alias.PointsToAliasContext;
import edu.cmu.cs.fusion.alias.PointsToLatticeOps;
import edu.cmu.cs.fusion.alias.MayPointsToTransferFunctions;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.ConstraintEnvironment;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.relationship.ConstraintChecker;
import edu.cmu.cs.fusion.relationship.RelationshipContext;
import edu.cmu.cs.fusion.relationship.RelationshipTransferFunction;
import edu.cmu.cs.fusion.xml.XMLRetriever;


public class FusionAnalysis extends AbstractCrystalMethodAnalysis {
	private TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>> constants;
	private TACFlowAnalysis<Pair<PointsToAliasContext,RelationshipContext>> fa;
	private ConstraintEnvironment constraints;
	private Variant variant;
	private Logger log;
	private InferenceEnvironment infers;
	private RelationsEnvironment rels;
	private SharedAnalysisData sharedData;
	private boolean majorErrorOccured = false;
	private DeclarativeRetriever retriever;

	/**
	 * Constructor used only for the purposes of the unit tests of fusion.
	 * 
	 * @param variant
	 */
	public FusionAnalysis(Variant variant) {
		this.variant = variant;
		sharedData = new SharedAnalysisData();
		log = Logger.getLogger("edu.cmu.cs.fusion");
		log.setLevel(Level.INFO);
	}

	public void beforeAllCompilationUnits() {
		rels = new RelationsEnvironment();
		constraints = new ConstraintEnvironment(rels);
		infers = new InferenceEnvironment(rels);
		retriever = new XMLRetriever(rels);
		majorErrorOccured = false;
		try {
			ReportingUtility.clearMarkers(ResourcesPlugin.getWorkspace().getRoot());
			
			rels.populate(null);
			constraints.populate(null);
			infers.populate(null);
			
			FusionFileVisitor visitor = new FusionFileVisitor();
			visitor.addObserver(constraints);
			visitor.addObserver(infers);
			visitor.addObserver(retriever);
			
			ResourcesPlugin.getWorkspace().getRoot().accept(visitor);
			
			for (Constraint cons : constraints) {
				log.log(Level.CONFIG, cons.toString());
			}
			
		} catch (CoreException err) {
			log.log(Level.SEVERE, "Error while parsing relations", err);
			majorErrorOccured = true;
		}
	}

	

	@Override
	public void beforeAllMethods(ICompilationUnit compUnit,
			CompilationUnit rootNode) {
		try {
			sharedData.checkForProjectReset(compUnit.getJavaProject(), analysisInput.getProgressMonitor().isSome() ? analysisInput.getProgressMonitor().unwrap() : null);
			retriever.retrieveRelationships(ResourcesPlugin.getWorkspace().getRoot(), sharedData.getHierarchy());
		} catch (JavaModelException e) {
			log.log(Level.SEVERE, "Could not create type hierarchy", e);
			majorErrorOccured = true;
		} catch (CoreException e) {
			log.log(Level.SEVERE, "Could not create type hierarchy", e);
			majorErrorOccured = true;
		}
	}

	public String getName() {
		return "FusionAnalysis";
	}
	
	public void analyzeMethod(MethodDeclaration methodDecl) {
		if (majorErrorOccured) {
			log.log(Level.SEVERE, "something was wrong in initial setup, check log above");
			return;
		}
		try {
			TypeHierarchy types = sharedData.getHierarchy();
			MayPointsToTransferFunctions aliasTF = new MayPointsToTransferFunctions(retriever, types);
			PointsToLatticeOps ops = new PointsToLatticeOps(types);
			
			RelationshipTransferFunction<PointsToAliasContext> tfR = new RelationshipTransferFunction<PointsToAliasContext>(this, constraints, infers, types, retriever, aliasTF, ops);
			fa = new TACFlowAnalysis<Pair<PointsToAliasContext,RelationshipContext>>(tfR, this.analysisInput.getComUnitTACs().unwrap());
			
			ConstantTransferFunction tfC = new ConstantTransferFunction();
			constants = new TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>>(tfC, this.analysisInput.getComUnitTACs().unwrap());

			RelationshipContext finalLattice = fa.getEndResults(methodDecl).snd();
			
			reportResults(methodDecl, tfR.getConstraintChecker());
		
		} catch (FusionException e) {
			log.log(Level.SEVERE, "Error in Fusion analysis", e);
		}
	}

	protected void reportResults(MethodDeclaration methodDecl, ConstraintChecker checker) {
		EclipseTAC tac = this.getInput().getComUnitTACs().unwrap().getMethodTAC(methodDecl);
		ErrorReporterVisitor errVisitor = new ErrorReporterVisitor(this, checker, reporter, tac, log);
		methodDecl.accept(errVisitor);
	}

	public TypeHierarchy getHierarchy() {
		return sharedData.getHierarchy();
	}
	
	public InferenceEnvironment getInfers() {
		return infers;
	}
	
	public AliasContext getPointsToResultsBefore(ASTNode node) {
		return fa.getResultsBefore(node).fst();
	}
	
	public AliasContext getPointsToResultsAfter(ASTNode node) {
		return fa.getResultsAfter(node).fst();		
	}

	public RelationshipContext getStartingResults(MethodDeclaration d) {
		return fa.getStartResults(d).snd();
	}
	
	public Pair<? extends AliasContext, RelationshipContext> getEndingResults(MethodDeclaration d) {
		return fa.getEndResults(d);
	}
	
	public RelationshipContext getRelResultsBefore(ASTNode node) {
		return fa.getResultsBefore(node).snd();
	}
	
	public Pair<? extends AliasContext, RelationshipContext> getResultsAfter(ASTNode node) {
		return fa.getResultsAfter(node);
	}
	
	public Pair<? extends AliasContext, RelationshipContext> getResultsBeforeAST(ASTNode node) {
		return fa.getResultsBeforeAST(node);
	}


	public RelationshipContext getRelResultsAfter(ASTNode node) {
		return fa.getResultsAfter(node).snd();		
	}

	public RelationshipContext getSpecificRelResultsAfter(ASTNode node, ILabel label) {
		return fa.getLabeledResultsAfter(node).get(label).snd();
	}

	public TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>> getBooleanAnalysis() {
		return constants;
	}

	public Variant getVariant() {
		return variant;
	}

}
