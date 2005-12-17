/**
 * <p> Project: org.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 20.06.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.base.action;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PartInitException;

import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.io.IOUtil;
import org.nightlabs.base.util.RCPUtil;

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
