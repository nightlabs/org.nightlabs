/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.base.exceptionhandler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.exceptionhandler.errorreport.ErrorReport;
import org.nightlabs.base.exceptionhandler.errorreport.ErrorReportWizardDialog;
import org.nightlabs.base.util.RCPUtil;

/**
 * When intending to create a DefaultErrorDialog to display an error message, one should use the static method
 * <code>addError</code>. If a DefaultErrorDialog of the specified type already exists, the error is added to
 * that dialog, else a new dialog is created to display the message and the dialog is opened!
 * 
 * One can also create a new instance of DefaultErrorDialog using the default constructor. Errors can be added
 * via <code>addErrorEntry</code>. The dialog can be displayed by calling <code>open</code>.
 *  
 * @author Tobias Langner
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class DefaultErrorDialog extends IconAndMessageDialog implements IErrorDialog 
{
	/**
	 * Reserve room for this many list items.
	 */
	private static final int TEXT_LINE_COUNT = 15;
	
	protected static final int SEND_ERROR_REPORT_ID = IDialogConstants.CLIENT_ID + 1;
	
	/**
	 * Whether to show the error report button
	 */
	private boolean errorReportEnabled = true;

	/**
	 * Whether to show the details button
	 */
	private boolean detailsEnabled = true;
	
	/**
	 * The title of the dialog.
	 */
	private String title;
	
	/**
	 * The stack trace text.
	 */
	private Text stackTraceText;
	
	/** 
	 * Collection of all errors currently displayed/displayable.
	 */
	private List<ErrorItem> errorList = new ArrayList<ErrorItem>();
	
	private ErrorTable errorTable;
	
	private Composite messageAreaComposite;
	
	/**
	 * Indicates whether the error details viewer is currently created.
	 */
	private boolean stackTraceTextCreated = false;
	
	public DefaultErrorDialog()
	{
		super(RCPUtil.getActiveWorkbenchShell());
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	/**
	 * @deprecated Use {@link ErrorDialogFactory#showError(Class, String, String, Throwable, Throwable)}
	 */
	public static boolean addError(Class theClass, String dialogTitle, String message, Throwable thrownException, Throwable triggerException)
	{
		return ErrorDialogFactory.showError(theClass, dialogTitle, message, thrownException, triggerException);
	}
	
	/**
	 * @deprecated Use {@link ErrorDialogFactory#showError(Class, ErrorReport)}
	 */
	public static boolean addError(Class theClass, ErrorReport errorReport)
	{
		return ErrorDialogFactory.showError(theClass, errorReport);
	}	
			
	/* (non-Javadoc)
	 * @see org.nightlabs.base.exceptionhandler.IErrorDialog#showError(java.lang.String, java.lang.String, java.lang.Throwable, java.lang.Throwable)
	 */
	public void showError(String dialogTitle, String message, Throwable thrownException, Throwable triggerException)
	{
		String exMsg = thrownException.toString();				
		this.message = (message == null || "".equals(message)) ? exMsg : message;
		//this.message = (this.message == null) ? "" : this.message;
		dialogTitle = dialogTitle == null ? "Error occured." : dialogTitle;
		setDialogTitle(dialogTitle);
		errorList.add(0, new ErrorItem(this.message, thrownException, triggerException));		
		if(errorTable != null)
			errorTable.refresh();
		
		if(errorList.size() >= 2)
		{
			((StackLayout)messageAreaComposite.getLayout()).topControl = errorTable;
			messageAreaComposite.layout(true, true);
		}
	}	
	
	/*
	 * (non-Javadoc) Method declared in Window.
	 */
	protected void configureShell(Shell shell)
	{
		super.configureShell(shell);
		shell.setText(title);
	}
	
	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected void createButtonsForButtonBar(Composite parent)
	{
		// create OK and Details buttons
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		
		if(shouldShowErrorReportButton())
			createButton(parent, SEND_ERROR_REPORT_ID, "Send error report", false);
		if(shouldShowDetailsButton())
			createButton(parent, IDialogConstants.DETAILS_ID, IDialogConstants.SHOW_DETAILS_LABEL, false);
	}
	
	/**
	 * This implementation of the <code>Dialog</code> framework method creates
	 * and lays out a composite and calls <code>createMessageArea</code> and
	 * <code>createCustomArea</code> to populate it. Subclasses should
	 * override <code>createCustomArea</code> to add contents below the
	 * message.
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		// create a composite layout with stack layout
		messageAreaComposite = new Composite(parent, SWT.NONE);
		StackLayout stackLayout = new StackLayout();
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 150;
		gd.widthHint = 550;
		messageAreaComposite.setLayoutData(gd);		
		messageAreaComposite.setLayout(stackLayout);
		messageAreaComposite.setFont(parent.getFont());
		createMessageArea(messageAreaComposite);
		createErrorTableArea(messageAreaComposite);
		stackLayout.topControl = messageLabel;		
		return messageAreaComposite;
	}
	
	protected void createErrorTableArea(Composite parent)
	{
		errorTable = new ErrorTable(parent, SWT.NONE);
		errorTable.setInput(errorList);
		errorTable.getTableViewer().addSelectionChangedListener(
				new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event)
					{
						if (!stackTraceTextCreated)
							return;
						Throwable thrown = errorTable.getSelectedItem().getThrownException();
						stackTraceText.setText(ErrorReport.getExceptionStackTraceAsString(thrown));						
					}					
				});
		messageLabel.setText(message);
	}
	
	/*
	 * @see IconAndMessageDialog#createDialogAndButtonArea(Composite)
	 */
	protected void createDialogAndButtonArea(Composite parent)
	{
		super.createDialogAndButtonArea(parent);
		if (this.dialogArea instanceof Composite) {
			//Create a label if there are no children to force a smaller layout
			Composite dialogComposite = (Composite) dialogArea;
			if (dialogComposite.getChildren().length == 0)
				new Label(dialogComposite, SWT.NULL);
		}
	}
		
	/**
	 * Create this dialog's drop-down list component.
	 * 
	 * @param parent
	 *            the parent composite
	 * @return the drop-down list component
	 */
	protected Text createDropDownText(Composite parent) 
	{
		stackTraceText = new Text(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		stackTraceText.setText(ErrorReport.getExceptionStackTraceAsString(errorTable.getSelectedItem().getThrownException()));
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL
				| GridData.GRAB_VERTICAL);
		data.heightHint = stackTraceText.getLineHeight() * TEXT_LINE_COUNT;
		data.horizontalSpan = 2;
		stackTraceText.setLayoutData(data);
		stackTraceText.setFont(parent.getFont());
		stackTraceTextCreated = true;
		return stackTraceText;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IconAndMessageDialog#getImage()
	 */
	protected Image getImage()
	{
		return getErrorImage();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int id) {
		switch(id) {
		case IDialogConstants.DETAILS_ID:
			// was the details button pressed?
			toggleDetailsArea();
			break;
		case SEND_ERROR_REPORT_ID:
			ErrorItem error = errorTable.getSelectedItem();
			ErrorReport errorReport = new ErrorReport(error.getThrownException(), error.getTriggerException());
			ErrorReportWizardDialog dlg = new ErrorReportWizardDialog(errorReport);
			okPressed();
			dlg.open();
			break;
		default:
			super.buttonPressed(id);
		}
	}
	
	protected final void showDetailsArea() 
	{
		if (!stackTraceTextCreated) {
			Control control = getContents();
			if (control != null && ! control.isDisposed())
				toggleDetailsArea();
		}
	}
		
	/**
	 * Toggles the unfolding of the details area. This is triggered by the user
	 * pressing the details button.
	 */
	private void toggleDetailsArea() {
		Point windowSize = getShell().getSize();
		Point oldSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		if (stackTraceTextCreated) {
			stackTraceText.dispose();
			stackTraceTextCreated = false;
			getButton(IDialogConstants.DETAILS_ID).setText(IDialogConstants.SHOW_DETAILS_LABEL);
		} else {
			stackTraceText = createDropDownText((Composite) getContents());
			getButton(IDialogConstants.DETAILS_ID).setText(IDialogConstants.HIDE_DETAILS_LABEL);
		}
		Point newSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		getShell()
		.setSize(
				new Point(windowSize.x, windowSize.y
						+ (newSize.y - oldSize.y)));
	}
			
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#open()
	 * @see IErrorDialog#open()
	 */
	public int open() 
	{
		ErrorDialogFactory.addDialog(this);
		return super.open();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#close()
	 */
	public boolean close() 
	{
		ErrorDialogFactory.removeDialog(this);
		errorList.clear();
		return super.close();
	}
	
	protected boolean shouldShowDetailsButton() 
	{
		return detailsEnabled;
	}
	
	public boolean shouldShowErrorReportButton()
	{
		return errorReportEnabled;
	}
	
	public void setDialogTitle(String title)
	{
		this.title = title;
	}
}
