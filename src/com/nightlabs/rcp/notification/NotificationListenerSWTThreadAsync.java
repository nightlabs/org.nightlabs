/*
 * Created on Apr 19, 2005
 */
package com.nightlabs.rcp.notification;

import com.nightlabs.notification.NotificationListener;

/**
 * Notification will be done on the SWT GUI thread and the caller thread (which executes
 * the {@link com.nightlabs.notification.NotificationManager#notify(NotificationEvent)}
 * method) will come back immediately - no wait. This is done by
 * {@link org.eclipse.swt.widgets.Display#asyncExec(java.lang.Runnable)}.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface NotificationListenerSWTThreadAsync extends NotificationListener
{

}
