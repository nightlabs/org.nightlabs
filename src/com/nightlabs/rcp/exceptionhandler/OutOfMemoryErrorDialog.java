/*
 * Created 	on Oct 21, 2005
 * 					by alex
 *
 */
package com.nightlabs.rcp.exceptionhandler;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.nightlabs.rcp.exceptionhandler.errorreport.ErrorReport;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class OutOfMemoryErrorDialog extends DefaultErrorDialog {
	
	private static final Logger LOGGER = Logger.getLogger(OutOfMemoryErrorDialog.class);
	
  protected static final int RESTART_WORKBENCH_ID = DefaultErrorDialog.SEND_ERROR_REPORT_ID + 10;
  
  private Button restartWorkbenchButton;
  
	/**
	 * @param parentShell
	 * @param dialogTitle
	 * @param message
	 * @param thrownException
	 * @param triggerException
	 */
	public OutOfMemoryErrorDialog(Shell parentShell, String dialogTitle,
			String message, Throwable thrownException, Throwable triggerException) {
		super(parentShell, dialogTitle, message, thrownException, triggerException);
	}

	/**
	 * @param dialogTitle
	 * @param message
	 * @param error
	 */
	public OutOfMemoryErrorDialog(String dialogTitle, String message,
			Throwable error) {
		super(dialogTitle, message, error);
	}

	/**
	 * @param error
	 */
	public OutOfMemoryErrorDialog(Throwable error) {
		super(error);
	}

	/**
	 * @param thrownException
	 * @param triggerException
	 */
	public OutOfMemoryErrorDialog(Throwable thrownException,
			Throwable triggerException) {
		super(thrownException, triggerException);
	}

	/**
	 * @param errorReport
	 */
	public OutOfMemoryErrorDialog(ErrorReport errorReport) {
		super(errorReport);
	}

	protected void createCustomButtons(Composite parent) {
		restartWorkbenchButton = createButton(parent, RESTART_WORKBENCH_ID, "Shutdown Workbench", false);
		super.createCustomButtons(parent);
	}
	
	protected void buttonPressed(int id) {
		if (id == RESTART_WORKBENCH_ID) {
			try {
				LOGGER.info("Trying to restart the workbench due to OutOfMemoryError");
				if (!PlatformUI.getWorkbench().restart()) {
					System.exit(1);
				}
				LOGGER.info("Close successful, restarting");
			} catch (Throwable t) {
				System.exit(1);
			}
		}
		else			
			super.buttonPressed(id);
	}

}
