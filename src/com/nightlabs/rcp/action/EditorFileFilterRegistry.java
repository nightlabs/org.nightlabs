/**
 * <p> Project: com.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 01.07.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/

package com.nightlabs.rcp.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

import com.nightlabs.rcp.extensionpoint.AbstractEPProcessor;
import com.nightlabs.rcp.extensionpoint.EPProcessorException;

public class EditorFileFilterRegistry 
extends AbstractEPProcessor 
{
	public static final String EXTENSION_POINT_ID = "com.nightlabs.base.editorfilefilter";

	private List patterns = new ArrayList();
	
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	public void processElement(IExtension extension, IConfigurationElement element) 
	throws EPProcessorException 
	{
		if (element.getName().equalsIgnoreCase("editorFileFilter")) {
			String pattern = element.getAttribute("pattern");
			if (pattern == null || "".equals(pattern))
				throw new EPProcessorException("Element editorFileFilter has to define attribute pattern.");
			patterns.add(pattern);
		}
	}

	private String globalPattern;
	
	public String getGlobalPattern() 
	{
		checkProcessing();
		
		if (globalPattern == null){
			StringBuffer globalPatternSB = new StringBuffer("^");
			for (Iterator it = patterns.iterator(); it.hasNext(); ) {
				globalPatternSB.append('(');
				globalPatternSB.append(it.next());
				globalPatternSB.append(')');
				if (it.hasNext())
					globalPatternSB.append('|');
			}
			globalPatternSB.append('$');
			globalPattern = globalPatternSB.toString();
		}
		return globalPattern;			
	}
	
	public boolean doesMatchEditorID(String id) 
	{
		checkProcessing();
		if (patterns.size() == 0)
			return true;
		return Pattern.matches(getGlobalPattern(), id);
	}

	public List getPatterns() 
	{
		checkProcessing();
		return patterns;
	}
	
	private static EditorFileFilterRegistry sharedInstance;
	
	/**
	 * @deprecated Use {@link #sharedInstance()} instead
	 */
	public static EditorFileFilterRegistry getSharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new EditorFileFilterRegistry();
		return sharedInstance;
	}
	
	public static EditorFileFilterRegistry sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new EditorFileFilterRegistry();
		return sharedInstance;
	}
}
