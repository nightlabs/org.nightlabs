/*
 * Created 	on Jan 13, 2005
 * 					by alex
 *
 */
package org.nightlabs.rcp.wizard;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import org.nightlabs.rcp.util.RCPUtil;


/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class DynamicPathWizardDialog extends WizardDialog {

	private DynamicPathWizard dynamicWizard;

	public DynamicPathWizardDialog(DynamicPathWizard wizard) {
		this(RCPUtil.getWorkbenchShell(),wizard);
	}

	public DynamicPathWizardDialog(Shell shell, DynamicPathWizard wizard) {
		super(shell, wizard);
		dynamicWizard = wizard;
		dynamicWizard.setDynamicWizardDialog(this);
	}
	
	/**
	 * Overrides and makes it public so the wizard can
	 * trigger the update of the dialog buttons.
	 * 
	 * @see org.eclipse.jface.wizard.WizardDialog#update()
	 */
	public void update() {
		super.update();
	}
	
	/**
	 * Overrides and makes it public.
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#getButton(int)
	 */
	public Button getButton(int id) {
		// TODO Auto-generated method stub
		return super.getButton(id);
	}
	
	protected void backPressed() {
		buttonBar.setFocus(); // to trigger all GUI-element-to-backend-object-store-methods
		super.backPressed();
//		if (dynamicWizard.getPopulator() != null &&
//				getCurrentPage() == dynamicWizard.getWizardEntryPage()) {
//			dynamicWizard.removeAllDynamicWizardPages();
//		}
	}
	
	protected void nextPressed() {
		buttonBar.setFocus(); // to trigger all GUI-element-to-backend-object-store-methods
//		if (getCurrentPage() == dynamicWizard.getWizardEntryPage()) {
//			if (dynamicWizard.getPopulator() != null)
//				dynamicWizard.getPopulator().addDynamicWizardPages(dynamicWizard);
//		}
		super.nextPressed();
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardDialog#finishPressed()
	 */
	protected void finishPressed()
	{
		buttonBar.setFocus(); // to trigger all GUI-element-to-backend-object-store-methods
		super.finishPressed();
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardDialog#cancelPressed()
	 */
	protected void cancelPressed()
	{
		buttonBar.setFocus(); // to trigger all GUI-element-to-backend-object-store-methods
		super.cancelPressed();
	}
	
	

	protected Control createContents(Composite parent) {
		Control result = super.createContents(parent);
		if (dynamicWizard.getFirstPage() instanceof IDynamicPathWizardPage)
			((IDynamicPathWizardPage)dynamicWizard.getFirstPage()).onShow();
		return result;
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.FINISH_ID) {
			IWizardPage currPage = getCurrentPage();
			if (currPage instanceof IDynamicPathWizardPage)
				((IDynamicPathWizardPage)currPage).onHide();
		}
		super.buttonPressed(buttonId);
		if (getReturnCode() != Window.OK) {
			IWizardPage currPage = getCurrentPage();
			if (currPage instanceof IDynamicPathWizardPage)
				((IDynamicPathWizardPage)currPage).onShow();
		}		
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardDialog#showPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	public void showPage(IWizardPage page)
	{
		IWizardPage currPage = getCurrentPage();

		if (currPage == page)
			return;

		if (currPage instanceof IDynamicPathWizardPage)
			((IDynamicPathWizardPage)currPage).onHide();

		super.showPage(page);

		if (page instanceof IDynamicPathWizardPage)
			((IDynamicPathWizardPage)page).onShow();
	}
}
