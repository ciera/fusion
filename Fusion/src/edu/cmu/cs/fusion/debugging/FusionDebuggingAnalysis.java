package edu.cmu.cs.fusion.debugging;

import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.crystal.tac.AbstractTACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.DeclarativeRetriever;
import edu.cmu.cs.fusion.FusionAnalysis;
import edu.cmu.cs.fusion.Variant;
import edu.cmu.cs.fusion.alias.MayPointsToTransferFunctions;
import edu.cmu.cs.fusion.alias.PointsToAliasContext;
import edu.cmu.cs.fusion.alias.PointsToLatticeOps;
import edu.cmu.cs.fusion.relationship.ConstraintChecker;

public class FusionDebuggingAnalysis extends FusionAnalysis<PointsToAliasContext> {

	public FusionDebuggingAnalysis() {
		super(Variant.PRAGMATIC_VARIANT);
	}

	@Override
	public String getName() {return "FusionDebugger";}
	
	@Override
	public void beforeAllMethods(ITypeRoot compUnit,
			CompilationUnit rootNode) {
		super.beforeAllMethods(compUnit, rootNode);
		FusionCache.getCache().setCompUnit(compUnit);
	}

	@Override
	protected void reportResults(MethodDeclaration methodDecl, ConstraintChecker checker) {
		DebuggingCacheVisitor cacheVisitor = new DebuggingCacheVisitor(this, FusionCache.getCache());
		methodDecl.accept(cacheVisitor);
	}

	@Override
	public AbstractTACBranchSensitiveTransferFunction<PointsToAliasContext> getAliasTransferFunction(
			DeclarativeRetriever retriever, TypeHierarchy types) {
		return new MayPointsToTransferFunctions(retriever, types);
	}

	@Override
	public ILatticeOperations<PointsToAliasContext> getAliasLatticeOps(TypeHierarchy types) {
		return new PointsToLatticeOps(types);
	}
	
}
