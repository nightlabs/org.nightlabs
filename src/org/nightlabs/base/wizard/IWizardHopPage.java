/*
 * Created on Jun 13, 2005
 */
package org.nightlabs.base.wizard;


/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface IWizardHopPage extends IDynamicPathWizardPage
{
	void setWizardHop(IWizardHop wizardHop);
	IWizardHop getWizardHop();
}
