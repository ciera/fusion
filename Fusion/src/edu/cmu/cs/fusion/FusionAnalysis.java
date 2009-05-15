package edu.cmu.cs.fusion;

import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;
import edu.cmu.cs.crystal.analysis.alias.MayAliasAnalysis;
import edu.cmu.cs.crystal.analysis.constant.ConstantAnalysis;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.tac.MethodCallInstruction;
import edu.cmu.cs.crystal.tac.TACFlowAnalysis;
import edu.cmu.cs.crystal.tac.TACInstruction;
import edu.cmu.cs.crystal.tac.Variable;
import edu.cmu.cs.fusion.constraint.Constraint;
import edu.cmu.cs.fusion.constraint.ConstraintEnvironment;
import edu.cmu.cs.crystal.analysis.alias.AliasLE;
import edu.cmu.cs.fusion.relationship.RelationshipContext;
import edu.cmu.cs.fusion.relationship.RelationshipTransferFunction;
import edu.cmu.cs.fusion.relationship.RelationshipTransferFunction.Variant;


public class FusionAnalysis extends AbstractCrystalMethodAnalysis {
	private ConstantAnalysis constants;
	private MayAliasAnalysis aliases;
	private TACFlowAnalysis<RelationshipContext> fa;
	private ConstraintEnvironment constraints;
	private Variant variant;
	
	public FusionAnalysis(MayAliasAnalysis aliases, ConstantAnalysis constants, Variant variant) {
		this.aliases = aliases;
		this.constants = constants;
		this.variant = variant;
		constraints = new ConstraintEnvironment();
	}

	public void beforeAllCompilationUnits() {
		constraints.populate();
	}

	public String getName() {
		return "Fusion Analysis";
	}
	
	public void analyzeMethod(MethodDeclaration d) {
		RelationshipTransferFunction tf = new RelationshipTransferFunction(this, constraints, variant);
		fa = new TACFlowAnalysis<RelationshipContext>(tf, this.analysisInput.getComUnitTACs().unwrap());
		
		// must call getResultsAfter at least once on this method,
		// or the analysis won't be run on this method
		RelationshipContext finalLattice = fa.getResultsAfter(d);
		
		//d.accept(checkResults );

	}
	
	public RelationshipContext getResultsBefore(TACInstruction instr) {
		return fa.getResultsBefore(instr);
	}
	
	public RelationshipContext getResultsAfter(TACInstruction instr) {
		return fa.getResultsAfter(instr);		
	}
	
	
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

	public void reportError(Variant variant, Constraint cons, TACInstruction instr) {
		//TODO
	}

	public MayAliasAnalysis getAliasAnalysis() {
		return aliases;
	}

	public Object getBooleanAnalysis() {
		// TODO Auto-generated method stub
		return null;
	}
}
