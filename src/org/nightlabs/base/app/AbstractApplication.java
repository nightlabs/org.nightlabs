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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.exceptionhandler.ExceptionHandlingThreadGroup;
import org.nightlabs.base.exceptionhandler.SaveRunnableRunner;
import org.nightlabs.util.Utils;

/**
 * This is the basis for RCP applications based on the nightlabs base plugin.
 * <p>
 * In order to use this framework you have to do several things.<br>
 * First you'll have to extend this class and register your implementation 
 * to the <code>org.eclipse.core.runtime.applications</code> extension-point.
 * Doing so will tell Eclipse to run your application.
 * <p>
 * When implementing your application you will see, that you have to write two
 * methods. One is to provide a name for your application {@link #initApplicationName()},
 * the other should provide a Thread that actually runs the application and the Workbench {@link #initApplicationThread(ThreadGroup)}.
 * {@link AbstractApplication} does not do much in its run method, it rather starts the application
 * thread and waits until it gets notified that the thread ended. What the application does, is 
 * taking care of uncaught Exceptions in all threads created by the application. This
 * is done by using a special ThreadGroup, the {@link ExceptionHandlingThreadGroup}. This
 * is passed to the method that creates the application thread and should be used as its parent group. 
 * <p>
 * Now after having created an implementation of {@link AbstractApplication}, you need to implement the thread actually
 * running the application where you are obliged to extend {@link AbstractApplicationThread}.
 * This thread runs the actual RCP application and provides a {@link WorkbenchAdvisor} for it.
 * <p>
 * That's basicly all you have to do. For customizations of your application you can use the
 * {@link WorkbenchAdvisor} you provide (You may use {@link AbstractWorkbenchAdvisor} as a basis here)
 * or the {@link WorkbenchWindowAdvisor} that is provided by the workbench advisor.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @author Daniel Mazurek Daniel.Mazurek[AT]NightLabs[DOT]de
 */
public abstract class AbstractApplication 
implements IPlatformRunnable
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(AbstractApplication.class);

	/**
	 * The system properties hold the name of the application accessible via this key (it's set by {@link #setAppNameSystemProperty()}).
	 * Use <code>System.getProperty(APPLICATION_SYSTEM_PROPERTY_NAME)</code> to get the application name.
	 */
	public static final String APPLICATION_SYSTEM_PROPERTY_NAME = "nightlabs.base.application.name";
	/**
	 * This is used to choose the application folder when the application starts.
	 * After start the system property with this name will point to the applications root folder.
	 * <p>
	 * To initialize the application folder the system property can be set before the application 
	 * starts. It might contain references to system environment variables in the following way:
	 * $ENV_NAME$, where ENV_NAME is the name of the environment variable.
	 */
	public static final String APPLICATION_FOLDER_SYSTEM_PROPERTY_NAME = "nightlabs.base.application.folder";

	protected static AbstractApplication sharedInstance;

	
	protected static AbstractApplication sharedInstance() {
		if (sharedInstance == null)
			throw new IllegalStateException("No application has been created yet!");
		return sharedInstance;
	}
	
	/**
	 * Constructs a new Application and {@link #init()}s it.
	 */
	public AbstractApplication() 
	{
		super();
		init();
		sharedInstance = this;
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
		if (rootDir.equals("")) {
			File rootFile = null; 
			// check system property org.nightlabs.appfolder
			String initialFolderName = System.getProperty(APPLICATION_FOLDER_SYSTEM_PROPERTY_NAME);
			if (initialFolderName == null) {
				// sys property not set, we use the users home dir
				rootFile = new File(System.getProperty("user.home"), "."+getApplicationName());
			}
			else {
				// the sys property is set, parse it
				String resolvedFolderName = initialFolderName;
				Pattern envRefs = Pattern.compile("\\$((.*?))\\$");
				Matcher matcher = envRefs.matcher(initialFolderName);
				while (matcher.find()) {
					String envValue = System.getenv(matcher.group(1));
					if (envValue == null) {
						System.err.println("Reference to undefined system environment variable "+matcher.group(1)+" in system property "+APPLICATION_FOLDER_SYSTEM_PROPERTY_NAME);
						envValue = "";
					}
					resolvedFolderName = resolvedFolderName.replace(matcher.group(0), envValue);
//					resolvedFolderName = resolvedFolderName.replaceAll("\\$"+matcher.group(1)+"\\$", envValue);
				}
				rootFile = new File(resolvedFolderName, "."+getApplicationName());
			}
			if (rootFile.exists() && !rootFile.isDirectory()) {
				System.err.println("[PANIC] The application's root directory exists, but is NOT a directory: "+rootFile);
				System.err.println("[PANIC] The application will probably not run correctly!!");
			}
			if (!rootFile.exists() && !rootFile.mkdirs()) {
				System.err.println("[PANIC] Could not create the application's root directory: "+rootFile);
				System.err.println("[PANIC] The application will probably not run correctly!!");
			}
			rootDir = rootFile.getAbsolutePath();
			setAppFolderSystemProperty(rootDir);
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

	/**
	 */
	private static Object mutex = new Object();
	
	/**
	 * The mutex is the object the {@link AbstractApplicationThread}
	 * synchronizes with the {@link AbstractApplication}.
	 * <p>
	 * In its {@link #run(Object)} method this application
	 * creates an application thread {@link #initApplicationThread(ThreadGroup)},
	 * starts it and then waits for it to notify the application
	 * via this mutex.
	 * 
	 * @return The mutex used to synchronize the application and its thread.
	 */
	protected static Object getMutex() {
		return mutex;
	}	

//	protected static final String LOG4J_CONFIG_FILE = "log4j.xml"; 
	protected static final String LOG4J_CONFIG_FILE = "log4j.properties";

	/**
	 * Configures log4j with the file located in {@link #getConfigDir()}+"/log4j.properties"
	 * @throws IOException
	 */
	protected static void initializeLogging() 
	throws IOException
	{
		String logConfFileName = getConfigDir() + File.separatorChar + LOG4J_CONFIG_FILE;		
		File logProp = new File(logConfFileName);
		if (!logProp.exists()){
			// if not there copy
			Utils.copyResource(AbstractApplication.class ,LOG4J_CONFIG_FILE, logConfFileName);		        
		}
		getLogDir();
		setAppNameSystemProperty();
		PropertyConfigurator.configure(logConfFileName);
		logger.info(getApplicationName()+" started.");
	}	

	/**
	 * sets the System Property for the ApplicationName so that the
	 * log4j.properties can access this systemProperty
	 *
	 */
	protected static void setAppNameSystemProperty() 
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
	 * Sets the system property for the applications root dir - the application folder.
	 */
	protected static void setAppFolderSystemProperty(String appFolder) 
	{
		try {
			System.setProperty(APPLICATION_FOLDER_SYSTEM_PROPERTY_NAME, appFolder);    	
		} catch (SecurityException se) {
			System.out.println("System Property "+APPLICATION_FOLDER_SYSTEM_PROPERTY_NAME+" could not be set, to "+appFolder+" because:");
			System.out.println("You dont have the permission to set a System Property");
		} catch (NullPointerException npe) {
			System.out.println("System Property "+APPLICATION_SYSTEM_PROPERTY_NAME+" could not be set, to "+appFolder+" because of a NullPointerException");
			npe.printStackTrace();
		}
	}
	
	
	/**
	 * Implements the application frameworks run method, but delegates to the {@link AbstractApplicationThread}.
	 * <p>
	 * This method will start the application thread created in {@link #init()} and wait until
	 * the mutex accessible by {@link #getMutex()} will be notified by this thread.
	 *  
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
	 * @return The program arguments as passed to the application.
	 */
	public String[] getArguments()
	{
		return arguments;
	}

	private ExceptionHandlingThreadGroup threadGroup = null;	
	/**
	 * Returns and lazyly creates an instance of {@link ExceptionHandlingThreadGroup}.
	 * 
	 * @return An instance of {@link ExceptionHandlingThreadGroup}.
	 */
	protected ThreadGroup getThreadGroup() 
	{
		if (threadGroup == null)
			threadGroup = new ExceptionHandlingThreadGroup(getApplicationName()+"ThreadGroup");
		return threadGroup;
	}

	private AbstractApplicationThread applicationThread = null;
	
	/**
	 * Initializes this application by calling
	 * {@link #initApplicationName()} and
	 * {@link #initApplicationThread(ThreadGroup)}.
	 * <p>
	 * When this method is overridden, make sure super.init() is called.
	 */
	protected void init() 
	{
		applicationName = initApplicationName();
		applicationThread = initApplicationThread(getThreadGroup());
	}

	/**
	 * Should return the application name for this application.
	 * This will be used to choose the application folder.
	 *  
	 * @return the name of the Application
	 */
	public abstract String initApplicationName();

	/**
	 * Should return a new implementation of {@link AbstractApplicationThread}
	 * that is responsible for actually running the application.
	 * <p>
	 * This is done to allow the thread implementation to catch more errors
	 * than the normal application could as the ThreadGroup passed here and
	 * that should be used as parent for the new thread is an instance of
	 * {@link ExceptionHandlingThreadGroup}.
	 * 
	 * @param group the threadGroup The {@link ThreadGroup} to use as group for the new Thread.
	 * @return the Implementation of AbstractApplicationThread for the Application
	 */
	public abstract AbstractApplicationThread initApplicationThread(ThreadGroup group);
}
