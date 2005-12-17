/*
 * Created 	on Sep 13, 2005
 * 					by alex
 *
 */
package org.nightlabs.base.part;

import org.eclipse.ui.IWorkbenchPartReference;

/**
 * Simple Adapter for {@link org.nightlabs.base.part.PartVisibilityListener}
 * doing nothing.
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public class PartVisibilityAdapter implements PartVisibilityListener {

	public PartVisibilityAdapter() {
		super();
	}

	/**
	 * Default implementation does nothing.
	 * @see org.nightlabs.base.part.PartVisibilityListener#partVisible(org.eclipse.ui.IWorkbenchPartReference)
	 */
	public void partVisible(IWorkbenchPartReference partRef) {
		// do nothing
	}

	/**
	 * Default implementation does nothing.
	 * @see org.nightlabs.base.part.PartVisibilityListener#partHidden(org.eclipse.ui.IWorkbenchPartReference)
	 */
	public void partHidden(IWorkbenchPartReference partRef) {
		// do nothing
	}

}
