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

package org.nightlabs.base.exceptionhandler;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.swt.widgets.Display;

import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;

/**
 * Maintains a Map of {@link IExceptionHandler} and is able 
 * to search the right handler for an exception.
 * This class staticly holds a default registry as singleton static member and 
 * provides some static convenience methods statically which
 * work with the default shared instance.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de> 
 */
public class ExceptionHandlerRegistry extends AbstractEPProcessor {
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(ExceptionHandlerRegistry.class);
	
	private Map exceptionHandlers = new HashMap();

	// Dummy object to provide thread safety
	// IMPROVE other synchronization strategy
	private Object synchronizedObject = new Object();
	
	protected Map getExceptionHandlers(){
		return exceptionHandlers;
	}
	
	public void addExceptionHandler(String targetType, IExceptionHandler handler)
	{		
		synchronized(synchronizedObject)
		{
//			if (exceptionHandlers.containsKey(targetType))
//				throw new DuplicateHandlerRegistryException("An exceptionHandler was already defined for "+targetType);
			logger.debug("An exceptionHandler was already defined for "+targetType+" !");
			
			exceptionHandlers.put(targetType,handler);
		}
	}
	
	/**
	 * @param targetType
	 * @return The registered IExceptionHandler for the given targetType. Null if none registered. 
	 */
	protected IExceptionHandler getExceptionHandler(String targetType){
		synchronized(synchronizedObject){
			checkProcessing();
			if (exceptionHandlers.containsKey(targetType))
				return (IExceptionHandler)exceptionHandlers.get(targetType);
			else
				return null;
		}
	}
	
	
	/**
	 * Removes the registration of the given targetType
	 * @param targetType
	 */
	public void removeExceptionHandler(String targetType){
		synchronized(synchronizedObject){
			if (exceptionHandlers.containsKey(targetType))
				exceptionHandlers.remove(targetType);
		}
	}
	
	/**
	 * @param targetType
	 * @return The registered IExceptionHandler for the given targetType. Null if none registered. 
	 */
	protected IExceptionHandler getExceptionHandler(Class targetType){
		return getExceptionHandler(targetType.getName());
	}

	/**
	 * Checks if a IExceptionHandler is registered for a specific Class
	 * @param targetType
	 */
	protected boolean haveHandler(Class targetType){
		return exceptionHandlers.containsKey(targetType.getName());
	}
	
	public ExceptionHandlerSearchResult getTopLevelCauseHandler(Throwable x)
	{
		ExceptionHandlerSearchResult handler = null;
		Throwable cause = x.getCause();
		if(cause != null)
			handler = getTopLevelCauseHandler(cause);
		if((handler == null) || (cause == null)) {
			if (haveHandler(x.getClass())) {
				IExceptionHandler eHandler = getExceptionHandler(x.getClass());
				handler = new ExceptionHandlerSearchResult();
				handler.setHandler(eHandler);
				handler.setTriggerException(x);
			}
		}
		return handler;
	}
	
	/**
	 * Used as result for {@link ExceptionHandlerRegistry#searchHandler(Throwable)}
	 * @author Alexander Bieber
	 */
	public static class ExceptionHandlerSearchResult {
		private Throwable triggerException;
		private IExceptionHandler handler;
		
		public IExceptionHandler getHandler() {
			return handler;
		}
		public void setHandler(IExceptionHandler handler) {
			this.handler = handler;
		}
		public Throwable getTriggerException() {
			return triggerException;
		}
		public void setTriggerException(Throwable triggerException) {
			this.triggerException = triggerException;
		}
	}
	
	/**
	 * Finds registered ExceptionHandlers. Moves up the class hierarchy for the 
	 * passed exception itself and all its nested cause exceptions to find 
	 * a handler for the specific class.  
	 * Returns null if no handler could be found.
	 * @param exception
	 */
	public ExceptionHandlerSearchResult searchHandler(Throwable exception){
		// make sure the registrations where made
		checkProcessing();

		ExceptionHandlerSearchResult rootCauseResult = getTopLevelCauseHandler(exception);
		if (rootCauseResult != null)
			return rootCauseResult;
		
		Class classRun = exception.getClass();
		Throwable exceptionRun = exception;		
		while (exceptionRun != null) {
			
		  classRun = exceptionRun.getClass();
		  while ( (!haveHandler(classRun)) && (!classRun.equals(Throwable.class)) ) {
			  classRun = classRun.getSuperclass();
		  }
			
		  if (!classRun.equals(Throwable.class))
		  	if (haveHandler(classRun))
		  		break;
		  
//		  exceptionRun = exceptionRun.getCause();
			exceptionRun = ExceptionUtils.getCause(exceptionRun);
		}
		
		ExceptionHandlerSearchResult result = new ExceptionHandlerSearchResult();
		result.setHandler(getExceptionHandler(classRun));
		if (exceptionRun == null)
			exceptionRun = exception;
		result.setTriggerException(exceptionRun);
		// returns null if none registered
		return result;
	}
	
	/**
	 * This method executes an ExceptionHandler on the GUI thread
	 * and does not wait for it. If this method is executed on the GUI
	 * thread, it will block, though.
	 *
	 * @param exception
	 */
	public static void asyncHandleException(Throwable exception)
	{
		sharedInstance().handleException(Thread.currentThread(), exception, true);
	}

	/**
	 * This method executes an ExceptionHandler on the GUI thread
	 * and does not wait for it. If this method is executed on the GUI
	 * thread, it will block, though.
	 *
	 * @param exception
	 */
	public static boolean asyncHandleException(Thread thread, Throwable exception)
	{
		return sharedInstance().handleException(thread, exception, true);
	}

	/**
	 * This method can be executed on every thread. It executes an
	 * ExceptionHandler on the GUI thread and waits for it to return. 
	 *
	 * @param exception
	 */
	public static boolean syncHandleException(Throwable exception)
	{
		return sharedInstance().handleException(Thread.currentThread(), exception, false);
	}

	/**
	 * This method can be executed on every thread. It executes an
	 * ExceptionHandler on the GUI thread and waits for it to return. 
	 *
	 * @param exception
	 */
	public static boolean syncHandleException(Thread thread, Throwable exception)
	{
		return sharedInstance().handleException(thread, exception, false);
	}

	private boolean handleException(final Thread thread, final Throwable exception, boolean async)
	{
		// TODO: Do we need to find out on which thread we are, or is syncExec intelligent enough?
		Throwable handlingException = null;
		final ExceptionHandlerSearchResult handlerSearch = sharedInstance().searchHandler(exception);
		if (handlerSearch.getHandler() != null){
			try {
				Runnable runnable = new Runnable(){
					public void run(){
						try {
							handlerSearch.getHandler().handleException(
									thread,exception, handlerSearch.getTriggerException());
						} catch(Throwable x) {
							logger.fatal("Exception occured while handling exception on GUI thread!", x);
						}
					}
				};

				if (async)
					Display.getDefault().asyncExec(runnable);
				else
					Display.getDefault().syncExec(runnable);
				
			} catch (Throwable ex) {
				logger.fatal("Exception occured while handling exception on causing thread!", ex);
		  }
			return true;
		}
		else {
			logger.fatal("Did not find an ExceptionHandler for this Throwable!", exception);
			return false;
		}
	}
	
	public static final String ExtensionPointID = "org.nightlabs.base.exceptionhandler";
	/**
	 * Processes exceptionHandler extension-point elements.
	 * For each element one instance of exceptionHandler.class is registered
	 * in the {@link ExceptionHandlerRegistry}. 
	 * @param element
	 * @throws EPProcessorException
	 */
	public void processElement(IExtension extension, IConfigurationElement element) 
	throws EPProcessorException
	{
		try{
			if (element.getName().toLowerCase().equals("exceptionhandler")) {
				String targetType = element.getAttribute("targetType");
				String handlerClassStr = element.getAttribute("class");
				IExceptionHandler handler = (IExceptionHandler) element.createExecutableExtension("class");
				if (!IExceptionHandler.class.isAssignableFrom(handler.getClass()))
					throw new IllegalArgumentException("Specified class for element exceptionHandler must implement "+IExceptionHandler.class.getName()+". "+handler.getClass().getName()+" does not.");
				ExceptionHandlerRegistry.sharedInstance().addExceptionHandler(targetType, handler);
			}
			else {
				// wrong element according to schema, probably checked earlier
				throw new IllegalArgumentException("Element "+element.getName()+" is not supported by extension-point "+ExtensionPointID);
			}
		}catch(Throwable e){
			throw new EPProcessorException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	public String getExtensionPointID() {
		return ExtensionPointID;
	}
	
	
	
	private static ExceptionHandlerRegistry sharedInstance;
	
	public static ExceptionHandlerRegistry sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new ExceptionHandlerRegistry();
		}
		return sharedInstance;
	}
}
