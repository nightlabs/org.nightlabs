/*
 * Created on 24.03.2005
 */
package org.nightlabs.rcp.exceptionhandler.errorreport;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.rcp.composite.XComposite;
import org.nightlabs.rcp.wizard.DynamicPathWizard;
import org.nightlabs.rcp.wizard.DynamicPathWizardPage;

/**
 * @author Simon Lehmann - simon@nightlabs.de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ErrorReportWizardCommentPage extends DynamicPathWizardPage
{
	private Text textUserComment;
	
	/**
	 * @param pageName
	 * @param title 
	 */
	public ErrorReportWizardCommentPage()
	{
		super(ErrorReportWizardCommentPage.class.getName(), NLBasePlugin.getResourceString("errorreport.commentpage.title")); //$NON-NLS-1$
		setDescription(NLBasePlugin.getResourceString("errorreport.commentpage.description")); //$NON-NLS-1$
	}

	/**
	 * @see org.nightlabs.rcp.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	public Control createPageContents(Composite parent)
	{	
		XComposite page = new XComposite(parent, SWT.NONE, XComposite.LAYOUT_MODE_TIGHT_WRAPPER);
//    GridLayout layout = new GridLayout();
//    //layout.
//    page.setLayout(layout);
    Label l = new Label(page, SWT.NONE);
    l.setText(NLBasePlugin.getResourceString("errorreport.commentpage.commentlabel")); //$NON-NLS-1$
		textUserComment = new Text(page, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
		textUserComment.setToolTipText(NLBasePlugin.getResourceString("errorreport.commentpage.commenttooltip")); //$NON-NLS-1$
		textUserComment.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				((DynamicPathWizard)getWizard()).updateDialog();
				ErrorReportWizard wizard = (ErrorReportWizard) getWizard();
				ErrorReport errorReport = wizard.getErrorReport();
				errorReport.setUserComment(textUserComment.getText());
			}
		});
		textUserComment.setLayoutData(new GridData(GridData.FILL_BOTH));
		return page;
	}
	
	/**
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	public boolean isPageComplete()
	{
		return true;
	}
}
