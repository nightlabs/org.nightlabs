/**
 * <p> Project: com.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 05.07.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.rcp.action;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PartInitException;

import com.nightlabs.base.NLBasePlugin;
import com.nightlabs.rcp.io.IOUtil;

public class NewFileAction 
extends Action 
implements INewFileAction
{
	public final String ID = NewFileAction.class.getName() + " " + hashCode();
	
	public NewFileAction() 
	{
		super();
		init();
	}

	protected void init() {
		setId(ID);
	}		
		
	protected String fileExtension;
	public String getFileExtension() {
		return fileExtension;
	}
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}
	
	protected int fileCount = 0;
	protected int nextFileCount() {
		return ++fileCount;
	}
	
	protected String defaultFileName = NLBasePlugin.getResourceString("action.new.unnamed");
	protected String defaultPath = "";
				
	protected String getDefaultPath() 
	{
    if (defaultPath.equals("")) {
    	String tmpDir = System.getProperty("java.io.tmpdir");
    	defaultPath = tmpDir;
    }
    return defaultPath;
	}
	
	public void run() 
	{		
		nextFileCount();		
		File file = createFile(fileExtension);
		try {			
			IOUtil.openFile(file, false);
		} catch (PartInitException e) {			
			e.printStackTrace();
		} 
	}
		 	
	protected File createFile(String fileExtension) 
	{
		String fileName = defaultFileName + fileCount + "." + fileExtension;
		return new File(getDefaultPath(), fileName);		
	}	
}
