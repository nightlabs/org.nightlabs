/*
 * Created on 09.08.2005
 */
package com.nightlabs.rcp.exceptionhandler.errorreport;

import com.nightlabs.rcp.wizard.DynamicPathWizardDialog;

public class ErrorReportWizardDialog extends DynamicPathWizardDialog
{
  public ErrorReportWizardDialog(ErrorReport errorReport)
  {
    super(new ErrorReportWizard(errorReport));
  }
  public ErrorReportWizardDialog(ErrorReportWizard errorReportWizard)
  {
    super(errorReportWizard);
  }
  public int open()
  {
    return super.open();
  }
}
