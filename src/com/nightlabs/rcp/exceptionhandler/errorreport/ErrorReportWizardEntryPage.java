/*
 * Created on 23.03.2005
 */
package com.nightlabs.rcp.exceptionhandler.errorreport;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;

import com.nightlabs.base.NLBasePlugin;
import com.nightlabs.rcp.wizard.DynamicPathWizardPage;

/**
 * @author Simon Lehmann - simon@nightlabs.de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ErrorReportWizardEntryPage extends DynamicPathWizardPage
{
	public ErrorReportWizardEntryPage()
	{
		super(ErrorReportWizardEntryPage.class.getName(), NLBasePlugin.getResourceString("errorreport.entrypage.title")); //$NON-NLS-1$
	}
  
	/**
	 * @see com.nightlabs.rcp.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	public Control createPageContents(Composite parent)
	{
    setDescription(NLBasePlugin.getResourceString("errorreport.entrypage.description")); //$NON-NLS-1$
    Label text = new Label(parent, SWT.WRAP);
    text.setText(NLBasePlugin.getResourceString("errorreport.entrypage.welcometext")); //$NON-NLS-1$
		return text;
	}
  
  public void onShow()
  {
    super.onShow();
    getShell().setSize(new Point(600, 500));
  }
}
