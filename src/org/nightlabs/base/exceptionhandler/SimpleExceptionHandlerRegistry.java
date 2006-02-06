package org.nightlabs.base.exceptionhandler;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;
import org.nightlabs.base.extensionpoint.IEPProcessListener;

/**
 * Processes extensions of <code>simpleExceptionHandler</code>s by registering to
 * {@link ExceptionHandlerRegistry}s processListener which calls method <code>postProcess</code>
 * after having processed all of its EPs.
 * 
 * @author Tobias Langner <tobias[DOT]langner[AT]gmx[DOT]net>
 */
public class SimpleExceptionHandlerRegistry
extends AbstractEPProcessor
implements IEPProcessListener
{
	public static final String ExtensionPointID = "org.nightlabs.base.simpleExceptionHandler";
	private static SimpleExceptionHandlerRegistry sharedInstance;

	@Override
	public String getExtensionPointID()
	{
		return ExtensionPointID;
	}

	@Override
	public void processElement(IExtension extension, IConfigurationElement element) throws EPProcessorException
	{
		try
		{
			if (element.getName().equals("simpleExceptionHandler"))
			{
				String targetType = element.getAttribute("targetType");
				String message = element.getAttribute("message");
				message = ( message != null && !message.equals("") ? message : null );
				IExceptionHandler handler = new SimpleExceptionHandler(message);				
				ExceptionHandlerRegistry.sharedInstance().addExceptionHandler(targetType, handler);
			}
			else
			{
				// wrong element according to schema, probably checked earlier
				throw new IllegalArgumentException("Element "+element.getName()+" is not supported by extension-point "+ExtensionPointID);
			}
		}
		catch(Throwable e)
		{
			throw new EPProcessorException(e);
		}
	}

	public void preProcess()
	{
		
	}

	public void postProcess()
	{
		checkProcessing();
	}
	
	public static SimpleExceptionHandlerRegistry sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new SimpleExceptionHandlerRegistry();
		}
		return sharedInstance;
	}
}
