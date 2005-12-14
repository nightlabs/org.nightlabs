/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 06.09.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.outline.filter;

public interface FilterNameProvider
{
	/**
	 * 
	 * @param c The Class to get a Localized Filter String for
	 * @return The localized String for the filter
	 */
	public String getTypeName(Class c);
}
