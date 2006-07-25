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

package org.nightlabs.base.io;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;
import org.nightlabs.io.IOFilter;
import org.nightlabs.io.IOFilterMan;

public class IOFilterRegistry 
extends AbstractEPProcessor 
{
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.ioFilterRegistry";
	
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
