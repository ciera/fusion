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
import edu.cmu.cs.fusion.debugging.FusionCache;
import edu.cmu.cs.fusion.relationship.RelationshipContext;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */
public class RelationshipView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "edu.cmu.cs.fusion.ui.RelationshipView";

	private FusionViewer viewer;
	private FusionContent content;


	class FusionContent implements ISelectionListener {
//		Object[] rels = {};
		String topType;
		int oldStart = -1;
		RelationshipContext context;
		
		public FusionContent() {
		}
		
//		public Object[] getElements(Object parent) {
//			return rels;
//		}
		
		public RelationshipContext getContext() {
			return context;
		}
		
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if (selection instanceof ITextSelection && part instanceof IEditorPart) {
				IEditorPart editor = (IEditorPart)part;
				ITextSelection text = (ITextSelection)selection;
				int start = text.getStartLine() + 1;
				
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
//					rels = makeIntoArr(context);
					RelationshipView.this.viewer.refresh();
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
//		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer = new FusionViewer(parent);
		
//		viewer.setContentProvider(content);
//		viewer.setLabelProvider(new ViewLabelProvider());
//		viewer.setSorter(new ViewerSorter());
//		viewer.setInput(getViewSite());

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