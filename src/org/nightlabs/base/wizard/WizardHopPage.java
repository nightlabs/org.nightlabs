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

package org.nightlabs.base.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
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

	public void setWizardHop(IWizardHop wizardHop)
	{
		this.wizardHop = wizardHop;
	}

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

	
	@Override
	public void setWizard(IWizard newWizard) {
		super.setWizard(newWizard);
		if (wizardHop == null)
			return;
		for (IWizardHopPage hopPage : wizardHop.getHopPages()) {
			if (hopPage != this && hopPage.getWizard() != newWizard)
				hopPage.setWizard(newWizard);
		}
	}
}
