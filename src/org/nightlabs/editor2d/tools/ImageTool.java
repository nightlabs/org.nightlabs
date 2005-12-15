/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 21.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
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
  
//  public static final String[] fileExtensions = new String[] {"*.jpg","*.gif","*.png","*.bmp"};
  public static final String[] fileExtensions = new String[] {"*.jpg"};
  protected boolean handleButtonDown(int button) 
  {    
    FileDialog dialog = new FileDialog(getCurrentViewer().getControl().getShell());
    dialog.setFilterExtensions(fileExtensions);
    dialog.setText(EditorPlugin.getResourceString("dialog.choose.image"));
    dialog.open();
    
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
