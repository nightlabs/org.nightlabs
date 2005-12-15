/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 16.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.tools;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.Request;
import org.eclipse.swt.graphics.Cursor;

import org.nightlabs.editor2d.custom.EditorCursors;
import org.nightlabs.editor2d.edit.AbstractDrawComponentEditPart;
import org.nightlabs.editor2d.request.EditorRotateRequest;
import org.nightlabs.editor2d.util.EditorUtil;

public class RotateTracker 
extends AbstractDragTracker 
{
  public static final Logger LOGGER = Logger.getLogger(RotateTracker.class);  
  protected int direction; 
  
  public RotateTracker(AbstractDrawComponentEditPart owner, int direction)
  {
    super(owner);
    this.direction = direction;
  }

  protected AbstractDrawComponentEditPart getAbstractDrawComponentEditPart() {
    return (AbstractDrawComponentEditPart) owner;
  }
  
  /**
   * @see org.eclipse.gef.tools.SimpleDragTracker#createSourceRequest()
   */
  protected Request createSourceRequest() 
  {
    EditorRotateRequest rotateRequest = new EditorRotateRequest();
    rotateRequest.setType(REQ_ROTATE);
    rotateRequest.setLocation(getLocation());   
    rotateRequest.setEditParts(getCurrentViewer().getSelectedEditParts());    
    return rotateRequest;
  }
        
  protected String getCommandName() 
  {
    return REQ_ROTATE;
  }

  /**
   * @see org.eclipse.gef.tools.AbstractTool#getDefaultCursor()
   */
  protected Cursor getDefaultCursor() 
  {
    return EditorCursors.ROTATE;
  }  
          
  protected Point rotationCenter;
  
  /**
   * @see org.eclipse.gef.tools.SimpleDragTracker#updateSourceRequest()
   */
  protected void updateSourceRequest() 
  {
    if (getEditorRotateRequest().getRotationCenter() == null && owner != null) 
    {
	    if (getEditorRotateRequest().getEditParts().size() > 1) {
	      rotationCenter = EditorUtil.getCenter(getEditorRotateRequest().getEditParts());
	      getEditorRotateRequest().setRotationCenter(rotationCenter);	      
	      getEditorRotateRequest().setMultiple(true);
	    }
	    else {      
        rotationCenter = new Point(getAbstractDrawComponentEditPart().getDrawComponent().getRotationX(),
            getAbstractDrawComponentEditPart().getDrawComponent().getRotationY());
        getEditorRotateRequest().setRotationCenter(rotationCenter);
        getEditorRotateRequest().setMultiple(false);        
	    }                  
    }
    getEditorRotateRequest().setLocation(getLocation());
        
    LOGGER.debug("updateSourceRequest.rotation = "+getEditorRotateRequest().getRotation());
    LOGGER.debug("rotationCenter = "+rotationCenter);
    LOGGER.debug("location = "+getLocation());
  }
  
  protected EditorRotateRequest getEditorRotateRequest() 
  {
    return (EditorRotateRequest) getSourceRequest();
  }
  
  protected String getDebugName() 
  {
    return "Rotate Handle Tracker";//$NON-NLS-1$
  }

  // Override to avoid the single selection of the EditPart whiches handle has been selected
	protected void performSelection() 
	{
		
	}       
}
