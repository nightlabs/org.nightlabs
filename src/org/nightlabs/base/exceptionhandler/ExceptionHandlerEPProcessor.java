/*
 * Created on Sep 23, 2004
 *
 */
package org.nightlabs.base.exceptionhandler;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;

/**
 * Helper class processes exceptionHandler extension-point.
 * Finds elements and makes registrations in {@link org.nightlabs.base.exceptionhandler.ExceptionHandlerRegistry}.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public class ExceptionHandlerEPProcessor extends AbstractEPProcessor{
	
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
			if (element.getName().toLowerCase().equals("exceptionhandler")){
				String targetType = element.getAttribute("targetType");
				String handlerClassStr = element.getAttribute("class");
				IExceptionHandler handler = (IExceptionHandler) element.createExecutableExtension("class");
				if (!IExceptionHandler.class.isAssignableFrom(handler.getClass()))
				throw new IllegalArgumentException("Specified class for element exceptionHandler must implement "+IExceptionHandler.class.getName()+". "+handler.getClass().getName()+" does not.");
				ExceptionHandlerRegistry.sharedInstance().addExceptionHandler(targetType,handler);
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
}
