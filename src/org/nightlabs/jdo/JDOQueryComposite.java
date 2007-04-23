package org.nightlabs.jdo;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.jdo.query.JDOQuery;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class JDOQueryComposite 
extends XComposite 
{

	public JDOQueryComposite(Composite parent, int style, LayoutMode layoutMode,
			LayoutDataMode layoutDataMode) 
	{
		super(parent, style, layoutMode, layoutDataMode);
	}

	public JDOQueryComposite(Composite parent, int style) {
		super(parent, style);
	}
	
	public abstract JDOQuery getJDOQuery();
	protected abstract void createComposite(Composite parent);
	
	private boolean active = true;
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
		setEnabled(active);
	}
}
