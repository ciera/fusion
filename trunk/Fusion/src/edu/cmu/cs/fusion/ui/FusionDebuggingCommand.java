package edu.cmu.cs.fusion.ui;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.cmu.cs.crystal.IAnalysisReporter;
import edu.cmu.cs.crystal.IRunCrystalCommand;
import edu.cmu.cs.crystal.internal.NullPrintWriter;

public class FusionDebuggingCommand implements IRunCrystalCommand {
	private List<ITypeRoot> units;
	static private Set<String> analyses;
	static private IAnalysisReporter nullReporter = new IAnalysisReporter() {
		public void clearMarkersForCompUnit(ITypeRoot compUnit) {}

		public PrintWriter debugOut() {
			return NullPrintWriter.instance();
		}

		public PrintWriter userOut() {
			return NullPrintWriter.instance();
		}

		public void reportUserProblem(String p, ASTNode n, String a) {
		}

		public void reportUserProblem(String p, ASTNode n, String a, SEVERITY v) {
		}
	};
	
	static {
		analyses = new HashSet<String>();
		analyses.add("FusionDebugger");		
	}
	
	public FusionDebuggingCommand(ITypeRoot unit) {
		units = new LinkedList<ITypeRoot>();
		units.add(unit);
	}
	
	public Set<String> analyses() {
		return analyses;
	}

	public List<ITypeRoot> compilationUnits() {
		return units;
	}

	public IAnalysisReporter reporter() {
		return nullReporter;
	}

}
