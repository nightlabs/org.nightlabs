package org.nightlabs.rcp.io;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

import org.nightlabs.io.IOFilter;
import org.nightlabs.io.IOFilterMan;
import org.nightlabs.rcp.extensionpoint.AbstractEPProcessor;
import org.nightlabs.rcp.extensionpoint.EPProcessorException;

public class IOFilterRegistry 
extends AbstractEPProcessor 
{
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.ioFilterRegistry";
	public static final Logger LOGGER = Logger.getLogger(IOFilterRegistry.class.getName());
	
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	protected static IOFilterRegistry _sharedInstance;
	public static IOFilterRegistry sharedInstance() 
	{		
		if (_sharedInstance == null) {
			_sharedInstance = new IOFilterRegistry();
//			_sharedInstance.process();
		}					
		return _sharedInstance;
	}
	
	protected IOFilterRegistry() {
		super();
	}	
	
	public static final String ELEMENT_IOFILTER = "ioFilter";
	public static final String ATTRIBUTE_NAME = "name";
	public static final String ATTRIBUTE_DESCRIPTION = "description";
	public static final String ATTRIBUTE_CLASS = "class";
	public static final String ATTRIBUTE_FILE_EXTENSION = "fileExtension";
	
	
	public void processElement(IExtension extension, IConfigurationElement element)
	throws EPProcessorException 
	{
		if (ioFilterMan == null)
			ioFilterMan = new IOFilterMan();
		
		if (element.getName().equalsIgnoreCase(ELEMENT_IOFILTER)) 
		{
			try {
				String name = element.getAttribute(ATTRIBUTE_NAME);
				String description = element.getAttribute(ATTRIBUTE_DESCRIPTION);
				String fileExtension = element.getAttribute(ATTRIBUTE_FILE_EXTENSION);
				
				Object ioFilter = (Object) element.createExecutableExtension(ATTRIBUTE_CLASS);
				if (!(ioFilter instanceof IOFilter))
					throw new IllegalArgumentException("Attribute class must implement "+IOFilter.class.getName()+" "+ioFilter.getClass().getName()+" does not!");
				IOFilter filter = (IOFilter) ioFilter; 
				
				if (checkString(description))
					filter.setDescription(description);
				if (checkString(fileExtension))
					filter.setFileExtension(fileExtension);
				
				ioFilterMan.addIOFilter(filter);
			}
			catch (Exception e) {
				throw new EPProcessorException(e);
			}
		}
	}
	
	protected IOFilterMan ioFilterMan = null;
	public IOFilterMan getIOFilterMan() 
	{
		checkProcessing();
		
		if (ioFilterMan == null) 
			ioFilterMan = new IOFilterMan();
		
		return ioFilterMan;
	}

}
