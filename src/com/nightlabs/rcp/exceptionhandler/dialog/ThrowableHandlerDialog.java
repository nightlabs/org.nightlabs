/*
 * Created 	on Nov 10, 2004
 * 					by Alexander Bieber
 *
 */
package com.nightlabs.rcp.exceptionhandler.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.nightlabs.base.NLBasePlugin;
import com.nightlabs.rcp.dialog.ExpandableAreaDialog;

/**
 * @author Alexander Bieber
 * @deprecated is this still in use?? see com.nightlabs.rcp.exceptionhandler.DefaultErrorDialog
 */
public class ThrowableHandlerDialog extends ExpandableAreaDialog {

	private Throwable error = null;
	public ThrowableHandlerDialog(Shell parent, Throwable error) {
		super(parent, NLBasePlugin.getResourceString("extensionpoint.throwable.dialog.title"), NLBasePlugin.getResourceString("extensionpoint.throwable.dialog.expandText"));
		this.error = error;
	}
	
	
	protected Composite createStaticArea(Composite parent) {
		ThrowableHandlerMessageArea messageArea = new ThrowableHandlerMessageArea(parent,SWT.NONE);
		messageArea.setThrowable(error);
		return messageArea;
	}
	
	
	protected Composite createExpandableArea(Composite parent) {
		ThrowableHandlerDetailArea detailArea = new ThrowableHandlerDetailArea(parent,SWT.NONE);
		detailArea.setThrowable(error);
		return detailArea;
	}
}
