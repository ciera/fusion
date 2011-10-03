package edu.cmu.cs.fusion.ui;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import edu.cmu.cs.crystal.tac.model.SourceVariable;
import edu.cmu.cs.crystal.tac.model.ThisVariable;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.fusion.Relationship;
import edu.cmu.cs.fusion.alias.AliasContext;
import edu.cmu.cs.fusion.alias.ObjectLabel;
import edu.cmu.cs.fusion.debugging.DebugInfo;
import edu.cmu.cs.fusion.relationship.RelationshipContext;
import edu.cmu.cs.fusion.ui.RelationshipView.FusionContent;

public class FusionViewer extends Viewer {
	private Composite topControl;
	private Label statementLabel;
	private List relPane;
	private List aliasPane;
	private List objPane;
	private FusionContent content;
	private boolean fullyQualified;
	private Text relRegexFilterText;

	public FusionViewer(Composite parent) {
		topControl = new SashForm(parent, SWT.VERTICAL);
		topControl.setLayout(new FillLayout());

		SashForm resultsPane = new SashForm(topControl, SWT.HORIZONTAL);
		resultsPane.setLayout(new FillLayout());

		relPane = new List(resultsPane, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		SashForm rightPane = new SashForm(resultsPane, SWT.VERTICAL);
		rightPane.setLayout(new FillLayout());

		SashForm rightTop = new SashForm(rightPane, SWT.VERTICAL);
		rightTop.setLayout(new RowLayout());

		statementLabel = new Label(rightTop, SWT.SINGLE);
		statementLabel.setText("-");

		SashForm rightTopBot = new SashForm(rightTop, SWT.HORIZONTAL);
		rightTopBot.setLayout(new RowLayout());

		SashForm rightBot = new SashForm(rightPane, SWT.HORIZONTAL);
		rightBot.setLayout(new FillLayout());

		aliasPane = new List(rightBot, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		objPane = new List(rightBot, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);

		relRegexFilterText = new Text(rightTopBot, SWT.BORDER);
		relRegexFilterText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				refresh();
			}
		});

		Button qualifiedCheckBox = new Button(rightTopBot, SWT.CHECK);
		qualifiedCheckBox.setText("Show fully qualified names");
		qualifiedCheckBox.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fullyQualified = !fullyQualified;
				refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		fullyQualified = true;
		resultsPane.setWeights(new int[] { 50, 50 });
		rightPane.setWeights(new int[] { 25, 75 });
		rightBot.setWeights(new int[] { 25, 75 });
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
		DebugInfo info = content.getInfo();

		String[] rels = info != null ? makeIntoRelArr(info.getRels()) : new String[] {};
		String[] aliases = info != null ? makeIntoPointerArr(info.getAliases()) : new String[] {};
		String[] labels = info != null ? makeIntoObjLabelArr(info.getAliases()) : new String[] {};
		String statement = info != null ? info.getStatement() : "-";

		relPane.setItems(rels);
		objPane.setItems(labels);
		aliasPane.setItems(aliases);
		statementLabel.setText(statement);

		relPane.redraw();
		aliasPane.redraw();
		objPane.redraw();
		statementLabel.redraw();
		topControl.update();
	}

	private String[] makeIntoObjLabelArr(AliasContext context) {
		if (context != null) {
			Set<ObjectLabel> labels = context.getAllAliases();
			String[] arr = new String[labels.size()];
			int ndx = 0;
			for (ObjectLabel lab : labels) {
				arr[ndx] = lab.toString() + " : " + lab.getTypeName();
				ndx++;
			}
			return arr;
		}
		else
			return new String[] {};
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
			Pattern filter = null;
			try {
				filter = Pattern.compile(relRegexFilterText.getText(), Pattern.CASE_INSENSITIVE);
			} catch (PatternSyntaxException e) {
				filter = Pattern.compile("");
			}
			Matcher matcher = filter.matcher("");
			for (Relationship rel : context) {
				String toMatch = rel.toString(fullyQualified);
				matcher.reset(toMatch);
				if (matcher.lookingAt()) {
					arr[ndx] = toMatch + " -> " + context.getRelationship(rel).toString();
					ndx++;
				}
			}
			// ndx == actual size
			if (ndx != arr.length) {
				arr = Arrays.copyOf(arr, ndx);
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
