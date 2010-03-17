package edu.cmu.cs.fusion.alias;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.ICrystalAnalysis;
import edu.cmu.cs.crystal.analysis.alias.AliasLE;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.tac.TACFlowAnalysis;
import edu.cmu.cs.crystal.tac.eclipse.CompilationUnitTACs;
import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.xml.XMLRetriever;

@Deprecated
public class MayAliasAnalysisWrapper extends TACFlowAnalysis<TupleLatticeElement<Variable, AliasLE>> {

	public MayAliasAnalysisWrapper(ICrystalAnalysis analysis, CompilationUnitTACs eclipseTAC, TypeHierarchy types, XMLRetriever retriever) {
		super(new MayAliasTransferFunctionWithXMLBindings(analysis, types, retriever), eclipseTAC);
	}

	public AliasContext getAliasContextAfter(ASTNode node) {
		return new AliasLatticeWrapper(getResultsAfter(node));
	}
	
	public AliasContext getAliasContextAfter(TACInstruction instr) {
		return new AliasLatticeWrapper(getResultsAfter(instr));
	}
	
	public AliasContext getInitialAliasContext(MethodDeclaration decl) {
		return new AliasLatticeWrapper(getStartResults(decl));
	}
}
