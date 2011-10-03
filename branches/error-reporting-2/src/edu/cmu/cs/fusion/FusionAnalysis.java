package edu.cmu.cs.fusion;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;
import edu.cmu.cs.crystal.analysis.constant.BooleanConstantLE;
import edu.cmu.cs.crystal.analysis.constant.ConstantTransferFunction;
import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.tac.AbstractTACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.TACFlowAnalysis;
import edu.cmu.cs.crystal.tac.eclipse.EclipseTAC;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.alias.AliasContext;
import edu.cmu.cs.fusion.alias.NoVarsAliasContext;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.ConstraintEnvironment;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.relationship.ConstraintChecker;
import edu.cmu.cs.fusion.relationship.RelationshipContext;
import edu.cmu.cs.fusion.relationship.RelationshipTransferFunction;
import edu.cmu.cs.fusion.xml.XMLRetriever;


public abstract class FusionAnalysis<AC extends AliasContext> extends AbstractCrystalMethodAnalysis {
	private static final String BASE_FUSION_LOGGER = "edu.cmu.cs.fusion";
	public static final String FUSION_LOGGER = BASE_FUSION_LOGGER + ".core";
	public static final String REPORTS_LOGGER = BASE_FUSION_LOGGER + ".reports";
	private TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>> constants;
	private TACFlowAnalysis<FusionLattice<AC>> fa;
	private ConstraintEnvironment constraints;
	private Variant variant;
	private Logger log;
	private InferenceEnvironment infers;
	private RelationsEnvironment rels;
	private SharedAnalysisData sharedData;
	private boolean majorErrorOccured = false;
	private DeclarativeRetriever retriever;
	private String compUnitName;

	/**
	 * Constructor used only for the purposes of the unit tests of fusion.
	 * 
	 * @param variant
	 */
	public FusionAnalysis(Variant variant) {
		this.variant = variant;
		sharedData = new SharedAnalysisData();
		log = Logger.getLogger(FUSION_LOGGER);
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
	public void beforeAllMethods(ITypeRoot compUnit,
			CompilationUnit rootNode) {
		try {
			compUnitName = compUnit.getElementName();
			sharedData.checkForProjectReset(compUnit.getJavaProject(), analysisInput.getProgressMonitor().isSome() ? analysisInput.getProgressMonitor().unwrap() : null);
			retriever.retrieveRelationships(ResourcesPlugin.getWorkspace().getRoot(), sharedData.getHierarchy());
			
			ConstraintChecker checker = new ConstraintChecker(constraints, getHierarchy(), variant, null);
			NoVarsAliasContext aContext = new NoVarsAliasContext(retriever.getAllLabels());
			RelationshipContext rels = retriever.getStartContext();
			new ErrorReporterVisitor<AC>(this, checker, reporter, null, compUnitName).checkXMLError(aContext, rels, rootNode);
			
		} catch (JavaModelException e) {
			log.log(Level.SEVERE, "Could not set up compilation unit for analysis", e);
			majorErrorOccured = true;
		} catch (CoreException e) {
			log.log(Level.SEVERE, "Could not set up compilation unit for analysis", e);
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
			AbstractTACBranchSensitiveTransferFunction<AC> aliasTF = getAliasTransferFunction(retriever);
			ILatticeOperations<AC> ops = getAliasLatticeOps();
			
			RelationshipTransferFunction<AC> tfR = new RelationshipTransferFunction<AC>(this, constraints, infers, types, retriever, aliasTF, ops);
			fa = new TACFlowAnalysis<FusionLattice<AC>>(tfR, this.analysisInput.getComUnitTACs().unwrap());
			
			ConstantTransferFunction tfC = new ConstantTransferFunction();
			constants = new TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>>(tfC, this.analysisInput.getComUnitTACs().unwrap());

			RelationshipContext finalLattice = fa.getEndResults(methodDecl).getRelContext();
			
			reportResults(methodDecl, tfR.getConstraintChecker());
		
		} catch (FusionException e) {
			log.log(Level.SEVERE, "Error in Fusion analysis", e);
		}
	}
	
	abstract public AbstractTACBranchSensitiveTransferFunction<AC> getAliasTransferFunction(DeclarativeRetriever retriever);
	
	abstract public ILatticeOperations<AC> getAliasLatticeOps();

	protected void reportResults(MethodDeclaration methodDecl, ConstraintChecker checker) {
		EclipseTAC tac = this.getInput().getComUnitTACs().unwrap().getMethodTAC(methodDecl);
		ErrorReporterVisitor<AC> errVisitor = new ErrorReporterVisitor<AC>(this, checker, reporter, tac, compUnitName);
		methodDecl.accept(errVisitor);
	}

	public TypeHierarchy getHierarchy() {
		return sharedData.getHierarchy();
	}
	
	public InferenceEnvironment getInfers() {
		return infers;
	}
	
	public AliasContext getPointsToResultsBefore(ASTNode node) {
		return fa.getResultsBeforeCFG(node).getAliasContext();
	}
	
	public AliasContext getPointsToResultsIntermediate(ASTNode node) {
		return fa.getResultsAfterCFG(node).getAliasesForTrigger();	
	}

	public AliasContext getPointsToResultsAfter(ASTNode node) {
		return fa.getResultsAfterCFG(node).getAliasContext();		
	}

	public RelationshipContext getStartingResults(MethodDeclaration d) {
		return fa.getStartResults(d).getRelContext();
	}
	
	public FusionLattice<AC> getEndingResults(MethodDeclaration d) {
		return fa.getEndResults(d);
	}
	
	public RelationshipContext getRelResultsBefore(ASTNode node) {
		return fa.getResultsBeforeCFG(node).getRelContext();
	}
	
	public FusionLattice<AC> getResultsAfter(ASTNode node) {
		return fa.getResultsAfterCFG(node);
	}
	
	public FusionLattice<AC> getResultsBeforeAST(ASTNode node) {
		return fa.getResultsBeforeAST(node);
	}


	public RelationshipContext getRelResultsAfter(ASTNode node) {
		return fa.getResultsAfterCFG(node).getRelContext();	
	}

	public RelationshipContext getSpecificRelResultsAfter(ASTNode node, ILabel label) {
		return fa.getLabeledResultsAfter(node).get(label).getRelContext();
	}

	public TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>> getBooleanAnalysis() {
		return constants;
	}

	public Variant getVariant() {
		return variant;
	}
}
