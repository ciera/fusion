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

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

import edu.cmu.cs.crystal.internal.ShortFormatter;
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
	static private Logger core;
	static private Logger warn;
	static private Logger checks;
	static private boolean setLoggers;
	
	public SharedAnalysisData() {
		//set up the base logger to actually log stuff. Otherwise, Eclipse decides that in run mode,
		//it's just going to ignore all logging requests.
//		Logger base = Logger.getLogger(FusionAnalysis.BASE_FUSION_LOGGER);
//		base.setLevel(Level.ALL);
//		base.getParent().setLevel(Level.ALL);
		
		if (!setLoggers) {
			Formatter simpleFormatter = new Formatter() {
				public String format(LogRecord record) {
					return record.getMessage();
				}
			};

			//The core logger. Should be at warnings only under most circumstances
			core = Logger.getLogger(FusionAnalysis.FUSION_LOGGER);
			core.setLevel(Level.WARNING);
			Handler handler = new ConsoleHandler();
			handler.setFormatter(new ShortFormatter());
			core.addHandler(handler);
			core.setUseParentHandlers(false);

			//The reports logger. Set to info if we want to generate reports.
			warn = Logger.getLogger(FusionAnalysis.REPORTS_LOGGER);
			warn.setLevel(Level.INFO);
			
			try {
				handler = new FileHandler("%h/fusion_warnings.txt");
				handler.setFormatter(simpleFormatter);
				warn.addHandler(handler);
				warn.setUseParentHandlers(false);
			} catch (SecurityException e) {
				core.log(Level.WARNING, "Could not create warnings handler", e.getMessage());
			} catch (IOException e) {
				core.log(Level.WARNING, "Could not create warnings handler", e.getMessage());
			}

			checks = Logger.getLogger(FusionAnalysis.CHECKS_LOGGER);
			checks.setLevel(Level.INFO);
			
			try {
				handler = new FileHandler("%h/fusion_checks.txt");
				handler.setFormatter(simpleFormatter);
				checks.addHandler(handler);
				checks.setUseParentHandlers(false);
			} catch (SecurityException e) {
				core.log(Level.WARNING, "Could not create checks handler", e.getMessage());
			} catch (IOException e) {
				core.log(Level.WARNING, "Could not create checks handler", e.getMessage());
			}
			setLoggers = true;
		}
}
	
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
