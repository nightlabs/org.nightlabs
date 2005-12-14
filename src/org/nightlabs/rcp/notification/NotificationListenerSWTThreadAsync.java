/*
 * Created on Apr 19, 2005
 */
package org.nightlabs.rcp.notification;

import org.nightlabs.notification.NotificationListener;

/**
 * Notification will be done on the SWT GUI thread and the caller thread (which executes
 * the {@link org.nightlabs.notification.NotificationManager#notify(NotificationEvent)}
 * method) will come back immediately - no wait. This is done by
 * {@link org.eclipse.swt.widgets.Display#asyncExec(java.lang.Runnable)}.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface NotificationListenerSWTThreadAsync extends NotificationListener
{

}
