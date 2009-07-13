/* ********************************************************************
 * NightLabsBase - Utilities by NightLabs                             *
 * Copyright (C) 2004-2008 NightLabs GmbH - http://NightLabs.org      *
 *                                                                    *
 * This library is free software; you can redistribute it and/or      *
 * modify it under the terms of the GNU Lesser General Public         *
 * License as published by the Free Software Foundation; either       *
 * version 2.1 of the License, or (at your option) any later version. *
 *                                                                    *
 * This library is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  *
 * Lesser General Public License for more details.                    *
 *                                                                    *
 * You should have received a copy of the GNU Lesser General Public   *
 * License along with this library; if not, write to the              *
 *     Free Software Foundation, Inc.,                                *
 *     51 Franklin St, Fifth Floor,                                   *
 *     Boston, MA  02110-1301  USA                                    *
 *                                                                    *
 * Or get it online:                                                  *
 *     http://www.gnu.org/copyleft/lesser.html                        *
 **********************************************************************/
package org.nightlabs.progress;

import java.security.InvalidParameterException;

/**
 * This is the abstract base class of all wrapper implementations using {@link ProgressMonitor}s.
 * You can have a look at {@link SubProgressMonitor} as an example.
 * 
 * @author Marius Heinzmann [marius<at>NightLabs<dot>de]
 */
public abstract class ProgressMonitorDelegater
	implements ProgressMonitor
{
	private ProgressMonitor monitor;
	
	public ProgressMonitorDelegater(ProgressMonitor monitor) {
		if (monitor == null)
			throw new InvalidParameterException("The wrapped monitor must not be null!");
		
		this.monitor = monitor;
	}

	/*
	 * @see org.nightlabs.progress.ProgressMonitor#beginTask(java.lang.String, int)
	 */
	public void beginTask(String name, int totalWork) {
		monitor.beginTask(name, totalWork);
	}

	/*
	 * @see org.nightlabs.progress.ProgressMonitor#done()
	 */
	public void done() {
		monitor.done();
	}

	/*
	 * @see org.nightlabs.progress.ProgressMonitor#internalWorked(double)
	 */
	public void internalWorked(double worked) {
		monitor.internalWorked(worked);
	}

	/*
	 * @see org.nightlabs.progress.ProgressMonitor#isCanceled()
	 */
	public boolean isCanceled() {
		return monitor.isCanceled();
	}

	/*
	 * @see org.nightlabs.progress.ProgressMonitor#setCanceled(boolean)
	 */
	public void setCanceled(boolean canceled) {
		monitor.setCanceled(canceled);
	}

	/*
	 * @see org.nightlabs.progress.ProgressMonitor#setTaskName(java.lang.String)
	 */
	public void setTaskName(String name) {
		monitor.setTaskName(name);
	}

	/*
	 * @see org.nightlabs.progress.ProgressMonitor#subTask(java.lang.String)
	 */
	public void subTask(String name) {
		monitor.subTask(name);
	}

	/*
	 * @see org.nightlabs.progress.ProgressMonitor#worked(int)
	 */
	public void worked(int work) {
		monitor.worked(work);
	}
	
}
