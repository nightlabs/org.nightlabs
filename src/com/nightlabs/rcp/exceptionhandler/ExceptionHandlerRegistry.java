/*
 * Created on Sep 23, 2004
 *
 */
package com.nightlabs.rcp.exceptionhandler;

import java.util.HashMap;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;

import com.nightlabs.rcp.extensionpoint.EPProcessorException;

/**
 * Maintains a Map of {@link IExceptionHandler} and is able 
 * to search the right handler for an exception.
 * This class staticly holds a default registry as singleton static member and 
 * provides some static convenience methods statically which
 * work with the default shared instance.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de> 
 */
public class ExceptionHandlerRegistry {
	public static final Logger LOGGER = Logger.getLogger(ExceptionHandlerRegistry.class);
	
	private HashMap exceptionHandlers = new HashMap();

	// Dummy object to provide thread safety
	// IMPROVE other synchronization strategy
	private Object synchronizedObject = new Object();
	
	protected HashMap getExceptionHandlers(){
		return exceptionHandlers;
	}
	
	public void addExceptionHandler(String targetType, IExceptionHandler handler){		
		synchronized(synchronizedObject){
			ensureProcessingDone();
			if (exceptionHandlers.containsKey(targetType))
				throw new DuplicateHandlerRegistryException("An exceptionHandler was already defined for "+targetType);
			exceptionHandlers.put(targetType,handler);
		}
	}
	
	/**
	 * @param targetType
	 * @return The registered IExceptionHandler for the given targetType. Null if none registered. 
	 */
	protected IExceptionHandler getExceptionHandler(String targetType){
		synchronized(synchronizedObject){
			ensureProcessingDone();
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
	 * @return  
	 */
	protected boolean haveHandler(Class targetType){
		return exceptionHandlers.containsKey(targetType.getName());
	}
	
	// TODO: Maybe this should be static as EPs should only be processed once
	private ExceptionHandlerEPProcessor epProcessor = null;
	
	protected void ensureProcessingDone() {
		synchronized(synchronizedObject){
			if (epProcessor == null){
				try {
					epProcessor = new ExceptionHandlerEPProcessor();
					epProcessor.process();
				} catch (EPProcessorException e) {
					// TODO Add handler code here
					e.printStackTrace();
				}
			}
		}
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
	 * @return
	 */
	public ExceptionHandlerSearchResult searchHandler(Throwable exception){
		// make sure the registrations where made
		ensureProcessingDone();

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
							LOGGER.fatal("Exception occured while handling exception on GUI thread!", x);
						}
					}
				};

				if (async)
					Display.getDefault().asyncExec(runnable);
				else
					Display.getDefault().syncExec(runnable);
				
			} catch (Throwable ex) {
				LOGGER.fatal("Exception occured while handling exception on causing thread!", ex);
		  }
			return true;
		}
		else {
			LOGGER.fatal("Did not find an ExceptionHandler for this Throwable!", exception);
			return false;
		}
	}
	
	
	private static ExceptionHandlerRegistry sharedInstance;
	
	public static ExceptionHandlerRegistry sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new ExceptionHandlerRegistry();
		}
		return sharedInstance;
	}
}