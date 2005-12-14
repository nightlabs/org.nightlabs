/*
 * Created 	on Sep 13, 2005
 * 					by alex
 *
 */
package org.nightlabs.rcp.part;

import org.eclipse.ui.IWorkbenchPartReference;

/**
 * Listener that will be notified by {@link org.nightlabs.rcp.part.PartVisibilityTracker}
 * when a workbenchPart is hidden or shown.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public interface PartVisibilityListener {
	
	/**
	 * Will be triggered when the parts status changed to visible.
	 * 
	 * @param partRef A reference to the part that got visible.
	 */
	public void partVisible(IWorkbenchPartReference partRef);

	/**
	 * Will be triggered when the parts status changed to hidden.
	 * 
	 * @param partRef A reference to the part that was hidden.
	 */
	public void partHidden(IWorkbenchPartReference partRef);
	
}
