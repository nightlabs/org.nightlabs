package org.nightlabs.base.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.resource.SharedImages;

public class InheritanceToggleButton
		extends XComposite
{
	private Button button;

	public InheritanceToggleButton(Composite parent)
	{
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		getGridData().grabExcessHorizontalSpace = false;
		getGridData().grabExcessVerticalSpace = false;
		button = new Button(this, SWT.TOGGLE);
		button.setToolTipText("Inherit?");
		button.setImage(SharedImages.getSharedImage(NLBasePlugin.getDefault(), InheritanceToggleButton.class));
		GridData gd = new GridData();
//		gd.widthHint = 80;
		gd.heightHint = 24;
		button.setLayoutData(gd);
//		button.setSize(8, 80);
//		button.setLayoutData(null);
//		button.getBounds().height = 8;
//		button.getBounds().width = 80;
	}

	public void addSelectionListener(SelectionListener arg0)
	{
		button.addSelectionListener(arg0);
	}

	public boolean getSelection()
	{
		return button.getSelection();
	}

	public void setSelection(boolean arg0)
	{
		button.setSelection(arg0);
	}

}
