package edu.cmu.cs.fusion.ui;

import java.util.Arrays;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;

import edu.cmu.cs.crystal.tac.model.SourceVariable;
import edu.cmu.cs.crystal.tac.model.ThisVariable;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.alias.AliasContext;
import edu.cmu.cs.fusion.relationship.RelationshipContext;
import edu.cmu.cs.fusion.ui.RelationshipView.FusionContent;

//pretty sure that to get this to work, I need some kind of ContentProvider that
//actually gives me something I know about...
public class FusionViewer extends Viewer {
	private SashForm topControl;
	private List relPane;
	private List aliasPane;
	private FusionContent content;
	
	public FusionViewer(Composite parent) {
		topControl = new SashForm(parent, SWT.HORIZONTAL);

		topControl.setLayout(new FillLayout());

		relPane = new List(topControl, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		aliasPane = new List(topControl, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		
		topControl.setWeights(new int[] {50, 50});
	}

	@Override
	public Control getControl() {
		return topControl;
	}

	@Override
	public ISelection getSelection() {
		return null;
	}

	@Override
	public void refresh() {
		Pair<AliasContext, RelationshipContext> context = content.getContext();
		String[] rels = makeIntoRelArr(context.snd());
		String[] aliases = makeIntoPointerArr(context.fst());
		
		
		relPane.setItems(rels);
		aliasPane.setItems(aliases);
		relPane.redraw();
		aliasPane.redraw();
		topControl.update();
	}

	private String[] makeIntoPointerArr(AliasContext context) {
		if (context != null) {
			String[] arr = new String[context.getVariables().size()];
			int ndx = 0;
			for (Variable var : context.getVariables()) {
				if (var instanceof ThisVariable || var instanceof SourceVariable) {
					arr[ndx] = var.getSourceString() + " -> " + context.getAliases(var);
					ndx++;
				}
			}
			return Arrays.copyOf(arr, ndx);
		}
		else
			return new String[] {};
	}

	@Override
	public void setSelection(ISelection selection, boolean reveal) {
	}

	private String[] makeIntoRelArr(RelationshipContext context) {
		
		if (context != null) {
			String[] arr = new String[context.getSize()];
			int ndx = 0;
			for (Relationship rel : context) {
				arr[ndx] = rel.toString() + " -> " + context.getRelationship(rel).toString();
				ndx++;
			}
			return arr;
		}
		else
			return new String[] {};
	}

	@Override
	public Object getInput() {
		return content;
	}

	@Override
	public void setInput(Object input) {
		this.content = (FusionContent) input;
	}
}
