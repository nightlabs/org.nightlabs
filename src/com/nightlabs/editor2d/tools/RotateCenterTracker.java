/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 13.04.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.tools;

import org.apache.log4j.Logger;
import org.eclipse.gef.Request;
import org.eclipse.swt.graphics.Cursor;

import com.nightlabs.editor2d.custom.EditorCursors;
import com.nightlabs.editor2d.edit.AbstractDrawComponentEditPart;
import com.nightlabs.editor2d.request.EditorRotateCenterRequest;


public class RotateCenterTracker 
extends AbstractDragTracker  
{
  public static final Logger LOGGER = Logger.getLogger(RotateCenterTracker.class);
  
  public RotateCenterTracker(AbstractDrawComponentEditPart owner) {
    super(owner);
  }

  protected AbstractDrawComponentEditPart getAbstractDrawComponentEditPart() {
    return (AbstractDrawComponentEditPart) owner;
  }

  protected String getCommandName() {
    return REQ_EDIT_ROTATE_CENTER;
  }

  protected Cursor getDefaultCursor() {
    return EditorCursors.CROSS;
  } 
  
  protected Request createSourceRequest() 
  {
    EditorRotateCenterRequest rotateRequest = new EditorRotateCenterRequest();
    rotateRequest.setType(REQ_EDIT_ROTATE_CENTER);
    rotateRequest.setRotationCenter(getLocation());   
    rotateRequest.setEditParts(getCurrentViewer().getSelectedEditParts());         
    return rotateRequest;
  }  
  
  protected void updateSourceRequest() 
  {
    getEditorRotateCenterRequest().setRotationCenter(getLocation());
    LOGGER.debug("rotationCenter = "+getLocation());
  }
  
  protected EditorRotateCenterRequest getEditorRotateCenterRequest() 
  {
    return (EditorRotateCenterRequest) getSourceRequest();
  }

  // Override to avoid the single selection of the EditPart whiches handle has been selected
	protected void performSelection() 
	{
		
	}   
    
}
