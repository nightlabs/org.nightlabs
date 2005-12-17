/*
 * Created 	on Nov 12, 2004
 * 					by Alexander Bieber
 *
 */
package org.nightlabs.base.exceptionhandler;



/**
 * ExceptionHandlingThreadGroup adds exception handling to 
 * a thread group. It is used internally to extend the mechanism of
 * exception handling in the NightLabs RCP framework. 
 * @author Alexander Bieber
 */
public class ExceptionHandlingThreadGroup extends ThreadGroup {

	/**
	 * @param name
	 */
	public ExceptionHandlingThreadGroup(String name) {
		super(name);
	}

	/**
	 * @param parent
	 * @param name
	 */
	public ExceptionHandlingThreadGroup(ThreadGroup parent, String name) {
		super(parent, name);
	}
	
	

	public void uncaughtException(Thread t, Throwable e) {
		if (!ExceptionHandlerRegistry.syncHandleException(t, e))
			super.uncaughtException(t, e);
//		final ExceptionHandlerRegistry.ExceptionHandlerSearchResult handlerSearch = ExceptionHandlerRegistry.searchHandler(e);
//		Throwable handlingException = null;
//		final Thread thread = t;
//		final Throwable exception = e;
//		if (handlerSearch.getHandler() != null){
//			try {
//				Display.getDefault().syncExec(new Runnable(){
//					public void run(){
//						try {
//							handlerSearch.getHandler().handleException(thread,exception,handlerSearch.getTriggerException());
//						} catch(Throwable x) {
//						}
//					}
//				});
//				
//			} catch (Throwable ex) {
//				handlingException = ex;
//				handlingException.initCause(e);
//		  }
//		  	if (handlingException != null)
//		  		super.uncaughtException(t,handlingException);
//		}
//		else
//			super.uncaughtException(t, e);
	}
}
