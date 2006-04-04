package org.nightlabs.base.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
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
		super(parent, SWT.NONE);
		button = new Button(parent, SWT.TOGGLE);
		button.setImage(SharedImages.getSharedImage(NLBasePlugin.getDefault(), InheritanceToggleButton.class));
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
