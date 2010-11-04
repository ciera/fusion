package edu.cmu.cs.fusion.debugging;

import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;

import edu.cmu.cs.crystal.IRunCrystalCommand;
import edu.cmu.cs.crystal.internal.AbstractCrystalPlugin;
import edu.cmu.cs.fusion.ui.FusionDebuggingCommand;

public class FusionCache {
	static private FusionCache THE_CACHE = new FusionCache();
	static public FusionCache getCache() {return THE_CACHE;}
	
	private ICompilationUnit compUnit;
	private SortedMap<Integer, DebugInfo> cache = new TreeMap<Integer, DebugInfo>();
	
	public void makeCacheValid(ICompilationUnit root) {
		if (compUnit == null || !compUnit.equals(root)) {
			final ICompilationUnit unit = compUnit = root;
			Job j = new Job("Fusion Debugger") {
				
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					IRunCrystalCommand command = new FusionDebuggingCommand(unit);
					
					AbstractCrystalPlugin.getCrystalInstance().runAnalyses(command, monitor);
					if (monitor.isCanceled())
						return Status.CANCEL_STATUS;
					return Status.OK_STATUS;
				}
			};
			j.setUser(true);
			j.schedule();
		}
	}
	
	public DebugInfo getResults(ICompilationUnit unit, int lineNum) {
		assert(compUnit.equals(unit));
		return cache.get(lineNum);
	}
	
	public void setCompUnit(ICompilationUnit compUnit) {
		this.compUnit = compUnit;
		cache.clear();
	}

	public void addResult(int startLine, int endLine, DebugInfo info) {
		cache.put(startLine, info);
	}

}
