/*
 * Created on 04.08.2005
 */
package org.nightlabs.rcp.exceptionhandler;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.nightlabs.rcp.exceptionhandler.errorreport.ErrorReport;
import org.nightlabs.rcp.exceptionhandler.errorreport.ErrorReportWizardDialog;
import org.nightlabs.rcp.util.RCPUtil;

public class DefaultErrorDialog extends IconAndMessageDialog {
  /**
   * Reserve room for this many list items.
   */
  private static final int TEXT_LINE_COUNT = 15;

  /**
   * The nesting indent.
   */
  private static final String NESTING_INDENT = "  "; //$NON-NLS-1$

  protected static final int SEND_ERROR_REPORT_ID = IDialogConstants.CLIENT_ID + 1;
  
  /**
   * The Details button.
   */
  private Button detailsButton;

  /**
   * Whether to show the error report button
   */
  private boolean errorReportEnabled = true;
  
  /**
   * The error report button
   */
  private Button sendErrorReportButton;
  
  /**
   * The title of the dialog.
   */
  private String title;

  /**
   * The stack trace text.
   */
  private Text stackTraceText;

  Throwable thrownException;
  Throwable triggerException;
  
  /**
   * Indicates whether the error details viewer is currently created.
   */
  private boolean stackTraceTextCreated = false;

  /**
   * Creates an error dialog. Note that the dialog will have no visual
   * representation (no widgets) until it is told to open.
   * <p>
   * Normally one should use <code>openError</code> to create and open one
   * of these. This constructor is useful only if the error object being
   * displayed contains child items <it>and </it> you need to specify a mask
   * which will be used to filter the displaying of these children.
   * </p>
   * 
   * @param parentShell
   *            the shell under which to create this dialog
   * @param dialogTitle
   *            the title to use for this dialog, or <code>null</code> to
   *            indicate that the default title should be used
   * @param message
   *            the message to show in this dialog, or <code>null</code> to
   *            indicate that the error's message should be shown as the
   *            primary message
   * @param status
   *            the error to show to the user
   * @param displayMask
   *            the mask to use to filter the displaying of child items, as
   *            per <code>IStatus.matches</code>
   * @see org.eclipse.core.runtime.IStatus#matches(int)
   */
  public DefaultErrorDialog(Shell parentShell, String dialogTitle, String message, Throwable thrownException, Throwable triggerException) 
  {
      super(parentShell);
      this.title = dialogTitle == null ? JFaceResources
              .getString("Problem_Occurred") : //$NON-NLS-1$
              dialogTitle;
      String errorMessage = thrownException.getMessage() != null ? thrownException.getMessage()
                                                       : thrownException.getClass().getName();
      this.message = message == null ? errorMessage
              : JFaceResources
                      .format(
                              "Reason", new Object[] { message, errorMessage }); //$NON-NLS-1$
      this.thrownException = thrownException;
      this.triggerException = triggerException == null ? thrownException : triggerException;
      setShellStyle(getShellStyle() | SWT.RESIZE);
  }

  public DefaultErrorDialog(String dialogTitle, String message, Throwable error)
  {
    this(RCPUtil.getWorkbenchShell(), dialogTitle, message, error, null);
  }

  public DefaultErrorDialog(Throwable error)
  {
    this(RCPUtil.getWorkbenchShell(), null, null, error, null);
  }
  
  public DefaultErrorDialog(Throwable thrownException, Throwable triggerException)
  {
    this(RCPUtil.getWorkbenchShell(), null, null, thrownException, triggerException);
  }

  public DefaultErrorDialog(Throwable thrownException, Throwable triggerException, String message)
  {
    this(RCPUtil.getWorkbenchShell(), null, message, thrownException, triggerException);
  }

  public DefaultErrorDialog(ErrorReport errorReport)
  {
    this(RCPUtil.getWorkbenchShell(), null, null, errorReport.getThrownException(), errorReport.getTriggerException());
  }

  /*
   * (non-Javadoc) Method declared on Dialog. Handles the pressing of the Ok
   * or Details button in this dialog. If the Ok button was pressed then close
   * this dialog. If the Details button was pressed then toggle the displaying
   * of the error details area. Note that the Details button will only be
   * visible if the error being displayed specifies child details.
   */
  protected void buttonPressed(int id) {
    switch(id) {
      case IDialogConstants.DETAILS_ID:
        // was the details button pressed?
        toggleDetailsArea();
        break;
      case SEND_ERROR_REPORT_ID:
        ErrorReport errorReport = new ErrorReport(thrownException, triggerException);
        ErrorReportWizardDialog dlg = new ErrorReportWizardDialog(errorReport);
        okPressed();
        dlg.open();
        break;
      default:
        super.buttonPressed(id);
    }
  }

  /*
   * (non-Javadoc) Method declared in Window.
   */
  protected void configureShell(Shell shell) {
      super.configureShell(shell);
      shell.setText(title);
  }

  /*
   * (non-Javadoc) Method declared on Dialog.
   */
  protected void createButtonsForButtonBar(Composite parent) {
      // create OK and Details buttons
      createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
      
      createCustomButtons(parent);
      
      if (shouldShowDetailsButton()) {
          detailsButton = createButton(parent, IDialogConstants.DETAILS_ID, IDialogConstants.SHOW_DETAILS_LABEL, false);
      }
  }
  
  protected void createCustomButtons(Composite parent) {
  	if(isErrorReportEnabled())
  		sendErrorReportButton = createButton(parent, SEND_ERROR_REPORT_ID, "Send error report", false);
  }

  /**
   * This implementation of the <code>Dialog</code> framework method creates
   * and lays out a composite and calls <code>createMessageArea</code> and
   * <code>createCustomArea</code> to populate it. Subclasses should
   * override <code>createCustomArea</code> to add contents below the
   * message.
   */
  protected Control createDialogArea(Composite parent) {
      createMessageArea(parent);
      // create a composite with standard margins and spacing
      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
      layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
      layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
      layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
      layout.numColumns = 2;
      composite.setLayout(layout);
      GridData childData = new GridData(GridData.FILL_BOTH);
      childData.horizontalSpan = 2;
      composite.setLayoutData(childData);
      composite.setFont(parent.getFont());
      return composite;
  }

  /*
   * @see IconAndMessageDialog#createDialogAndButtonArea(Composite)
   */
  protected void createDialogAndButtonArea(Composite parent) {
      super.createDialogAndButtonArea(parent);
      if (this.dialogArea instanceof Composite) {
          //Create a label if there are no children to force a smaller layout
          Composite dialogComposite = (Composite) dialogArea;
          if (dialogComposite.getChildren().length == 0)
              new Label(dialogComposite, SWT.NULL);
      }
  }

  /*
   *  (non-Javadoc)
   * @see org.eclipse.jface.dialogs.IconAndMessageDialog#getImage()
   */
  protected Image getImage() {
      return getErrorImage();
  }

  /**
   * Create this dialog's drop-down list component.
   * 
   * @param parent
   *            the parent composite
   * @return the drop-down list component
   */
  protected Text createDropDownText(Composite parent) {
    stackTraceText = new Text(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
    stackTraceText.setText(ErrorReport.getExceptionStackTraceAsString(thrownException));
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


  /*
   * (non-Javadoc) Method declared on Window.
   */
  /**
   * Extends <code>Window.open()</code>. Opens an error dialog to display
   * the error. If you specified a mask to filter the displaying of these
   * children, the error dialog will only be displayed if there is at least
   * one child status matching the mask.
   */
  public int open() {
      return super.open();
  }

  public static int openError(Throwable thrownException) {
      DefaultErrorDialog dialog = new DefaultErrorDialog(thrownException, null);
      return dialog.open();
  }

  public static int openError(Throwable thrownException, Throwable triggerException) {
    DefaultErrorDialog dialog = new DefaultErrorDialog(thrownException, triggerException);
    return dialog.open();
  }

  public static int openError(ErrorReport errorReport) {
    DefaultErrorDialog dialog = new DefaultErrorDialog(errorReport);
    return dialog.open();
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
        detailsButton.setText(IDialogConstants.SHOW_DETAILS_LABEL);
      } else {
        stackTraceText = createDropDownText((Composite) getContents());
        detailsButton.setText(IDialogConstants.HIDE_DETAILS_LABEL);
      }
      Point newSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
      getShell()
              .setSize(
                      new Point(windowSize.x, windowSize.y
                              + (newSize.y - oldSize.y)));
  }

  /**
   * Put the details of the status of the error onto the stream.
   * 
   * @param buildingStatus
   * @param buffer
   * @param nesting
   */
  private void populateCopyBuffer(IStatus buildingStatus,
          StringBuffer buffer, int nesting) {
      for (int i = 0; i < nesting; i++) {
          buffer.append(NESTING_INDENT); //$NON-NLS-1$
      }
      buffer.append(buildingStatus.getMessage());
      buffer.append("\n"); //$NON-NLS-1$
      
      // Look for a nested core exception
      Throwable t = buildingStatus.getException();
      if (t instanceof CoreException) {
          CoreException ce = (CoreException)t;
          populateCopyBuffer(ce.getStatus(), buffer, nesting + 1);
      }
      
      IStatus[] children = buildingStatus.getChildren();
      for (int i = 0; i < children.length; i++) {
          populateCopyBuffer(children[i], buffer, nesting + 1);
      }
  }

  /**
   * The current clipboard. To be disposed when closing the dialog.
   */
  private Clipboard clipboard;

  /**
   * Copy the contents of the statuses to the clipboard.
   */
  private void copyToClipboard() {
      if (clipboard != null)
          clipboard.dispose();
      StringBuffer statusBuffer = new StringBuffer();
      //populateCopyBuffer(status, statusBuffer, 0);
      clipboard = new Clipboard(stackTraceText.getDisplay());
      clipboard.setContents(new Object[] { statusBuffer.toString() },
              new Transfer[] { TextTransfer.getInstance() });
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.window.Window#close()
   */
  public boolean close() {
      if (clipboard != null)
          clipboard.dispose();
      return super.close();
  }
  
  /**
   * Show the details portion of the dialog if it is not already visible.
   * This method will only work when it is invoked after the control of the dialog
   * has been set. In other words, after the <code>createContents</code> method
   * has been invoked and has returned the control for the content area of the dialog.
   * Invoking the method before the content area has been set or after the dialog has been
   * disposed will have no effect.
   * @since 3.1
   */
  protected final void showDetailsArea() {
      if (!stackTraceTextCreated) {
          Control control = getContents();
          if (control != null && ! control.isDisposed())
              toggleDetailsArea();
      }
  }
  
  /**
   * Return whether the Details button should be included.
   * This method is invoked once when the dialog is built.
   * By default, the Details button is only included if
   * the status used when creating the dialog was a multi-status
   * or if the status contains an exception that is a CoreException.
   * Subclasses may override.
   * @return whether the Details button should be included
   * @since 3.1
   */
  protected boolean shouldShowDetailsButton() {
    return true;
  }

	public boolean isErrorReportEnabled()
	{
		return errorReportEnabled;
	}

	public void setErrorReportEnabled(boolean errorReportEnabled)
	{
		this.errorReportEnabled = errorReportEnabled;
	}
}
