/*
 * Created on Apr 15, 2005
 */
package org.nightlabs.rcp.notification;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

import org.nightlabs.notification.Interceptor;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.rcp.extensionpoint.AbstractEPProcessor;
import org.nightlabs.rcp.extensionpoint.EPProcessorException;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public abstract class NotificationManager
extends org.nightlabs.notification.NotificationManager
{
	private AbstractEPProcessor notificationInterceptorEPProcessor = new AbstractEPProcessor() {

		public String getExtensionPointID()
		{
			return "org.nightlabs.base.notificationinterceptor";
		}

		public void processElement(IExtension extension, IConfigurationElement element) throws EPProcessorException
		{
			try {
				Interceptor interceptor = (Interceptor) element.createExecutableExtension("class");
//				String name =
					element.getAttribute("name");

				addInterceptor(interceptor);
			} catch (Throwable e) {
				throw new EPProcessorException("Extension to "+getExtensionPointID()+" with class "+element.getAttribute("class")+" has errors!", e);
			}
		}
	};

	protected NotificationManager()
	{
		try {
			notificationInterceptorEPProcessor.process();
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see org.nightlabs.notification.NotificationManager#getNotificationModeForListener(org.nightlabs.notification.NotificationListener)
	 */
	protected String getNotificationModeForListener(NotificationListener listener)
	{
		if (listener instanceof NotificationListenerJob)
			return NotificationListenerJob.class.getName();

		if (listener instanceof NotificationListenerSWTThreadSync)
			return NotificationListenerSWTThreadSync.class.getName();

		if (listener instanceof NotificationListenerSWTThreadAsync)
			return NotificationListenerSWTThreadAsync.class.getName();

		return super.getNotificationModeForListener(listener);
	}

	/**
	 * @see org.nightlabs.notification.NotificationManager#performNotification(java.lang.String, org.nightlabs.notification.NotificationListener, org.nightlabs.notification.NotificationEvent)
	 */
	protected void performNotification(String notificationMode, final NotificationListener listener,
			final NotificationEvent event)
	{
		if (NotificationListenerJob.class.getName().equals(notificationMode)) {
			NotificationListenerJob l = (NotificationListenerJob) listener;

			Job job = l.getJob(event);
			if (job == null) {
				String jobName = l.getJobName();
				if (jobName == null)
					jobName = "Processing Notification";

				job = new Job(jobName) {
					protected IStatus run(IProgressMonitor monitor)
					{
						((NotificationListenerJob)listener).setProgressMonitor(monitor);
						((NotificationListenerJob)listener).notify(event);

						return Status.OK_STATUS;
					}
				};
				job.setRule(l.getRule());
				job.setPriority(l.getPriority());
				job.setUser(l.isUser());
				job.setSystem(l.isSystem());
			}
			job.schedule(l.getDelay());
		}
		else if (NotificationListenerSWTThreadAsync.class.getName().equals(notificationMode)) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run()
				{
					listener.notify(event);
				}
			});
		}
		else if (NotificationListenerSWTThreadSync.class.getName().equals(notificationMode)) {
			Display.getDefault().syncExec(new Runnable() {
				public void run()
				{
					listener.notify(event);
				}
			});
		}
		else
			super.performNotification(notificationMode, listener, event);
	}
}
