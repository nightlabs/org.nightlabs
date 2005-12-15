/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 14.01.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.actions;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;

import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.EditorStateManager;
import org.nightlabs.editor2d.edit.ShapeDrawComponentEditPart;
import org.nightlabs.editor2d.request.EditorRequestConstants;


public class EditShapeAction 
extends SelectionAction 
implements EditorRequestConstants
{  
  public static final String ID = EditShapeAction.class.getName();   
  
  /**
   * @param part
   */
  public EditShapeAction(IWorkbenchPart part) {
    super(part);
  }

  /**
   * returns <code>true</code> if there is exactly 1 EditPart selected that understand
   * a request of type: {@link EditorRequestConstants#REQ_EDIT_SHAPE}.
   * @return <code>true</code> if enabled
   */
  protected boolean calculateEnabled() 
  {
    if (EditorStateManager.getCurrentState() != EditorStateManager.STATE_EDIT_SHAPE) 
    {
    	if (getSelectedObjects().size() == 1 && (getSelectedObjects().get(0) instanceof ShapeDrawComponentEditPart)) {    		
        return true;    		
    	} 
    }    
    return false;
  }
  
  /**
   * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#init()
   */
  protected void init() 
  {
  	super.init();
  	setText(EditorPlugin.getResourceString("action.editshape.text"));
  	setToolTipText(EditorPlugin.getResourceString("action.editshape.tooltip"));
  	setId(ID);
//  	setImageDescriptor(ImageDescriptor.createFromFile(EditorPlugin.class,"icons/editShape16.gif"));
  }  
  
  /**
   * @see org.eclipse.jface.action.IAction#run()
   */
  public void run() 
  {
//    EditorStateManager.setCurrentState(EditorStateManager.STATE_EDIT_SHAPE);
  	if (getSelectedObjects().size() == 1 && (getSelectedObjects().get(0) instanceof ShapeDrawComponentEditPart)) 
  	{    		
  		ShapeDrawComponentEditPart sdcEP = (ShapeDrawComponentEditPart) getSelectedObjects().get(0); 
  	  EditorStateManager.setEditShapeMode(sdcEP);
  	}   	          
  }
    
}
