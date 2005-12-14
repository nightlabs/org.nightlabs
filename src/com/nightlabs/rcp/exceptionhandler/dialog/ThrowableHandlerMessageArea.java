/*
 * Created 	on Nov 10, 2004
 * 					by Alexander Bieber
 *
 */
package com.nightlabs.rcp.exceptionhandler.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * @author Alexander Bieber
 * @deprecated is this still in use?? see com.nightlabs.rcp.exceptionhandler.DefaultErrorDialog
 */
public class ThrowableHandlerMessageArea extends Composite {
	private Label labelErrorIcon = null;
	private Label labelErrMessage = null;
	public ThrowableHandlerMessageArea(Composite parent, int style) {
		super(parent, style);
		initialize();
	}
	private void initialize() {
		GridLayout gridLayout1 = new GridLayout();
		labelErrorIcon = new Label(this, SWT.NONE);
		labelErrMessage = new Label(this, SWT.SHADOW_IN | SWT.HORIZONTAL | SWT.WRAP);
//		labelDummy = new Label(this, SWT.NONE);
//		label2ndMessage = new Label(this, SWT.WRAP);
		labelErrorIcon.setText("");
		labelErrorIcon.setImage(Display.getCurrent().getSystemImage(SWT.ICON_ERROR));
		labelErrMessage.setText("");
		this.setLayout(gridLayout1);
		gridLayout1.numColumns = 2;
//		label2ndMessage.setText("");
//		labelDummy.setText("");
		setSize(new org.eclipse.swt.graphics.Point(297,79));
	}

	
	public void setErrMessage(String message) {
		labelErrMessage.setText(message);
	}
	
	private Throwable error = null;
	public void setThrowable(Throwable err) {
		this.error = err;
		String message = "";
		if (error != null) {
			setErrMessage(error.toString());
//			if (error.getCause() != null) {
//				set2ndMessage(error.getCause().toString());
//			}
		}
	}
	
}  //  @jve:decl-index=0:visual-constraint="24,25"
