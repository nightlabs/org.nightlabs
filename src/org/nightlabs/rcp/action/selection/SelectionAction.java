/**
 * <p> Project: org.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 20.10.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.rcp.action.selection;

import org.eclipse.jface.action.Action;

import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.rcp.notification.NotificationListenerSWTThreadAsync;
import org.nightlabs.rcp.notification.SelectionManager;

public abstract class SelectionAction 
extends Action
//implements IUpdateAction
{
	public SelectionAction() { }

	public void init(String zone, Class selectionClass, String text)
	{
		SelectionManager.sharedInstance().addNotificationListener(zone, selectionClass, selectionListener);
	}

	private NotificationListener selectionListener = new NotificationListenerSWTThreadAsync() {
		public void notify(NotificationEvent notificationEvent) {
			setEnabled(calculateEnabled(notificationEvent));
		}
	};

	public abstract boolean calculateEnabled(NotificationEvent evt);	
		
//	/**
//	 * updates the UpdateAction when the selection changed
//	 *
//	 */
//	public void update() 
//	{
//		setEnabled(calculateEnabled());		
//	}
	
//	/**
//	 * Gets the current selection.
//	 * 
//	 * @return The current selection.
//	 */
//	protected ISelection getSelection() {
//		return selection;
//	}	
	
//	/**
//	 * Returns a <code>List</code> containing the currently
//	 * selected objects.
//	 * 
//	 * @return A List containing the currently selected objects.
//	 */
//	protected List getSelectedObjects() {
//		if (!(getSelection() instanceof IStructuredSelection))
//			return Collections.EMPTY_LIST;
//		return ((IStructuredSelection)getSelection()).toList();
//	}

}
