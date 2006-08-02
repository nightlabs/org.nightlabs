package org.nightlabs.base.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


/**
 * Composite for displaying a checkbox with a describing text.
 * 
 * @author Tobias Langner <tobias[DOT]langner[AT]nightlabs[DOT]de>
 */
public class LabeledCheckboxComposite extends XComposite
{
	private Button checkBox;
	private Label label;

	public LabeledCheckboxComposite(Composite parent, int style, boolean setLayoutData)
	{
		super(parent, style, LayoutMode.LEFT_RIGHT_WRAPPER, setLayoutData ? LayoutDataMode.GRID_DATA : LayoutDataMode.NONE, 2);
		label = new Label(this, SWT.NONE);
		GridLayout layout = (GridLayout)this.getLayout();
		layout.horizontalSpacing = 15;
		checkBox = new Button(this, SWT.CHECK);
	}

	public Button getCheckbox()
	{
		return checkBox;
	}

	public Label getLabel()
	{
		return label;		
	}		
}
