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

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.io.IOUtil;

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
			throw new RuntimeException(e);
		} 
	}
		 	
	protected File createFile(String fileExtension) 
	{
		String fileName = defaultFileName + fileCount + "." + fileExtension;
		return new File(getDefaultPath(), fileName);		
	}	
}
