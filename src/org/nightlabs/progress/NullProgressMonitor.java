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

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class NullProgressMonitor implements ProgressMonitor {

	private boolean canceled;

	public NullProgressMonitor() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.progress.ProgressMonitor#beginTask(java.lang.String, int)
	 */
	public void beginTask(String name, int totalWork) {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.progress.ProgressMonitor#done()
	 */
	public void done() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.progress.ProgressMonitor#setTaskName(java.lang.String)
	 */
	public void setTaskName(String name) {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.progress.ProgressMonitor#subTask(java.lang.String)
	 */
	public void subTask(String name) {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.progress.ProgressMonitor#worked(int)
	 */
	public void worked(int work) {
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	public void internalWorked(double worked) {
	}
}
