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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.tools.CreationTool;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.command.CreateImageCommand;
import org.nightlabs.editor2d.dialog.ConvertImageDialog;
import org.nightlabs.editor2d.image.RenderModeMetaData;
import org.nightlabs.editor2d.model.IModelCreationFactory;
import org.nightlabs.editor2d.request.ImageCreateRequest;

public class ImageTool 
extends CreationTool 
{
	public static final Logger LOGGER = Logger.getLogger(ImageTool.class);
	
//  public ImageTool(CreationFactory aFactory) {
  public ImageTool(IModelCreationFactory aFactory) {	
    this(aFactory, false);
  }

//  public ImageTool(CreationFactory aFactory, boolean colorConversion) 
  public ImageTool(IModelCreationFactory aFactory, boolean colorConversion)  
  {
    super(aFactory);
    this.colorConversion = colorConversion;
  }
    
  protected boolean colorConversion = false;
  
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
  
  // TODO should come from ImageIO
  protected static final String[] fileExtensions = new String[] {"*.jpg", "*.png", "*.gif", "*.bmp", "*.pcx"};
  public String[] getFileExtensions() {
  	return fileExtensions;
  }
//  public String[] getFileExtensions() {
//  	return ImageIO.getReaderFormatNames();
//  }
  
  protected Shell getShell() {
  	return getCurrentViewer().getControl().getShell();
  }
  
  protected FileDialog openFileDialog() 
  {
    FileDialog dialog = new FileDialog(getShell());
    dialog.setFilterExtensions(getFileExtensions());
    dialog.setText(EditorPlugin.getResourceString("dialog.choose.image"));
    dialog.open();
    return dialog;
  }
   
  protected boolean handleButtonDown(int button) 
  {    
  	FileDialog dialog = openFileDialog();  	  	
    if (!dialog.getFileName().equals("")) 
    {
      String fullPathName = dialog.getFilterPath() + File.separator + dialog.getFileName();
      String fileName = dialog.getFileName();
      getImageCreateRequest().setFileName(fullPathName);      
      if (colorConversion) {
      	BufferedImage originalImage;
				try {
					originalImage = ImageIO.read(new File(fullPathName));
					if (originalImage != null) {
		      	ConvertImageDialog convertDialog = new ConvertImageDialog(getShell(), originalImage);
		      	if (convertDialog.open() == Dialog.OK) {
		      		renderModeMetaDatas = convertDialog.getConvertImageComposite().getRenderModeMetaDatas();
		      		doCreation(fullPathName, fileName);		      		
		      	}											
					} else {
						throw new RuntimeException("Image "+fullPathName+" could not be loaded");
					}
				} catch (IOException e) {
					throw new RuntimeException("Image "+fullPathName+" could not be loaded", e);
				}      
			}
      else {
        doCreation(fullPathName, fileName);      	
      }
      return true;
    }
    return false;
  }   
    
  protected void doCreation(String fullFileName, String fileName) 
  {
    ((CreateImageCommand)getCurrentCommand()).setFileName(fullFileName);
    ((CreateImageCommand)getCurrentCommand()).setSimpleFileName(fileName);
    ((CreateImageCommand)getCurrentCommand()).setRenderModeMetaData(renderModeMetaDatas);        
    performCreation(1);  	
  }
    
  protected List<RenderModeMetaData> renderModeMetaDatas = new LinkedList<RenderModeMetaData>();
  
}
