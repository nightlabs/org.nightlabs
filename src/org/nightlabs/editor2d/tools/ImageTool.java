/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

package org.nightlabs.editor2d.tools;

import java.io.File;

import org.eclipse.gef.Request;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.tools.CreationTool;
import org.eclipse.swt.widgets.FileDialog;

import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.command.CreateImageCommand;
import org.nightlabs.editor2d.request.ImageCreateRequest;

public class ImageTool 
extends CreationTool 
{
  public ImageTool(CreationFactory aFactory) {
    super(aFactory);
  }

  /**
   * Creates a {@link CreateRequest} and sets this tool's factory on the request.
   * @see org.eclipse.gef.tools.TargetingTool#createTargetRequest()
   */
  protected Request createTargetRequest() 
  {
    ImageCreateRequest request = new ImageCreateRequest();
    request.setFactory(getFactory());
    return request;
  }   
  
  protected ImageCreateRequest getImageCreateRequest() {
    return (ImageCreateRequest) getTargetRequest();
  }
  
  protected static final String[] fileExtensions = new String[] {"*.jpg", "*.png", "*.gif", "*.bmp"};
  // TODO should come from ImageFormatRegistry
  public String[] getFileExtensions() {
  	return fileExtensions;
  }
  
  protected FileDialog openFileDialog() 
  {
    FileDialog dialog = new FileDialog(getCurrentViewer().getControl().getShell());
    dialog.setFilterExtensions(getFileExtensions());
    dialog.setText(EditorPlugin.getResourceString("dialog.choose.image"));
    dialog.open();
    return dialog;
  }
  
  protected boolean handleButtonDown(int button) 
  {    
//    FileDialog dialog = new FileDialog(getCurrentViewer().getControl().getShell());
//    dialog.setFilterExtensions(fileExtensions);
//    dialog.setText(EditorPlugin.getResourceString("dialog.choose.image"));
//    dialog.open();
  	FileDialog dialog = openFileDialog();
  	
    if (!dialog.getFileName().equals("")) 
    {
      String fullPathName = dialog.getFilterPath() + File.separator + dialog.getFileName(); 
      getImageCreateRequest().setFileName(fullPathName);
      ((CreateImageCommand)getCurrentCommand()).setFileName(fullPathName);
      ((CreateImageCommand)getCurrentCommand()).setSimpleFileName(dialog.getFileName());
      performCreation(1);
      return true;
    }
    return false;
  }   
}
