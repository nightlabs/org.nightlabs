/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 18.04.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.actions;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.edit.AbstractDrawComponentEditPart;
import org.nightlabs.editor2d.request.EditorRequestConstants;
import org.nightlabs.editor2d.request.EditorRotateCenterRequest;


public class ResetRotationCenterAction 
extends SelectionAction
implements EditorRequestConstants
{
  public static final String ID = ResetRotationCenterAction.class.getName();
  
  /**
   * @param part
   */
  public ResetRotationCenterAction(IWorkbenchPart part) 
  {
    super(part);
  }

  protected EditorRotateCenterRequest request = new EditorRotateCenterRequest();
  protected Map dc2RotationCenter;
  
  /* (non-Javadoc)
   * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
   */
  protected boolean calculateEnabled() 
  {
    for (Iterator it = getSelectedObjects().iterator(); it.hasNext(); ) {
      Object o = it.next();
      if (o instanceof EditPart) {
        EditPart editPart = (EditPart) o;
        if (editPart instanceof AbstractDrawComponentEditPart) {
          DrawComponent dc = ((AbstractDrawComponentEditPart) editPart).getDrawComponent();
          if (dc.getRotationX() != DrawComponent.ROTATION_X_DEFAULT || 
              dc.getRotationX() != DrawComponent.ROTATION_Y_DEFAULT) 
          {
            return true;
          }
        }        
      }       
    }
    return false;
  }

  public void run() 
  { 
    for (Iterator it = getSelectedObjects().iterator(); it.hasNext(); ) 
    {
      EditPart editPart = (EditPart) it.next();
      if (editPart instanceof AbstractDrawComponentEditPart) {
        DrawComponent dc = ((AbstractDrawComponentEditPart) editPart).getDrawComponent();
        if (dc.getRotationX() != DrawComponent.ROTATION_X_DEFAULT || 
            dc.getRotationX() != DrawComponent.ROTATION_Y_DEFAULT) 
        {
          dc.setRotationX(DrawComponent.ROTATION_X_DEFAULT);
          dc.setRotationY(DrawComponent.ROTATION_Y_DEFAULT);
        }
      }
    }        
  }
  
  protected void init() 
  {
  	super.init();
  	setText(EditorPlugin.getResourceString("action.resetrotationcenter.label"));
  	setToolTipText(EditorPlugin.getResourceString("action.resetrotationcenter.tooltip"));
  	setId(ID);
//  	setImageDescriptor(ImageDescriptor.createFromFile(EditorPlugin.class,"icons/editShape16.gif"));
  }   
}
