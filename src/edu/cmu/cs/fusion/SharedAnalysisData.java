/**
 * Copyright (c) 2006-2009 Marwan Abi-Antoun, Jonathan Aldrich, Nels E. Beckman,    
 * Kevin Bierhoff, David Dickey, Ciera Jaspan, Thomas LaToza, Gabriel Zenarosa, and others.
 *
 * This file is part of Crystal.
 *
 * Crystal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Crystal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Crystal.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.cmu.cs.fusion;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.crystal.util.typehierarchy.CachedTypeHierarchy;
import edu.cmu.cs.fusion.constraint.FreeVars;

/**
 * @author ciera
 * @since Crystal 3.4.1
 */
public class SharedAnalysisData {
	static private TypeHierarchy hierarchy;
	static private IJavaProject project;
	
	public void checkForProjectReset(IJavaProject newProject, IProgressMonitor monitor) throws JavaModelException {
		if (project == null || !project.equals(newProject)) {
			//we have a new project. reset the type hierarchy
			if (monitor != null)
				monitor.subTask("Creating new type hierarchy");
			project = newProject;
			hierarchy = new CachedTypeHierarchy(project);
			FreeVars.setHierarchy(hierarchy);
		}
	}
	
	public TypeHierarchy getHierarchy() {
		return hierarchy;
	}

	public boolean existsCommonSubtype(String t1, String t2) {
		return hierarchy.existsCommonSubtype(t1, t2);
	}

	public boolean existsCommonSubtype(String t1, String t2,
			boolean skipCheck1, boolean skipCheck2) {
		return hierarchy.existsCommonSubtype(t1, t2, skipCheck1, skipCheck2);
	}

	public boolean isSubtypeCompatible(String subType, String superType) {
		return hierarchy.isSubtypeCompatible(subType, superType);
	}

}
