package edu.cmu.cs.fusion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import edu.cmu.cs.fusion.alias.MayPointsToAliasContext;
import edu.cmu.cs.fusion.alias.MayPointsToLatticeOps;
import edu.cmu.cs.fusion.alias.MayPointsToTransferFunctions;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.ConstraintEnvironment;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.relationship.ConstraintChecker;
import edu.cmu.cs.fusion.relationship.RelationshipContext;
import edu.cmu.cs.fusion.relationship.RelationshipTransferFunction;
import edu.cmu.cs.fusion.xml.XMLRetriever;


public class FusionAnalysis extends AbstractCrystalMethodAnalysis {
	protected static final String BASE_FUSION_LOGGER = "edu.cmu.cs.fusion";
	public static final String FUSION_LOGGER = BASE_FUSION_LOGGER + ".core";
	public static final String REPORTS_LOGGER = BASE_FUSION_LOGGER + ".reports";
	public static final String CHECKS_LOGGER = BASE_FUSION_LOGGER + ".checks";
	private TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>> constants;
	private TACFlowAnalysis<Pair<MayPointsToAliasContext,RelationshipContext>> fa;
	private ConstraintEnvironment constraints;
	private Variant variant;
	private Logger log;
	private InferenceEnvironment infers;
	private RelationsEnvironment rels;
	private SharedAnalysisData sharedData;
	private boolean majorErrorOccured = false;
	private DeclarativeRetriever retriever;
	private String compUnitName;
	private List<String> methodBlacklist = new ArrayList<String>();
	private long startTime;
	private MethodDeclaration currentMethod;
	
	/**
	 * Constructor used only for the purposes of the unit tests of fusion.
	 * 
	 * @param variant
	 */
	public FusionAnalysis(Variant variant) {
		this.variant = variant;
		sharedData = new SharedAnalysisData();
		log = Logger.getLogger(FUSION_LOGGER);
		
		File file = new File("/tmp/methods.blacklist");		
		if (file.exists()) {
			BufferedReader r = null;
			try {
				r = new BufferedReader(new FileReader(file));
				String line = r.readLine();
				while (line != null) {
					methodBlacklist.add(line);
					line = r.readLine();
				}
			}
			catch (FileNotFoundException e) {
				log.log(Level.WARNING, "Couldn't find blacklist at "+file.getAbsolutePath());
			}
			catch (IOException e) {
				log.log(Level.WARNING, "Couldn't read blacklist at "+file.getAbsolutePath());
			}
			try {
				if (r != null)
					r.close();
			} catch (IOException e) {
				log.log(Level.WARNING, "Couldn't close blacklist at "+file.getAbsolutePath());
			}
		}
		else
			log.log(Level.WARNING, "Couldn't find blacklist at "+file.getAbsolutePath());
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

	
	public void checkIfTimeout() {
		long timeTaken = System.currentTimeMillis() - startTime;
		if (timeTaken > 30000)
			throw new TimeoutException("TIMEOUT: Cancelling check of method " + currentMethod.resolveBinding().toString() + " in " + compUnitName + "  after " + ((double)timeTaken)/1000 + " seconds");			
	}

	@Override
	public void beforeAllMethods(ICompilationUnit compUnit,
			CompilationUnit rootNode) {
		try {
			compUnitName = compUnit.getElementName();
			sharedData.checkForProjectReset(compUnit.getJavaProject(), analysisInput.getProgressMonitor().isSome() ? analysisInput.getProgressMonitor().unwrap() : null);
		} catch (JavaModelException e) {
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
		currentMethod = methodDecl;
		String methodString = compUnitName + " " + methodDecl.resolveBinding().toString();
		for (String blacklisted : methodBlacklist) {
			if (methodString.matches(blacklisted)) {
				log.log(Level.SEVERE, "Skipping method because it's blacklisted: "+ methodString);	
				return;
			}
		}

		try {
			startTime = System.currentTimeMillis();

			if (analysisInput.getProgressMonitor().isSome())
				analysisInput.getProgressMonitor().unwrap().subTask("Analyzing " + compUnitName  + " " + methodDecl.resolveBinding().toString());

			TypeHierarchy types = sharedData.getHierarchy();
			MayPointsToTransferFunctions aliasTF = new MayPointsToTransferFunctions(retriever, types);
			MayPointsToLatticeOps ops = new MayPointsToLatticeOps(types);
			
			RelationshipTransferFunction<MayPointsToAliasContext> tfR = new RelationshipTransferFunction<MayPointsToAliasContext>(this, constraints, infers, types, retriever, aliasTF, ops);
			fa = new TACFlowAnalysis<Pair<MayPointsToAliasContext,RelationshipContext>>(tfR, this.analysisInput.getComUnitTACs().unwrap());
			
			ConstantTransferFunction tfC = new ConstantTransferFunction();
			constants = new TACFlowAnalysis<TupleLatticeElement<Variable, BooleanConstantLE>>(tfC, this.analysisInput.getComUnitTACs().unwrap());

			RelationshipContext finalLattice = fa.getEndResults(methodDecl).snd();
			
			reportResults(methodDecl, tfR.getConstraintChecker());
		} catch (TimeoutException e) {
			log.log(Level.SEVERE, e.getMessage());
		} catch (FusionException e) {
			log.log(Level.SEVERE, "Error in Fusion analysis", e);
		}
	}

	protected void reportResults(MethodDeclaration methodDecl, ConstraintChecker checker) {
		EclipseTAC tac = this.getInput().getComUnitTACs().unwrap().getMethodTAC(methodDecl);
		ErrorReporterVisitor errVisitor = new ErrorReporterVisitor(this, checker, reporter, tac, compUnitName);
		methodDecl.accept(errVisitor);
	}

	public TypeHierarchy getHierarchy() {
		return sharedData.getHierarchy();
	}
	
	public InferenceEnvironment getInfers() {
		return infers;
	}
	
	public AliasContext getPointsToResultsBefore(ASTNode node) {
		return fa.getResultsBeforeCFG(node).fst();
	}
	
	public AliasContext getPointsToResultsAfter(ASTNode node) {
		return fa.getResultsAfterCFG(node).fst();		
	}

	public RelationshipContext getStartingResults(MethodDeclaration d) {
		return fa.getStartResults(d).snd();
	}
	
	public Pair<? extends AliasContext, RelationshipContext> getEndingResults(MethodDeclaration d) {
		return fa.getEndResults(d);
	}
	
	public RelationshipContext getRelResultsBefore(ASTNode node) {
		return fa.getResultsBeforeCFG(node).snd();
	}
	
	public Pair<? extends AliasContext, RelationshipContext> getResultsAfter(ASTNode node) {
		return fa.getResultsAfterCFG(node);
	}
	
	public Pair<? extends AliasContext, RelationshipContext> getResultsBeforeAST(ASTNode node) {
		return fa.getResultsBeforeAST(node);
	}


	public RelationshipContext getRelResultsAfter(ASTNode node) {
		return fa.getResultsAfterCFG(node).snd();		
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
