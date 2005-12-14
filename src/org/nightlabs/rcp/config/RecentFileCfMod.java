/**
 * <p> Project: org.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 20.06.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.rcp.config;

import java.util.ArrayList;
import java.util.List;

import org.nightlabs.config.ConfigModule;
import org.nightlabs.config.InitException;

public class RecentFileCfMod 
extends ConfigModule
{
	protected List recentFileNames = null;
	public List getRecentFileNames() {		
		return recentFileNames;
	}
	public void setRecentFileNames(List recentFileNames) {
		this.recentFileNames = recentFileNames;
	}
	
	protected int maxHistoryLength = 5;		
	public int getMaxHistoryLength() {
		return maxHistoryLength;
	}
	public void setMaxHistoryLength(int maxHistoryLength) {
		this.maxHistoryLength = maxHistoryLength;
	}
	
	public void init() 
	throws InitException 
	{
		if (recentFileNames == null)
			recentFileNames = new ArrayList();				
	}
		
}
