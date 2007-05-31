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

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.util.RCPUtil;
import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;

/**
 * Baisc {@link WorkbenchAdvisor} that can be used as basis when developing custom applications.
 * This Advisor initializes Log4J and the NightLabs {@link Config} for you.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @author Daniel Mazurek Daniel.Mazurek[AT]NightLabs[DOT]de
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
				AbstractApplication.initializeLogging();
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
		Config.createSharedInstance(new File(AbstractApplication.getConfigDir(), "config.xml"), true);		
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
			org.nightlabs.config.Config.sharedInstance().save();
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

	/**
	 * Init the application.
	 * <p>
	 * The default implementation returns 
	 * {@link AbstractApplication#sharedInstance()}
	 * which should be the application that is
	 * currently running.
	 * <p>
	 * Extendors might call <code>super.initApplication()</code>
	 * and then modify the result.
	 */
	protected AbstractApplication initApplication() {
		return AbstractApplication.sharedInstance();
	}

	/**
	 * creates the DefaultWorkbenchWindowAdvisor by default.
	 * iy you want to specify your own WorkbenchWindowAdvisor, you have to override
	 * this Method
	 * 
	 * @param configurer the IWorkbenchWindowConfigurer
	 * @return the WorkbenchWindowAdvisor for this WorkbenchAdvisor
	 */
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		return new DefaultWorkbenchWindowAdvisor(configurer, AbstractApplication.getApplicationName());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#getInitialWindowPerspectiveId()
	 */
	@Override
	public String getInitialWindowPerspectiveId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void postShutdown() {
		super.postShutdown();
		checkClearWorkspace();
	}		
	
	protected void checkClearWorkspace() 
	{
		String[] args = NLBasePlugin.getDefault().getApplication().getArguments();
		boolean doClearWorkspace = true;
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			String val = i + 1 < args.length ? args[i + 1] : null;
			if ("--clearWorkspace".equals(arg))
				doClearWorkspace = Boolean.parseBoolean(val);
		}
		if (doClearWorkspace) {
			logger.info("************ clearing workspace! **************");
			RCPUtil.clearWorkspace(false);
		}
	}
	
}
