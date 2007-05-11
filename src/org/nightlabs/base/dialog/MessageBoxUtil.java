package org.nightlabs.base.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.nightlabs.base.util.RCPUtil;


public class MessageBoxUtil {
	public static int openErrorMessageBox(String title, String errorMessage) {
		return openDialog(SWT.ICON_ERROR | SWT.OK, title, errorMessage);
	}
	
	public static int openQuestionMessageBox(String title, String questionMessage) {
		return openDialog(SWT.ICON_QUESTION | SWT.YES | SWT.NO, title, questionMessage);
	}
	
	private static int openDialog(int style, String title, String message) {
		MessageBox mb = new MessageBox(RCPUtil.getActiveWorkbenchShell(), style);
		mb.setText(title);
		mb.setMessage(message);
		return mb.open();
	}
}
