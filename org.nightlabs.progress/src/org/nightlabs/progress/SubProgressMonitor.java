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
 * This is the adaption of the Eclipse SubProgressMonitor to {@link ProgressMonitor}.
 * The whole class is a copy of the Eclipse SubProgressMonitor.
 * <br><br>
 * A progress monitor that uses a given amount of work ticks
 * from a parent monitor. It can be used as follows:
 * <pre>
 *     try {
 *         pm.beginTask("Main Task", 100);
 *         doSomeWork(pm, 30);
 *         SubProgressMonitor subMonitor= new SubProgressMonitor(pm, 40);
 *         try {
 *             subMonitor.beginTask("", 300);
 *             doSomeWork(subMonitor, 300);
 *         } finally {
 *             subMonitor.done();
 *         }
 *         doSomeWork(pm, 30);
 *     } finally {
 *         pm.done();
 *     }
 * </pre>
 * <p>
 * This class can be used without OSGi running.
 * </p><p>
 * This class may be instantiated or subclassed by clients.
 * </p>
 * 
 * @author Marius Heinzmann [marius<at>NightLabs<dot>de]
 */
public class SubProgressMonitor extends ProgressMonitorDelegater {
	
	/**
	 * Style constant indicating that calls to <code>subTask</code>
	 * should not have any effect.
	 *
	 * @see #SubProgressMonitor(IProgressMonitor,int,int)
	 */
	public static final int SUPPRESS_SUBTASK_LABEL = 1 << 1;
	/**
	 * Style constant indicating that the main task label
	 * should be prepended to the subtask label.
	 *
	 * @see #SubProgressMonitor(IProgressMonitor,int,int)
	 */
	public static final int PREPEND_MAIN_LABEL_TO_SUBTASK = 1 << 2;

	private int parentTicks = 0;
	private double sentToParent = 0.0;
	private double scale = 0.0;
	private int nestedBeginTasks = 0;
	private boolean usedUp = false;
	private boolean hasSubTask = false;
	private int style;
	private String mainTaskLabel;

	/**
	 * Creates a new sub-progress monitor for the given monitor. The sub
	 * progress monitor uses the given number of work ticks from its
	 * parent monitor.
	 *
	 * @param monitor the parent progress monitor
	 * @param ticks the number of work ticks allocated from the
	 *    parent monitor
	 */
	public SubProgressMonitor(ProgressMonitor wrappedMonitor, int ticks) {
		this(wrappedMonitor, ticks, 0);
	}

	/**
	 * Creates a new sub-progress monitor for the given monitor. The sub
	 * progress monitor uses the given number of work ticks from its
	 * parent monitor.
	 *
	 * @param monitor the parent progress monitor
	 * @param ticks the number of work ticks allocated from the
	 *    parent monitor
	 * @param style one of
	 *    <ul>
	 *    <li> <code>SUPPRESS_SUBTASK_LABEL</code> </li>
	 *    <li> <code>PREPEND_MAIN_LABEL_TO_SUBTASK</code> </li>
	 *    </ul>
	 * @see #SUPPRESS_SUBTASK_LABEL
	 * @see #PREPEND_MAIN_LABEL_TO_SUBTASK
	 */
	public SubProgressMonitor(ProgressMonitor wrappedMonitor, int ticks, int style) {
		super(wrappedMonitor);
		this.parentTicks = ticks;
		this.style = style;
	}

	/* (Intentionally not javadoc'd)
	 * Implements the method <code>IProgressMonitor.beginTask</code>.
	 *
	 * Starts a new main task. Since this progress monitor is a sub
	 * progress monitor, the given name will NOT be used to update
	 * the progress bar's main task label. That means the given
	 * string will be ignored. If style <code>PREPEND_MAIN_LABEL_TO_SUBTASK
	 * <code> is specified, then the given string will be prepended to
	 * every string passed to <code>subTask(String)</code>.
	 */
	@Override
	public void beginTask(String name, int totalWork) {
		nestedBeginTasks++;
		// Ignore nested begin task calls.
		if (nestedBeginTasks > 1) {
			return;
		}
		// be safe:  if the argument would cause math errors (zero or
		// negative), just use 0 as the scale.  This disables progress for
		// this submonitor.
		scale = totalWork <= 0 ? 0 : (double) parentTicks / (double) totalWork;
		if ((style & PREPEND_MAIN_LABEL_TO_SUBTASK) != 0) {
			mainTaskLabel = name;
		}
	}

	/* (Intentionally not javadoc'd)
	 * Implements the method <code>IProgressMonitor.done</code>.
	 */
	@Override
	public void done() {
		// Ignore if more done calls than beginTask calls or if we are still
		// in some nested beginTasks
		if (nestedBeginTasks == 0 || --nestedBeginTasks > 0)
			return;
		// Send any remaining ticks and clear out the subtask text
		double remaining = parentTicks - sentToParent;
		if (remaining > 0)
			super.internalWorked(remaining);
		//clear the sub task if there was one
		if (hasSubTask)
			subTask("");
		sentToParent = 0;
	}

	/* (Intentionally not javadoc'd)
	 * Implements the internal method <code>IProgressMonitor.internalWorked</code>.
	 */
	@Override
	public void internalWorked(double work) {
		if (usedUp || nestedBeginTasks != 1) {
			return;
		}

		double realWork = scale * work;
		super.internalWorked(realWork);
		sentToParent += realWork;
		if (sentToParent >= parentTicks) {
			usedUp = true;
		}
	}

	/* (Intentionally not javadoc'd)
	 * Implements the method <code>IProgressMonitor.subTask</code>.
	 */
	@Override
	public void subTask(String name) {
		if ((style & SUPPRESS_SUBTASK_LABEL) != 0) {
			return;
		}
		hasSubTask = true;
		String label = name;
		if ((style & PREPEND_MAIN_LABEL_TO_SUBTASK) != 0 && mainTaskLabel != null && mainTaskLabel.length() > 0) {
			label = mainTaskLabel + ' ' + label;
		}
		super.subTask(label);
	}

	/* (Intentionally not javadoc'd)
	 * Implements the method <code>IProgressMonitor.worked</code>.
	 */
	@Override
	public void worked(int work) {
		internalWorked(work);
	}

}
