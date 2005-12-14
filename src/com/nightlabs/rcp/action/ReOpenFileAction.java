/**
 * <p> Project: com.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 20.06.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.rcp.action;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PartInitException;

import com.nightlabs.base.NLBasePlugin;
import com.nightlabs.rcp.io.IOUtil;
import com.nightlabs.rcp.util.RCPUtil;

public class ReOpenFileAction 
extends Action
{
	public static final String ID = ReOpenFileAction.class.getName();	
	protected String fileName;
	
	public ReOpenFileAction(String fileName) 
	{
		if (fileName == null)
			throw new IllegalArgumentException("Param fileName must not be null!");
		
		this.fileName = fileName;
		init();
	}
	
	protected void init() 
	{
		setId(ID + "-" + fileName + fileName.hashCode());
		setText(fileName);
		setToolTipText(fileName);
	}

	public String getFileName() {
		return fileName;
	}
	
	public void run() 
	{
		try 
		{
			File file = new File(fileName);
			if (file.exists())
				IOUtil.openFile(file);				
		} 
		catch (PartInitException e) 
		{
			RCPUtil.showErrorDialog(
					NLBasePlugin.getResourceString("action.openfile.error.message1")
					+ " " + fileName + " " + 
					NLBasePlugin.getResourceString("action.openfile.error.message2")
			);	
			e.printStackTrace();			
		}
	}
		
}
