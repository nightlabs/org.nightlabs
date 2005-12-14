/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 14.04.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.tools;

import java.util.List;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.swt.graphics.Cursor;

import com.nightlabs.editor2d.custom.EditorCursors;
import com.nightlabs.editor2d.edit.AbstractDrawComponentEditPart;
import com.nightlabs.editor2d.request.EditorShearRequest;
import com.nightlabs.editor2d.util.J2DUtil;


public class ShearTracker 
extends AbstractDragTracker 
{
  protected int direction;
  
  /**
   * @param owner
   */
  public ShearTracker(GraphicalEditPart owner, int direction) {
    super(owner);
    this.direction = direction;
  }

  /* (non-Javadoc)
   * @see org.eclipse.gef.tools.SimpleDragTracker#createSourceRequest()
   */
  protected Request createSourceRequest() 
  {
    EditorShearRequest request = new EditorShearRequest();
    request.setType(REQ_SHEAR);
    request.setLocation(getLocation());
    request.setDirection(direction);
    List selectedParts = getCurrentViewer().getSelectedEditParts();
    if (!selectedParts.isEmpty()) {
      AbstractDrawComponentEditPart part = (AbstractDrawComponentEditPart) selectedParts.get(0);
      request.setShearBounds(J2DUtil.toDraw2D(part.getDrawComponent().getBounds()));
    }
    request.setEditParts(selectedParts);     
    return request;
  }

  /* (non-Javadoc)
   * @see org.eclipse.gef.tools.SimpleDragTracker#updateSourceRequest()
   */
  protected void updateSourceRequest() 
  {
//    Point loq = getLocation().getCopy();
//    loq.translate(getStartLocation());
//    getEditorShearRequest().setLocation(loq);
    getEditorShearRequest().setLocation(getLocation());
  }
 
  protected EditorShearRequest getEditorShearRequest() {
    return (EditorShearRequest) getSourceRequest();
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.gef.tools.AbstractTool#getCommandName()
   */
  protected String getCommandName() {
    return REQ_SHEAR;
  }

  /**
   * @see org.eclipse.gef.tools.AbstractTool#getDefaultCursor()
   */
  protected Cursor getDefaultCursor() 
  {
    switch(direction)
    {
    	case(PositionConstants.NORTH):
    	case(PositionConstants.SOUTH):
    	  return EditorCursors.SHEAR_HORIZONTAL;
    	case(PositionConstants.WEST):
    	case(PositionConstants.EAST):
    	  return EditorCursors.SHEAR_VERTICAL;    		
    }
    return EditorCursors.NO;
  }    
}
