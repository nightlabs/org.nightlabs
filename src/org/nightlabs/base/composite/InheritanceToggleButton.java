package org.nightlabs.base.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.resource.SharedImages;

public class InheritanceToggleButton
extends XComposite
{
	private Button button;
	private Label captionLabel;

	public InheritanceToggleButton(Composite parent)
	{
		this(parent, null);
	}

	public InheritanceToggleButton(Composite parent, String caption)
	{
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		getGridData().grabExcessHorizontalSpace = false;
		getGridData().grabExcessVerticalSpace = false;
		button = new Button(this, SWT.TOGGLE);
		button.setToolTipText("Inherit?");
		button.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				if (button.getSelection())
					button.setImage(SharedImages.getSharedImage(NLBasePlugin.getDefault(), InheritanceToggleButton.class));
				else
					button.setImage(SharedImages.getSharedImage(NLBasePlugin.getDefault(), InheritanceToggleButton.class, "Unlink"));
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}		
		});
		if (caption != null) {
			captionLabel = new Label(this, SWT.NONE);
			captionLabel.setText(caption);
			++getGridLayout().numColumns;
		}
		setSelection(true);
	}

	/**
	 * This method is a noop, if <code>null</code> was passed to the constructor
	 * {@link #InheritanceToggleButton(Composite, String)} as <code>caption</code>.
	 *
	 * @param caption
	 */
	public void setCaption(String caption) {
		if (captionLabel == null)
			return;

		captionLabel.setText(caption);
		layout(true);
	}

	public String getCaption() {
		if (captionLabel != null)
			return captionLabel.getText();

		return null;
	}

	public void addSelectionListener(SelectionListener selectionListener)
	{
		button.addSelectionListener(selectionListener);
	}

	public boolean getSelection()
	{
		return button.getSelection();
	}

	public void setSelection(boolean selection)
	{
		button.setSelection(selection);
		if (selection)
			button.setImage(SharedImages.getSharedImage(NLBasePlugin.getDefault(), InheritanceToggleButton.class));
		else
			button.setImage(SharedImages.getSharedImage(NLBasePlugin.getDefault(), InheritanceToggleButton.class, "Unlink"));
	}

}
