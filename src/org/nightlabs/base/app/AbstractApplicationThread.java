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

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;

/**
 * 
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de, <p>
 * <p>		 		 Alex[AT]NightLabs[DOT]de</p>
 */
public abstract class AbstractApplicationThread 
extends Thread 
{

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
	
	public int getPlatformResultCode() {
		return platformResultCode;
	}
	
	/**
	 * First initializes the {@link org.nightlabs.config.Config} in the users home 
	 * directory under ".applicationName/config". Sets static members of this class for 
	 * other plugins to access the root of the applicationName folder and log.
	 * Creates a display in {@link org.eclipse.ui.PlatformUI} and a new WorkbenchAdvisor 
	 * {@link getWorkbenchAdvisor} and runs the Application.
	 * @see org.eclipse.core.boot.IPlatformRunnable#run(java.lang.Object)
	 */	
	public void run() 
	{
		try {
			// create the display
	  	WorkbenchAdvisor workbenchAdvisor = getWorkbenchAdvisor();
	    Display display = PlatformUI.createDisplay();
//	    try {
//	    	AbstractApplication.initializeLogging();
//	    } catch (Throwable t) {
//	    	System.out.println("Could not initialize logging !!!");
//	    }
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
			workbenchAdvisor = initWorkbenchAdvisor();
		
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
	public abstract AbstractWorkbenchAdvisor initWorkbenchAdvisor();	

}
