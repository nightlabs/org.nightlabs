package org.nightlabs.rcp.app;

import org.apache.log4j.Logger;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;

import org.nightlabs.classloader.DelegatingClassLoader;
import org.nightlabs.classsharing.ClasssharingPlugin;
import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;
import org.nightlabs.rcp.exceptionhandler.ExceptionHandlerEPProcessor;
import org.nightlabs.rcp.exceptionhandler.ExceptionHandlerRegistry;

public abstract class AbstractWorkbenchAdvisor 
extends WorkbenchAdvisor 
{
	public static Logger LOGGER = Logger.getLogger(AbstractWorkbenchAdvisor.class);
	
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
				if (isSystemClassLoaderDelegating()) {
					LOGGER.debug("Initializing classsharing ...");
					ClasssharingPlugin.initializeClassSharing();
					LOGGER.debug("Initializing classsharing ... DONE");
				}
				else
					LOGGER.error("classsharing is enabled, but system classloader is NOT an instance of DelegatingClassLoader! Cannot initialize classsharing!");
			}
			else
				LOGGER.debug("classsharing is disabled - NOT initialized.");
				

			initConfig();
			
  	} catch (Exception e) {
  		throw new RuntimeException(e);
		}		
	}
	
	protected boolean classSharing = true;
	public void setClassSharing(boolean classSharing) {
		this.classSharing = classSharing;
	}	
	
	public static boolean isSystemClassLoaderDelegating() {
		return ClassLoader.getSystemClassLoader() instanceof DelegatingClassLoader;
	}	
	
	private ExceptionHandlerEPProcessor epProcessor = null;
	
	/**
	 * Checks the {@link ExceptionHandlerRegistry} for registered {@link ExceptionHandlerRegistryItem} by invoking
	 * {@link #ExceptionHandlerRegistry.searchHandler(Throwable)}. For the found item the {@link IExceptionHandler.handleException(Throwable)} 
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
//	 initialize the Config
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
			LOGGER.error("Saving config failed!", e);
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
}
