/*
 * Created on Apr 19, 2005
 */
package org.nightlabs.base.notification;

import org.nightlabs.notification.NotificationListener;

/**
 * Notification will be done on the SWT GUI thread and the caller thread (which executes
 * the {@link org.nightlabs.notification.NotificationManager#notify(NotificationEvent)}
 * method) will wait for it. This is done by
 * {@link org.eclipse.swt.widgets.Display#syncExec(java.lang.Runnable)}.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface NotificationListenerSWTThreadSync extends NotificationListener
{

}
