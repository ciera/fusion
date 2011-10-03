package edu.cmu.cs.fusion;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ISourceRange;

public class ReportingUtility {
	static public void reportParseError(IResource resource, ISourceRange range, String description) {
		try {
			IMarker marker = resource.createMarker("edu.cmu.cs.fusion.fusionparseproblem");
			if (range != null) {
				marker.setAttribute(IMarker.CHAR_START, range.getOffset());
				marker.setAttribute(IMarker.CHAR_END, range.getOffset() + range.getLength());
			}
			marker.setAttribute(IMarker.MESSAGE, "[Fusion Parser]: " + description);
			marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			marker.setAttribute(IMarker.LINE_NUMBER, 1);
		}
		catch (CoreException ce) {
			Logger logger = Logger.getLogger(ReportingUtility.class.getName());
			logger.log(Level.SEVERE, "CoreException when creating marker", ce);
			
		}
	}

	public static void clearMarkers(IResource resource) throws CoreException {
		resource.deleteMarkers("edu.cmu.cs.fusion.fusionparseproblem", true, IResource.DEPTH_INFINITE);
	}

}
