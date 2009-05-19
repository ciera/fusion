package edu.cmu.cs.fusion.test.flow;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class TestMethodCallTransferFunction {

	@BeforeClass
	static public void setup() {
//		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT);
//		ASTParser parser = ASTParser.newParser(AST.JLS3);
//		CompilationUnit node;
//
//		project.open(null);
//
//		IJavaProject javaProject = JavaCore.create(project);
//		ICompilationUnit source = javaProject.findType(qualifiedCompUnitName)
//				.getCompilationUnit();
//
//		parser.setKind(ASTParser.K_COMPILATION_UNIT);
//		parser.setSource(source);
//		parser.setResolveBindings(true);
//		node = (CompilationUnit) parser.createAST(null);

	}
	
	@Test
	@Ignore
	public void testNoMatches() {
		
	}
	
	@Test
	@Ignore
	public void testWithChangesSingle() {
		
	}
	
	@Test
	@Ignore
	public void testWithChangesMultiple() {
		
	}
	
	@Test
	@Ignore
	public void testBranching() {
		
	}
	
	@Test
	@Ignore
	public void testAliases() {
		
	}
}
