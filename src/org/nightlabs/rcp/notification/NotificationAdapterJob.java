/*
 * Created on Aug 27, 2005
 */
package org.nightlabs.rcp.notification;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;

import org.nightlabs.notification.NotificationEvent;


public abstract class NotificationAdapterJob implements NotificationListenerJob
{
	private String jobName = null;

	public NotificationAdapterJob() { }

	public NotificationAdapterJob(String jobName)
	{
		this.jobName = jobName;
	}

	/**
	 * @see org.nightlabs.rcp.notification.NotificationListenerJob#getJob(NotificationEvent)
	 */
	public Job getJob(NotificationEvent event)
	{
		return null;
	}

	public String getJobName()
	{
		return jobName;
	}
	
	private IProgressMonitor progressMonitor;

	/**
	 * @see org.nightlabs.rcp.notification.NotificationListenerJob#setProgressMonitor(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setProgressMonitor(IProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

	/**
	 * @see org.nightlabs.rcp.notification.NotificationListenerJob#getProgressMonitor()
	 */
	public IProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}

	/**
	 * @see org.nightlabs.rcp.notification.NotificationListenerJob#getRule()
	 */
	public ISchedulingRule getRule()
	{
		return null;
	}

	/**
	 * The default implementation of this method returns {@link Job#SHORT}.
	 *
	 * @see org.nightlabs.rcp.notification.NotificationListenerJob#getPriority()
	 */
	public int getPriority()
	{
		return Job.SHORT;
	}

	/**
	 * The default implementation of this method returns 0.
	 *
	 * @see org.nightlabs.rcp.notification.NotificationListenerJob#getDelay()
	 */
	public long getDelay()
	{
		return 0;
	}

	/**
	 * The default implementation of this method returns false.
	 *
	 * @see org.nightlabs.rcp.notification.NotificationListenerJob#isUser()
	 */
	public boolean isUser()
	{
		return false;
	}

	/**
	 * The default implementation of this method returns false.
	 *
	 * @see org.nightlabs.rcp.notification.NotificationListenerJob#isSystem()
	 */
	public boolean isSystem()
	{
		return false;
	}
}
