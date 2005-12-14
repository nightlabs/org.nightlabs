/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 10.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.tools;

import org.eclipse.gef.Request;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.tools.CreationTool;
import org.eclipse.jface.dialogs.Dialog;

import com.nightlabs.editor2d.dialog.CreateTextDialog;
import com.nightlabs.editor2d.request.TextCreateRequest;

public class TextTool 
extends CreationTool
{

  /**
   * Creates a {@link CreateRequest} and sets this tool's factory on the request.
   * @see org.eclipse.gef.tools.TargetingTool#createTargetRequest()
   */
  protected Request createTargetRequest() 
  {
    TextCreateRequest request = new TextCreateRequest();
    request.setFactory(getFactory());
    return request;
  }  
  
  public TextCreateRequest getTextCreateRequest() 
  {
    return (TextCreateRequest) getTargetRequest();
  }
  
  protected boolean handleButtonDown(int button) 
  {
    CreateTextDialog dialog = new CreateTextDialog(
        getCurrentViewer().getControl().getShell(), 
        getTextCreateRequest()
      );
    dialog.open();
        
    if (dialog.getReturnCode() == Dialog.OK) 
    {
      performCreation(1);
      return true;
    }
    return false;
  } 
    
  public TextTool(CreationFactory factory) {
    super(factory);
  }

}
