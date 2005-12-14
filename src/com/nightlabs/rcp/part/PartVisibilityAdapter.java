/*
 * Created 	on Sep 13, 2005
 * 					by alex
 *
 */
package com.nightlabs.rcp.part;

import org.eclipse.ui.IWorkbenchPartReference;

/**
 * Simple Adapter for {@link com.nightlabs.rcp.part.PartVisibilityListener}
 * doing nothing.
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public class PartVisibilityAdapter implements PartVisibilityListener {

	public PartVisibilityAdapter() {
		super();
	}

	/**
	 * Default implementation does nothing.
	 * @see com.nightlabs.rcp.part.PartVisibilityListener#partVisible(org.eclipse.ui.IWorkbenchPartReference)
	 */
	public void partVisible(IWorkbenchPartReference partRef) {
		// do nothing
	}

	/**
	 * Default implementation does nothing.
	 * @see com.nightlabs.rcp.part.PartVisibilityListener#partHidden(org.eclipse.ui.IWorkbenchPartReference)
	 */
	public void partHidden(IWorkbenchPartReference partRef) {
		// do nothing
	}

}
