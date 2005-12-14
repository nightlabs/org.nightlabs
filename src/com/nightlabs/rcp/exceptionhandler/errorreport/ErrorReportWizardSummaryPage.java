/*
 * Created on 24.03.2005
 */
package com.nightlabs.rcp.exceptionhandler.errorreport;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.nightlabs.base.NLBasePlugin;
import com.nightlabs.rcp.composite.XComposite;
import com.nightlabs.rcp.wizard.DynamicPathWizardPage;

/**
 * @author Simon Lehmann - simon@nightlabs.de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ErrorReportWizardSummaryPage extends DynamicPathWizardPage
{
  protected Text overviewText;
  
	public ErrorReportWizardSummaryPage()
	{
		super(ErrorReportWizardCommentPage.class.getName(), NLBasePlugin.getResourceString("errorreport.summarypage.title")); //$NON-NLS-1$
		setDescription(NLBasePlugin.getResourceString("errorreport.summarypage.description")); //$NON-NLS-1$
	}

	/**
	 * @see com.nightlabs.rcp.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	public Control createPageContents(Composite parent)
	{
    XComposite page = new XComposite(parent, SWT.NONE, XComposite.LAYOUT_MODE_TIGHT_WRAPPER);
    page.setLayoutData(new GridData(GridData.FILL_BOTH));
    
    Label label = new Label(page, SWT.NONE);
    label.setText(NLBasePlugin.getResourceString("errorreport.summarypage.overviewlabel")); //$NON-NLS-1$
    
    overviewText = new Text(page, SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
    overviewText.setLayoutData(new GridData(GridData.FILL_BOTH));    

    return page;
	}
  
  public void onShow()
  {
    setOverviewText();
  }

  protected void setOverviewText()
  {
    ErrorReportWizard wizard = (ErrorReportWizard) getWizard();
    final ErrorReport errorReport = wizard.getErrorReport();
  
    StringBuffer reportData = new StringBuffer();
    reportData.append(NLBasePlugin.getResourceString("errorreport.summarypage.datetime")); //$NON-NLS-1$
    reportData.append(ErrorReport.getTimeAsString(errorReport.getTime()));
    reportData.append(NLBasePlugin.getResourceString("errorreport.summarypage.breakafter")); //$NON-NLS-1$
    if(errorReport.getUserComment() != null && errorReport.getUserComment().length() != 0) {
      reportData.append(NLBasePlugin.getResourceString("errorreport.summarypage.usercommentlabel")); //$NON-NLS-1$
      reportData.append(errorReport.getUserComment());
      reportData.append(NLBasePlugin.getResourceString("errorreport.summarypage.breakafter")); //$NON-NLS-1$
    }
    reportData.append(NLBasePlugin.getResourceString("errorreport.summarypage.stacktracelabel")); //$NON-NLS-1$
    reportData.append(ErrorReport.getExceptionStackTraceAsString(errorReport.getThrownException()));
    reportData.append(NLBasePlugin.getResourceString("errorreport.summarypage.breakafter")); //$NON-NLS-1$
    reportData.append(NLBasePlugin.getResourceString("errorreport.summarypage.triggerstacktracelabel")); //$NON-NLS-1$
    reportData.append(ErrorReport.getExceptionStackTraceAsString(errorReport.getThrownException()));
    reportData.append(NLBasePlugin.getResourceString("errorreport.summarypage.breakafter")); //$NON-NLS-1$
    reportData.append(NLBasePlugin.getResourceString("errorreport.summarypage.systempropertieslabel")); //$NON-NLS-1$
    reportData.append(errorReport.getSystemProperties());
    reportData.append(NLBasePlugin.getResourceString("errorreport.summarypage.breakafter")); //$NON-NLS-1$
    
    overviewText.setText(reportData.toString());
  }
}
