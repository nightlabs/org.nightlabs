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
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.nightlabs.base.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;

/**
 *
 * <p> Author: alex[AT]NightLabs[DOT]de </p>
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public abstract class AbstractWorkbenchAdvisor 
extends WorkbenchAdvisor 
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(AbstractWorkbenchAdvisor.class);

	public AbstractWorkbenchAdvisor() 
	{
		super();
		try {
			application = initApplication();
			try {
				getApplication().initializeLogging();
			} catch (Throwable t) {
				System.out.println("Could not initialize logging !!!");
			}  		

			if (classSharing)
			{
//				if (isSystemClassLoaderDelegating()) {
//				LOGGER.debug("Initializing classsharing ...");
//				ClasssharingPlugin.initializeClassSharing();
//				LOGGER.debug("Initializing classsharing ... DONE");
//				}
//				else
//				LOGGER.error("classsharing is enabled, but system classloader is NOT an instance of DelegatingClassLoader! Cannot initialize classsharing!");
			}
			else
				logger.debug("classsharing is disabled - NOT initialized.");


			initConfig();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}

	protected boolean classSharing = true;
	public void setClassSharing(boolean classSharing) {
		this.classSharing = classSharing;
	}	

//	public static boolean isSystemClassLoaderDelegating() {
//	return ClassLoader.getSystemClassLoader() instanceof DelegatingClassLoader;
//	}	


	/**
	 * Checks the {@link ExceptionHandlerRegistry} for registered handlers by invoking
	 * {@link ExceptionHandlerRegistry#searchHandler(Throwable)}. For the found item the 
	 * {@link org.eclipse.jface.window.Window.IExceptionHandler#handleException(java.lang.Throwable)} 
	 * method is invoked on a unique instance of the eventHandler class (plugin.xml). 
	 */
	public void eventLoopException(Throwable exception) {
		if (!ExceptionHandlerRegistry.syncHandleException(exception))
			super.eventLoopException(exception);
	}  	

	/**
	 * initialzies the Config in the ConfigDir of the Application
	 * @throws ConfigException
	 */
	protected void initConfig() 
	throws ConfigException
	{
//		initialize the Config
		Config.createSharedInstance("config.xml", true, getApplication().getConfigDir());		
	}	

	/**
	 * Intializes the workbench 
	 */
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		configurer.setSaveAndRestore(true);
	}	

	/**
	 * saves the Config before the Application is shutDown
	 */
	public boolean preShutdown() 
	{
		boolean superResult = super.preShutdown();
		try {
			org.nightlabs.config.Config.sharedInstance().saveConfFile();
		} catch (ConfigException e) {
			logger.error("Saving config failed!", e);
		}
		return superResult;
	}

	protected AbstractApplication application = null;
	public AbstractApplication getApplication() 
	{
		if (application == null)
			application = initApplication();

		return application;
	}

	protected abstract AbstractApplication initApplication();	

	/**
	 * creates the DefaultWorkbenchWindowAdvisor by default.
	 * iy you want to specify your own WorkbenchWindowAdvisor, you have to override
	 * this Method
	 * 
	 * @param configurer the IWorkbenchWindowConfigurer
	 * @return the WorkbenchWindowAdvisor for this WorkbenchAdvisor
	 */
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		return new DefaultWorkbenchWindowAdvisor(configurer, getApplication().getApplicationName());
	}		
}
