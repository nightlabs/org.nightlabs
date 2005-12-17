/*
 * Created on Oct 5, 2005
 */
package org.nightlabs.base.action;

import org.eclipse.jface.action.IContributionItem;

public interface IXContributionItem extends IContributionItem
{
	void setId(String id);

	public void setEnabled(boolean enabled);
}
