package edu.cmu.cs.fusion.alias;

import java.util.Set;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.ICrystalAnalysis;
import edu.cmu.cs.crystal.analysis.alias.AliasLE;
import edu.cmu.cs.crystal.analysis.alias.DefaultObjectLabel;
import edu.cmu.cs.crystal.analysis.alias.LiteralLabel;
import edu.cmu.cs.crystal.analysis.alias.MayAliasTransferFunction;
import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.xml.XMLRetriever;

@Deprecated
public class MayAliasTransferFunctionWithXMLBindings extends
		MayAliasTransferFunction {

	private XMLRetriever retriever;

	public MayAliasTransferFunctionWithXMLBindings(ICrystalAnalysis analysis, TypeHierarchy types, XMLRetriever retriever) {
		super(analysis, types);
		this.retriever = retriever;
	}

	@Override
	public TupleLatticeElement<Variable, AliasLE> createEntryValue(
			MethodDeclaration m) {
		TupleLatticeElement<Variable, AliasLE> entry = ops.getDefault();
		Variable thisVar = this.getAnalysisContext().getThisVariable();
		
		if (thisVar != null) {
			Set<ObjectLabel> thisAliases = retriever.getStartingAliases(thisVar);
			AliasLE aliasLattice;
			
			if (thisAliases.isEmpty())
				aliasLattice = AliasLE.create(new DefaultObjectLabel(thisVar.resolveType(), false));
			else
				aliasLattice = AliasLE.create(thisAliases);
			
			for (ObjectLabel label : thisAliases) {
				if (label instanceof LiteralLabel) {
					knownLiterals.put(((LiteralLabel)label).getLiteral(), label);
				}
				knownObjects.add(label);
			}
			
			entry.put(thisVar, aliasLattice);
		}
		return entry;
	}	
}
