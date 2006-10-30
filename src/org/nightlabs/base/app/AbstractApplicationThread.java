/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.base.app;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;

/**
 * @author Daniel.Mazurek[AT]NightLabs[DOT]de
 * @author Alex[AT]NightLabs[DOT]de</p>
 */
public abstract class AbstractApplicationThread 
extends Thread 
{
	
	/**
	 * Logger used by this class.
	 */
	private static final Logger logger = Logger.getLogger(AbstractApplicationThread.class);

	public AbstractApplicationThread() {
		super();
	}

	public AbstractApplicationThread(Runnable arg0) {
		super(arg0);
	}

	public AbstractApplicationThread(ThreadGroup arg0, Runnable arg1) {
		super(arg0, arg1);
	}

	public AbstractApplicationThread(String arg0) {
		super(arg0);
	}

	public AbstractApplicationThread(ThreadGroup arg0, String arg1) {
		super(arg0, arg1);
	}

	public AbstractApplicationThread(Runnable arg0, String arg1) {
		super(arg0, arg1);
	}

	public AbstractApplicationThread(ThreadGroup arg0, Runnable arg1, String arg2) {
		super(arg0, arg1, arg2);
	}

	public AbstractApplicationThread(ThreadGroup arg0, Runnable arg1,
			String arg2, long arg3) 
	{
		super(arg0, arg1, arg2, arg3);
	}

	private int platformResultCode = -1;

	protected void setPlatformResultCode(int platformResultCode) {
		this.platformResultCode = platformResultCode;
	}
	
	public int getPlatformResultCode() {
		return platformResultCode;
	}

	private Display display;

	/**
	 * First initializes the {@link org.nightlabs.config.Config} in the users home 
	 * directory under ".applicationName/config". Sets static members of this class for 
	 * other plugins to access the root of the applicationName folder and log.
	 * Creates a display in {@link org.eclipse.ui.PlatformUI} and a new WorkbenchAdvisor 
	 * {@link #getWorkbenchAdvisor()} and runs the Application.
	 * @see org.eclipse.core.runtime.IPlatformRunnable#run(java.lang.Object)
	 */	
	public void run() 
	{
		try {
			// create the display
			display = PlatformUI.createDisplay();
			WorkbenchAdvisor workbenchAdvisor = getWorkbenchAdvisor();
			
			try {				
				preCreateWorkbench();
			} catch (Throwable t) {
				logger.warn("Error in preCreateWorkbench()!", t);
			}
			
			platformResultCode = PlatformUI.createAndRunWorkbench(display, workbenchAdvisor);
		}
		catch(Throwable e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		finally {
			synchronized(getApplication().getMutex()) {
				getApplication().getMutex().notifyAll();
			}
		}
	}

	protected AbstractWorkbenchAdvisor workbenchAdvisor = null;

	public AbstractWorkbenchAdvisor getWorkbenchAdvisor() 
	{
		if (workbenchAdvisor == null)
			workbenchAdvisor = initWorkbenchAdvisor(display);

		return workbenchAdvisor;
	}

	protected AbstractApplication application = null;
	/**
	 * 
	 * @return the the Implementation of the AbstractApplication for the
	 * Application
	 * @see AbstractApplication
	 */
	public AbstractApplication getApplication() 
	{
		if (application == null)
			application = getWorkbenchAdvisor().getApplication();

		return application;
	}

	/**
	 * 
	 * @return the Implementation of the AbstractWorkbenchAdvisor for the
	 * ApplicationThread
	 * @see AbstractWorkbenchAdvisor
	 */
	public abstract AbstractWorkbenchAdvisor initWorkbenchAdvisor(Display display);	

	/**
	 * Called right before the workbench is created.
	 * Default implementation does nothing.
	 */
	protected void preCreateWorkbench() {}
	
}
