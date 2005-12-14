/*
 * Created on Jun 13, 2005
 */
package org.nightlabs.rcp.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;


/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public abstract class WizardHopPage extends DynamicPathWizardPage
implements IWizardHopPage
{

	/**
	 * @param pageName
	 */
	public WizardHopPage(String pageName)
	{
		super(pageName);
	}

	/**
	 * @param pageName
	 * @param title
	 */
	public WizardHopPage(String pageName, String title)
	{
		super(pageName, title);
	}

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public WizardHopPage(String pageName, String title, ImageDescriptor titleImage)
	{
		super(pageName, title, titleImage);
	}
	
	private IWizardHop wizardHop;

	/**
	 * @see org.nightlabs.rcp.wizard.IWizardHopPage#setWizardHop(org.nightlabs.ipanema.trade.transfer.wizard.IWizardHop)
	 */
	public void setWizardHop(IWizardHop wizardHop)
	{
		this.wizardHop = wizardHop;
	}

	/**
	 * @see org.nightlabs.rcp.wizard.IWizardHopPage#getWizardHop()
	 */
	public IWizardHop getWizardHop()
	{
		return wizardHop;
	}

	/**
	 * If this WizardHopPage has a wizardHop set, the hop will be asked for the next
	 * page, otherwise the wizard of this page (if set) will be asked.
	 *   
	 * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
	 */
	public IWizardPage getNextPage()
	{
		if (wizardHop != null)
			return wizardHop.getNextPage(this);
		else {
			if (getWizard() != null)
				return getWizard().getNextPage(this);
			else
				throw new IllegalStateException("wizardHop AND wizard are not assigned! If this is the entry-page of a WizardHop, no WizardHop has been created in the constructor! Call 'new WizardHop(this);' in the constructor of " + this.getClass().getName()+". If this should be used as normal WizardPage, add it to the wizard.");		
		}
	}

//	/**
//	 * @see org.eclipse.jface.wizard.WizardPage#getPreviousPage()
//	 */
//	public IWizardPage getPreviousPage()
//	{
//		return wizardHop.getPreviousPage(this);
//	}
}
