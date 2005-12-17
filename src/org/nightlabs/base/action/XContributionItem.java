/*
 * Created on Oct 5, 2005
 */
package org.nightlabs.base.action;

import java.lang.reflect.Field;

import org.eclipse.jface.action.ContributionItem;

public class XContributionItem
extends ContributionItem
implements IXContributionItem
{
	public XContributionItem()
	{
	}

	public XContributionItem(String id)
	{
		super(id);
	}

	public void setId(String id)
	{
		try {
			Field field = ContributionItem.class.getDeclaredField("id");
			field.setAccessible(true);
			field.set(this, id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private boolean enabled;

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
}
