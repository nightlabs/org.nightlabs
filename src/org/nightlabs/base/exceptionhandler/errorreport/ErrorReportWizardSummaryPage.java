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

package org.nightlabs.base.exceptionhandler.errorreport;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.composite.XComposite.LayoutMode;
import org.nightlabs.base.wizard.DynamicPathWizardPage;

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
	 * @see org.nightlabs.base.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	public Control createPageContents(Composite parent)
	{
    XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
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
