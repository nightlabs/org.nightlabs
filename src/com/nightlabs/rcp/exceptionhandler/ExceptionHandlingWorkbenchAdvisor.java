/*
 * Created on Sep 24, 2004
 */
package com.nightlabs.rcp.exceptionhandler;

import org.eclipse.ui.application.WorkbenchAdvisor;


/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public abstract class ExceptionHandlingWorkbenchAdvisor extends WorkbenchAdvisor {
			
	private ExceptionHandlerEPProcessor epProcessor = null;
	/**
	 * Checks the {@link ExceptionHandlerRegistry} for registered {@link ExceptionHandlerRegistryItem} by invoking
	 * {@link #ExceptionHandlerRegistry.searchHandler(Throwable)}. For the found item the {@link IExceptionHandler.handleException(Throwable)} 
	 * method is invoked on a unique instance of the eventHandler class (plugin.xml). 
	 */
	public void eventLoopException(Throwable exception) {
		if (!ExceptionHandlerRegistry.syncHandleException(exception))
			super.eventLoopException(exception);
//    // find the proper handler
//		ExceptionHandlerRegistry.ExceptionHandlerSearchResult handlerSearch = ExceptionHandlerRegistry.searchHandler(exception);
//		Throwable handlingException = null;
//		if (handlerSearch.getHandler() != null){
//			try {
//				handlerSearch.getHandler().handleException(Thread.currentThread(),exception,handlerSearch.getTriggerException());
//			} catch (Throwable e) {
//				handlingException = e;
//				handlingException.initCause(exception);
//		  }
//		  	if (handlingException != null)
//		  		super.eventLoopException(handlingException);
//		}
//		else
//			super.eventLoopException(exception);
  }    
}
