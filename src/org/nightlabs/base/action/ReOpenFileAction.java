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
import org.eclipse.jface.dialogs.MessageDialog;
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
			MessageDialog.openError(RCPUtil.getActiveWorkbenchShell(),
					"Error", 
					NLBasePlugin.getResourceString("action.openfile.error.message1")
					+ " " + fileName + " " + 
					NLBasePlugin.getResourceString("action.openfile.error.message2")
			);			
			e.printStackTrace();			
		}
	}
		
}
