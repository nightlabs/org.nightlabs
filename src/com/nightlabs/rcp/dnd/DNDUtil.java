/**
 * <p> Project: com.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 04.10.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.rcp.dnd;

public class DNDUtil
{

	public DNDUtil() {
		super();
	}
	
	public static final String DROP_NONE = "DROP_NONE";
	public static final String DROP_COPY = "DROP_COPY";
	public static final String DROP_MOVE = "DROP_MOVE";
	public static final String DROP_LINK = "DROP_LINK";
	public static final String DROP_TARGET_MOVE = "DROP_TARGET_MOVE";
	public static final String DROP_DEFAULT = "DROP_DEFAULT";
	
	public static String getStringForCode(int value) 
	{
		StringBuffer sb = new StringBuffer();
		
		if (value == 0)
			sb.append(DROP_NONE + " ");
		else if (value == (1 << 0))
			sb.append(DROP_COPY + " ");
		else if (value == (1 << 1))
			sb.append(DROP_MOVE + " ");
		else if (value == (1 << 2))
			sb.append(DROP_LINK + " ");
		else if (value == (1 << 3))
			sb.append(DROP_TARGET_MOVE + " ");
		else if (value == (1 << 4))
			sb.append(DROP_DEFAULT + " ");

		return sb.toString();
	}

}
