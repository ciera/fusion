package edu.cmu.cs.fusion.ui;


import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import edu.cmu.cs.crystal.IRunCrystalCommand;
import edu.cmu.cs.crystal.internal.AbstractCrystalPlugin;
import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.fusion.alias.AliasContext;
import edu.cmu.cs.fusion.debugging.FusionCache;
import edu.cmu.cs.fusion.relationship.RelationshipContext;



public class RelationshipView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "edu.cmu.cs.fusion.ui.RelationshipView";

	private FusionViewer viewer;
	private FusionContent content;


	class FusionContent implements ISelectionListener {
		String topType;
		int oldStart = -1;
		Pair<? extends AliasContext, RelationshipContext> context;
		
		public FusionContent() {
		}
		
		public Pair<? extends AliasContext, RelationshipContext> getContext() {
			return context;
		}
		
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if (selection instanceof ITextSelection && part instanceof IEditorPart) {
				IEditorPart editor = (IEditorPart)part;
				ITextSelection text = (ITextSelection)selection;
				int start = text.getStartLine();
				
				if (start == oldStart)
					return;
				oldStart = start;
				
				ITypeRoot root = JavaUI.getEditorInputTypeRoot(editor.getEditorInput());

				if (root != null && root.getElementType() == IJavaElement.COMPILATION_UNIT) {
					String type = root.findPrimaryType().getFullyQualifiedName();
					if (!(type.equals(topType))) {
						topType = type;
						IRunCrystalCommand command = new FusionDebuggingCommand((ICompilationUnit)root);
						AbstractCrystalPlugin.getCrystalInstance().runAnalyses(command, null);
					}
					FusionCache cache = FusionCache.getCache();
					context = cache.getResults((ICompilationUnit)root, oldStart);
					RelationshipView.this.viewer.refresh();
				}
				else {
					context = null;
				}
			}
		}
	}
	
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		public Image getColumnImage(Object obj, int index) {
			return null;
		}
	}

	public void createPartControl(Composite parent) {
		content = new FusionContent();
		viewer = new FusionViewer(parent);
		viewer.setInput(content);
		
		this.getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(content);
	}


	@Override
	public void dispose() {
		this.getSite().getWorkbenchWindow().getSelectionService().removePostSelectionListener(content);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}