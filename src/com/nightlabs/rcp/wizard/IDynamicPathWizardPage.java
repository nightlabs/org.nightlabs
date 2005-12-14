/*
 * Created on Mar 8, 2005
 */
package com.nightlabs.rcp.wizard;

import org.eclipse.jface.wizard.IWizardPage;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface IDynamicPathWizardPage extends IWizardPage
{
	/**
	 * When the {@link DynamicPathWizard} evaluates whether it can finish
	 * (in the method {@link DynamicPathWizard#canFinish()}),
	 * it asks all pages of the current path, whether they are complete by calling
	 * {@link IWizardPage#isPageComplete()}. To find out the current path,
	 * it starts at the entry page and moves forward by {@link IWizardPage#getNextPage()}.
	 * In case {@link IWizardPage#getNextPage()} returns <tt>null</tt>, the wizard cannot
	 * know whether this page is really the last or whether the next page is just not yet
	 * known. To make clear that a new page must follow, overwrite this method to return
	 * <tt>false</tt>.
	 *
	 * @return Returns whether or not this wizard page can be the last one in a chain.
	 */
	boolean canBeLastPage();

	/**
	 * This method will be called, when this page becomes the current one (after it has
	 * been made visible).
	 */
	void onShow();

	/**
	 * This method will be called, after this page was the current one, when setVisible
	 * is called with false (after the page was hidden, before the other page becomes
	 * visible).
	 */
	void onHide();
}
