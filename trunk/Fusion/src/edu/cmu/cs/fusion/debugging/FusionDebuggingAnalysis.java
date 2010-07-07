package edu.cmu.cs.fusion.debugging;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.fusion.FusionAnalysis;
import edu.cmu.cs.fusion.Variant;
import edu.cmu.cs.fusion.relationship.ConstraintChecker;

public class FusionDebuggingAnalysis extends FusionAnalysis {

	public FusionDebuggingAnalysis() {
		super(Variant.PRAGMATIC_VARIANT);
	}

	@Override
	public String getName() {return "FusionDebugger";}
	
	@Override
	public void beforeAllMethods(ICompilationUnit compUnit,
			CompilationUnit rootNode) {
		super.beforeAllMethods(compUnit, rootNode);
		FusionCache.getCache().setCompUnit(compUnit);
	}

	@Override
	protected void reportResults(MethodDeclaration methodDecl, ConstraintChecker checker) {
		DebuggingCacheVisitor cacheVisitor = new DebuggingCacheVisitor(this, FusionCache.getCache());
		methodDecl.accept(cacheVisitor);
	}

	
}
