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
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.exceptionhandler.ExceptionHandlingThreadGroup;
import org.nightlabs.base.exceptionhandler.SaveRunnableRunner;
import org.nightlabs.util.Utils;

/**
 * @author Daniel.Mazurek[AT]NightLabs[DOT]de
 * @author Alex[AT]NightLabs[DOT]de</p>
 */
public abstract class AbstractApplication 
implements IPlatformRunnable
{
	public static final Logger LOGGER = Logger.getLogger(AbstractApplication.class);

	/**
	 * The system properties hold the name of the application accessible via this key (it's set by {@link #setSystemProperty()}).
	 * Use <code>System.getProperty(APPLICATION_SYSTEM_PROPERTY_NAME)</code> to get the application name.
	 */
	public static final String APPLICATION_SYSTEM_PROPERTY_NAME = "nightlabs.base.application.name";

	public AbstractApplication() 
	{
		super();
		init();
	}
	
	private static String applicationName = "AbstractApplication";
	public static String getApplicationName() {
		return applicationName;
	}
	
	private static String rootDir = "";
	
	/** 
	 * @return the root directory, which is the applicationName in the users home directory.
	 */
	public static String getRootDir() {
		if (rootDir.equals("")){
			File rootFile = new File(System.getProperty("user.home"), "."+applicationName);
			rootFile.mkdirs();
			rootDir = rootFile.getAbsolutePath();
//			System.out.println("rootDir is "+rootDir);
		}
		return rootDir;
	}
	
	private static String configDir = "";
	
	/**
	 * @return the config directory, which is getRootDir()+"/config".
	 */
	public static String getConfigDir() {
		if (configDir.equals("")){
			File configFile = new File(getRootDir(),"config");
			configFile.mkdirs();
			configDir = configFile.getAbsolutePath();
//			System.out.println("configDir is "+configDir);
		}
		return configDir;
	}
	
	
	private static String logDir = "";
	
	/**
	 * 
	 * @return the log directory, which is getRootDir()+"/log"
	 */
	public static String getLogDir() {
		if (logDir.equals("")){
			File logFile = new File(getRootDir(),"log");
			if (!logFile.exists())
				if (!logFile.mkdirs())
					System.out.println("Could not create log directory "+logFile.getAbsolutePath());
			logDir = logFile.getAbsolutePath();
//			System.out.println("logDir is "+logDir);
		}
		return logDir;
	}
	
	private static Object mutex = new Object();
	public static Object getMutex() {
		return mutex;
	}	
	
//	protected static final String LOG4J_CONFIG_FILE = "log4j.xml"; 
	protected static final String LOG4J_CONFIG_FILE = "log4j.properties";
	
	/**
	 * Configures log4j with the file located in {@link #getConfigDir()}+"/log4j.properties"
	 * @throws IOException
	 */
	public static void initializeLogging() 
	throws IOException
	{
//		String logConfFileName = getConfigDir() + File.separatorChar + "log4j.properties";
		String logConfFileName = getConfigDir() + File.separatorChar + LOG4J_CONFIG_FILE;		
		File logProp = new File(logConfFileName);
		if (!logProp.exists()){
			// if not there copy
			Utils.copyResource(AbstractApplication.class ,LOG4J_CONFIG_FILE, logConfFileName);		        
		}
		getLogDir();
		setSystemProperty();
		PropertyConfigurator.configure(logConfFileName);
		LOGGER.info(getApplicationName()+" started.");
	}	
	
	/**
	 * sets the System Property for the ApplicationName so that the
	 * log4j.properties can access this systemProperty
	 *
	 */
	protected static void setSystemProperty() 
	{
		try {
			System.setProperty(APPLICATION_SYSTEM_PROPERTY_NAME, getApplicationName());    	
		} catch (SecurityException se) {
			System.out.println("System Property "+APPLICATION_SYSTEM_PROPERTY_NAME+" could not be set, to "+getApplicationName()+" because:");
			System.out.println("You dont have the permission to set a System Property");
		} catch (NullPointerException npe) {
			System.out.println("System Property "+APPLICATION_SYSTEM_PROPERTY_NAME+" could not be set, to "+getApplicationName()+" because:");    	
			System.out.println("applicationName == null");
		}
//		System.out.println("System Property "+APPLICATION_SYSTEM_PROPERTY_NAME+" = "+System.getProperty(APPLICATION_SYSTEM_PROPERTY_NAME));    
	}

	/**
	 * Creates a display in {@link org.eclipse.ui.PlatformUI} and a new 
	 * {@link org.eclipse.ui.application.WorkbenchAdvisor} and runs the AbstractApplication.
	 * @see org.eclipse.core.runtime.IPlatformRunnable#run(java.lang.Object)
	 */
	public Object run(Object args) 
	throws Exception 
	{
		try {
			NLBasePlugin.getDefault().setApplication(this);
			this.arguments = (String[]) args;
			SafeRunnable.setRunner(new SaveRunnableRunner());
			applicationThread.start();
			synchronized (mutex) {
				mutex.wait();
			}

			if (applicationThread.getPlatformResultCode() == PlatformUI.RETURN_RESTART) 
				return IPlatformRunnable.EXIT_RESTART; 
			else 
				return IPlatformRunnable.EXIT_OK;
		} finally {
			if (Display.getCurrent() != null)
				Display.getCurrent().dispose();
		}
	}

	private String[] arguments = null;

	/**
	 * This method returns the program arguments. Note, that they are <code>null</code> until {@link #run(Object)} has been
	 * called!
	 *
	 * @return The program arguments as passed to the command.
	 */
	public String[] getArguments()
	{
		return arguments;
	}

	private ExceptionHandlingThreadGroup threadGroup = null;	
	protected ThreadGroup getThreadGroup() 
	{
		if (threadGroup == null)
			threadGroup = new ExceptionHandlingThreadGroup(getApplicationName()+"ThreadGroup");
		return threadGroup;
	}

	private AbstractApplicationThread applicationThread = null;
	protected void init() 
	{
		applicationName = initApplicationName();
		applicationThread = initApplicationThread(getThreadGroup());
	}

	/**
	 * 
	 * @return the name of the Application
	 */
	public abstract String initApplicationName();
	
	/**
	 * 
	 * @param group the threadGroup
	 * @return the Implementation of AbstractApplicationThread for the Application
	 */
	public abstract AbstractApplicationThread initApplicationThread(ThreadGroup group);
}
