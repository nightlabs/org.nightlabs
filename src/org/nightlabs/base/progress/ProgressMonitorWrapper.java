/**
 * 
 */
package org.nightlabs.base.progress;

import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ProgressMonitorWrapper implements ProgressMonitor {

	private IProgressMonitor monitor;
	
	public ProgressMonitorWrapper(IProgressMonitor monitor) {
		if (monitor == null)
			throw new IllegalArgumentException("The wrapped monitor must not be null.");
		this.monitor = monitor;
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.progress.ProgressMonitor#beginTask(java.lang.String, int)
	 */
	public void beginTask(String name, int totalWork) {
		monitor.beginTask(name, totalWork);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.progress.ProgressMonitor#done()
	 */
	public void done() {
		monitor.done();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.progress.ProgressMonitor#setTaskName(java.lang.String)
	 */
	public void setTaskName(String name) {
		monitor.setTaskName(name);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.progress.ProgressMonitor#subTask(java.lang.String)
	 */
	public void subTask(String name) {
		monitor.subTask(name);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.progress.ProgressMonitor#worked(int)
	 */
	public void worked(int work) {
		monitor.worked(work);
	}

}
