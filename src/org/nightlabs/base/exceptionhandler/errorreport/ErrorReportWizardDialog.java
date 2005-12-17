/*
 * Created on 09.08.2005
 */
package org.nightlabs.base.exceptionhandler.errorreport;

import org.nightlabs.base.wizard.DynamicPathWizardDialog;

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
