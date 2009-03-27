package edu.cmu.cs.fusion;

import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;
import edu.cmu.cs.crystal.IAnalysisInput;
import edu.cmu.cs.crystal.IAnalysisReporter;
import edu.cmu.cs.crystal.ICrystalAnalysis;
import edu.cmu.cs.crystal.analysis.alias.MayAliasAnalysis;
import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.analysis.constant.ConstantAnalysis;
import edu.cmu.cs.crystal.tac.TACFlowAnalysis;
import edu.cmu.cs.crystal.tac.Variable;
import edu.cmu.cs.fusion.constraint.ConstraintEnvironment;
import edu.cmu.cs.fusion.relationship.RelationshipContext;
import edu.cmu.cs.fusion.relationship.RelationshipTransferFunction;


public class FusionAnalysis extends AbstractCrystalMethodAnalysis {
	private ConstantAnalysis constants;
	private MayAliasAnalysis aliases;
	private TACFlowAnalysis<RelationshipContext> fa;
	private ConstraintEnvironment constraints;
	
	public FusionAnalysis(MayAliasAnalysis aliases, ConstantAnalysis constants) {
		this.aliases = aliases;
		this.constants = constants;
		constraints = new ConstraintEnvironment();
	}

	public void beforeAllCompilationUnits() {
		constraints.populate();
	}

	public String getName() {
		return "Fusion Analysis";
	}
	
	public void analyzeMethod(MethodDeclaration d) {
		RelationshipTransferFunction tf = new RelationshipTransferFunction(this);
		fa = new TACFlowAnalysis<RelationshipContext>(tf, this.analysisInput.getComUnitTACs().unwrap());
		
		// must call getResultsAfter at least once on this method,
		// or the analysis won't be run on this method
		RelationshipContext finalLattice = fa.getResultsAfter(d);
		
		//d.accept(checkResults );

	}
	
	public Set<ObjectLabel> getLabels(Variable var, ASTNode node) {
		return aliases.getAfterAliasLabels(var, node);
	}
 
	
	/**
	 * Get all the object labels at this node which have the given type, regardless
	 * of who the alias is. This will also include anything which is a subtype of
	 * the given type.
	 * This is expensive.
	 * @param typeBinding
	 * @param node
	 * @return A set of all object labels at node which are castable to typeBinding
	 */
 	public Set<ObjectLabel> getLabels(ITypeBinding typeBinding, ASTNode node) {
		return aliases.getAfterAliasLabels(typeBinding, node);
	}

	public ThreeValue getBoolInfo(Variable setVar, ASTNode node) {
		if (!constants.hasPreciseValueAfter(setVar, node, false))
			return ThreeValue.UNKNOWN;
		else if (constants.getValue(setVar, node, false))
			return ThreeValue.TRUE;
		else
			return ThreeValue.FALSE;
	}

	/**
	 *  Get the object labels that match the type of a relationship parameter
	 * @param name The name of the relationship predicate
	 * @param arity The arity of the relationship predicate
	 * @param ndx The index of the parameter we want
	 * @param node The node at which we should get the object labels
	 * @return
	 */
/*	public Set<ObjectLabel> getLabels(String name, int arity, int ndx, ASTNode node) {
		String typeName; 
		
		typeName = checker.getTypes(name, arity)[ndx];
		
		return aliases.getAfterAliasLabels(typeName, node);

	}
*/
	
	private ITypeBinding getMatchingType(TypeDeclaration decl, String typeName) {
		if (decl.resolveBinding().getQualifiedName().equals(typeName))
			return decl.resolveBinding();
		
		for (TypeDeclaration inner : decl.getTypes()) {
			ITypeBinding binding = getMatchingType(inner, typeName);
			if (binding != null)
				return binding;
		}
		return null;
	}


	
/*	public boolean isCastCompatible(ITypeBinding compareBinding, String typeName, IJavaProject project) {
		IType type;
		ICompilationUnit compU;
		CompilationUnit rootNode;
		ITypeBinding binding = null;
		
		try {
			type = project.findType(typeName, (IProgressMonitor)null);
			
			compU = type.getCompilationUnit();

			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setResolveBindings(true);
			parser.setSource(compU);
			rootNode = (CompilationUnit) parser.createAST(null);
			
			for (TypeDeclaration decl : (List<TypeDeclaration>)rootNode.types()) {
				binding = getMatchingType(decl, typeName);
				if (binding != null)
					break;
			}		
			
			return compareBinding.isCastCompatible(binding);
		} catch (JavaModelException e) {
			return false;
		}
	}
*/
}
