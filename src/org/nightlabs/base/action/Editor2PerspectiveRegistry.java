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

package org.nightlabs.base.action;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;

public class Editor2PerspectiveRegistry 
extends AbstractEPProcessor 
{
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.editor2perspective";
	
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	private static Editor2PerspectiveRegistry sharedInstance;
	/**
	 * @deprecated Use {@link #sharedInstance()} instead
	 */
	public static Editor2PerspectiveRegistry getSharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new Editor2PerspectiveRegistry();
		return sharedInstance;
	}
	
	public static Editor2PerspectiveRegistry sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new Editor2PerspectiveRegistry();
		return sharedInstance;
	}	
		
	protected Map<String, String> editorID2PerspectiveID = new HashMap<String, String>();
	public String getPerspectiveID(String editorID) 
	{
		checkProcessing();
		return (String) editorID2PerspectiveID.get(editorID);
	}
	
	public void processElement(IExtension extension, IConfigurationElement element)
	throws EPProcessorException 
	{
		if (element.getName().equalsIgnoreCase("registry")) 
		{
			String editorID = element.getAttribute("editorID");
			if (!checkString(editorID))
				throw new EPProcessorException("Element registry has to define attribute editorID.");
			
			String perspectiveID = element.getAttribute("perspectiveID");
			if (!checkString(perspectiveID))
				throw new EPProcessorException("Element registry has to define attribute perspectiveID.");
			
			editorID2PerspectiveID.put(editorID, perspectiveID);
		}
	}
	
}
