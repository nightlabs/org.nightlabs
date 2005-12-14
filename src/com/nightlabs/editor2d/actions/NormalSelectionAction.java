/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 20.01.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.actions;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;

import com.nightlabs.editor2d.EditorPlugin;
import com.nightlabs.editor2d.EditorStateManager;
import com.nightlabs.editor2d.request.EditorRequestConstants;


public class NormalSelectionAction 
extends SelectionAction 
implements EditorRequestConstants
{
  public static final String ID = NormalSelectionAction.class.getName();
  
//  private Request selectionRequest = new EditorEditShapeRequest(REQ_SELECTION);  
//  public Request getSelectionRequest() {
//    return selectionRequest;
//  }
  
  /**
   * @param part
   */
  public NormalSelectionAction(IWorkbenchPart part) {
    super(part);
  }

  /* (non-Javadoc)
   * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
   */
  protected boolean calculateEnabled() 
  {
    if (EditorStateManager.getCurrentState() != EditorStateManager.STATE_NORMAL_SELECTION) {
      return true;
    }
    return false;
//  	if (getSelectedObjects().size() == 1 && (getSelectedObjects().get(0) instanceof ShapeDrawComponentEditPart)) 
//  	{
//  	  ShapeDrawComponentEditPart part = (ShapeDrawComponentEditPart)getSelectedObjects().get(0);
//  		boolean understands = part.understandsRequest(getSelectionRequest()); 
//  		if (understands && EditorStateManager.getCurrentState() == EditorStateManager.STATE_EDIT_SHAPE);
//  			return true;
//  	}
//  	return false;    
  }
  
  /**
   * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#init()
   */
  protected void init() 
  {
  	super.init();
  	setText(EditorPlugin.getResourceString("action.normalselection.text"));
  	setToolTipText(EditorPlugin.getResourceString("action.normalselection.tooltip"));
  	setId(ID);  	
//  	setImageDescriptor(SharedImages.DESC_SELECTION_TOOL_16);
  }  
    
  public void run() 
  {
//	EditorStateManager.setCurrentState(EditorStateManager.STATE_NORMAL_SELECTION);
  	if (!getSelectedObjects().isEmpty()) {
  		EditorStateManager.setNormalSelectionMode(getSelectedObjects());
  	}
  }
  	
}
