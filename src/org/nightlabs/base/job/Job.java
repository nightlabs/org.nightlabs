/**
 * 
 */
package org.nightlabs.base.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.nightlabs.base.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.progress.ProgressMonitorWrapper;
import org.nightlabs.progress.ProgressMonitor;

/**
 * A {@link org.eclipse.core.runtime.jobs.Job} that will wrap around
 * the normal {@link IProgressMonitor} of an Eclipse Job and provide
 * an {@link ProgressMonitorWrapper} instead.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public abstract class Job extends org.eclipse.core.runtime.jobs.Job {

	private ProgressMonitorWrapper progressMonitorWrapper;
	private IProgressMonitor progressMonitor;
	private ProgressMonitorWrapper subProgressMonitorWrapper;
	private IProgressMonitor subProgressMonitor;
	
	/**
	 * @param name
	 */
	public Job(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		this.progressMonitor = monitor;
		IStatus status;
		try {
			status = run(getProgressMonitorWrapper());
		} catch (Throwable t) {
			status = Status.CANCEL_STATUS;
			ExceptionHandlerRegistry.asyncHandleException(t);
		}
		return status;
	}
	
	protected abstract IStatus run(ProgressMonitor monitor);
	
	public ProgressMonitorWrapper getProgressMonitorWrapper() {
		if (progressMonitorWrapper == null) {
			if (progressMonitor == null)
				throw new IllegalStateException("getProgressMonitorWrapper must not be called before run(IProgressMonitor) was invoked.");
			progressMonitorWrapper = new ProgressMonitorWrapper(progressMonitor);
		}
		return progressMonitorWrapper;
	}
	
	
	public ProgressMonitorWrapper getSubProgressMonitorWrapper(int subTicks) {
		if (subProgressMonitorWrapper == null) {
			if (progressMonitor == null)
				throw new IllegalStateException("getSubProgressMonitorWrapper must not be called before run(IProgressMonitor) was invoked.");
			subProgressMonitor = new SubProgressMonitor(progressMonitor, subTicks);
			subProgressMonitorWrapper = new ProgressMonitorWrapper(subProgressMonitor);
		}
		return subProgressMonitorWrapper;
	}

}
