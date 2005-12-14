/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 02.05.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.actions;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;

import com.nightlabs.editor2d.EditorPlugin;
import com.nightlabs.editor2d.EditorStateManager;
import com.nightlabs.editor2d.request.EditorRequestConstants;


public class RotateAction 
extends SelectionAction 
implements EditorRequestConstants
{
  public static final String ID = RotateAction.class.getName();
  public RotateAction(IWorkbenchPart part) 
  {
    super(part);
  }

  protected void init() 
  {
  	super.init();
  	setText(EditorPlugin.getResourceString("action.rotate.text"));
  	setToolTipText(EditorPlugin.getResourceString("action.rotate.tooltip"));
  	setId(ID);
//  	setImageDescriptor(ImageDescriptor.createFromFile(EditorPlugin.class,"icons/editShape16.gif"));
  } 
  
  protected boolean calculateEnabled() 
  {
  	if (EditorStateManager.getCurrentState() != EditorStateManager.STATE_ROTATE) {
  	  return true;
  	}
  	return false;
  }
    
  public void run() 
  {
  	if (!getSelectedObjects().isEmpty()) {
  	  EditorStateManager.setRotateMode(getSelectedObjects());
  	}    
  }
  
}
