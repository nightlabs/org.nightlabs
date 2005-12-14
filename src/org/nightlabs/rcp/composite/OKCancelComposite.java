/*
 * Created 	on Mar 12, 2005
 * 					by alex
 *
 */
package org.nightlabs.rcp.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public abstract class OKCancelComposite extends XComposite {

	private Button okButton;
	private Button cancelButton;
	private Label label;

	/**
	 * @param parent
	 * @param style
	 * @param setLayoutData
	 */
	public OKCancelComposite(Composite parent, int style, String text, String okTxt, String cancelTxt) {
		super(parent, style, XComposite.LAYOUT_MODE_ORDINARY_WRAPPER);
		if ((text != null) && (!"".equals(text))) {
			label = new Label(this, SWT.WRAP);
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING));
			label.setText(text);
		}
		XComposite buttonWrapper = new XComposite(this, SWT.NONE, XComposite.LAYOUT_MODE_TIGHT_WRAPPER);
		buttonWrapper.getGridLayout().numColumns = 2;
		okButton = new Button(buttonWrapper, SWT.PUSH);
		GridData okGD = new GridData();
		okGD.grabExcessHorizontalSpace = false;
		okGD.horizontalAlignment = GridData.END;
		okButton.setLayoutData(okGD);
		okButton.setText(okTxt);
		okButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				okPressed();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				okPressed();
			}
		});

		cancelButton = new Button(buttonWrapper, SWT.PUSH);
		cancelButton.setLayoutData(new GridData());
		cancelButton.setText(cancelTxt);
		cancelButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				cancelPressed();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				cancelPressed();
			}
		});
	}
	
	protected abstract void okPressed();
	
	protected abstract void cancelPressed();
	
	

}
