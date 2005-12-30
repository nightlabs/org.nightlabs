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

package org.nightlabs.base.io;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import org.nightlabs.base.action.Editor2PerspectiveRegistry;

public class IOUtil
{
  public static String getFullFileName(String pathName, String fileName) 
  {
  	if (pathName == null)
			throw new IllegalArgumentException("Param pathName must not be null!");
  	
  	if (fileName == null)  		
			throw new IllegalArgumentException("Param fileName must not be null!");
  	
    StringBuffer sb = new StringBuffer();
    String pathSeparator = File.separator;
    sb.append(pathName);
    sb.append(pathSeparator);
    sb.append(fileName);
    return sb.toString();
  }
  
  /**
   * Opens an Editor (IEditorPart) for the given file, based on the FileExtension of the Editor
   * and the EditorRegistry of the Workbench.
   * @param file The file to open
   * @param saved determines if the created FileEditorInput should be marked as saved or not
   * return true if an editor could be found for this file and the editors opened
   * return false if no editor could be found for this file
   */
  public static boolean openFile(File file, boolean saved)
  throws PartInitException
  {
  	if (file == null)
			throw new IllegalArgumentException("Param file must not be null!");

  	IPerspectiveRegistry perspectiveRegistry = PlatformUI.getWorkbench().getPerspectiveRegistry();
  	IEditorRegistry editorRegistry = PlatformUI.getWorkbench().getEditorRegistry();  	
		IEditorDescriptor editorDescriptor = editorRegistry.getDefaultEditor(file.getName());
		if (editorDescriptor != null) {
			String editorID = editorDescriptor.getId();
	  	String perspectiveID = Editor2PerspectiveRegistry.sharedInstance().getPerspectiveID(editorID);
	  	if (perspectiveID != null) {
	  		IPerspectiveDescriptor perspectiveDescriptor = perspectiveRegistry.findPerspectiveWithId(perspectiveID);
	  		if (perspectiveDescriptor != null) 
	  		{
		  	  try {
			  		IWorkbench workbench = PlatformUI.getWorkbench();	  	  	
						workbench.showPerspective(perspectiveID, 
						    workbench.getActiveWorkbenchWindow());
					} catch (WorkbenchException e) {
						throw new PartInitException("Perspective width ID "+perspectiveID+" could not be opend", e);
					}	  			  			
	  		}
	  	}
			FileEditorInput input = new FileEditorInput(file);
			input.setSaved(saved);
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, editorID);			
			return true;
		}
		return false;
  }
   
  public static boolean openFile(File file) 
  throws PartInitException 
  {
  	return openFile(file, true);
  }
    
  public static IPath getPath(File file) 
  {
  	String path = null;
  	try {
			path = file.getCanonicalPath();
		} catch (IOException e) {			
			path = file.getAbsolutePath();
		}
		return new Path(path);
  }

  /**
   * 
   * @param file the File to determine the fileExtension
   * @return the fileExtension or null if no fileExtension could be found
   */
  public static String getFileExtension(File file) 
  {
    if (file == null)
      throw new IllegalArgumentException("Param file must not be null!");
   
  	StringBuffer sb = new StringBuffer(file.getName());
  	int index = sb.lastIndexOf(".");  	
  	if (index == -1) {
  		return null;
  	}
  	return sb.substring(index+1);
  }  
  
  /**
   * 
   * @param fileName the name of the File
   * @return the fileExtension or null if no fileExtension could be found
   */
  public static String getFileExtension(String fileName) 
  {
  	StringBuffer sb = new StringBuffer(fileName);
  	int index = sb.lastIndexOf(".");  	
  	if (index == -1) {
  		return null;
  	}
  	return sb.substring(index+1);  	
  }
  
}
