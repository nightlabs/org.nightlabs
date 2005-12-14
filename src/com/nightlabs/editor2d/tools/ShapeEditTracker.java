/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 13.01.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.tools;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;

import com.nightlabs.editor2d.edit.ShapeDrawComponentEditPart;
import com.nightlabs.editor2d.j2d.GeneralShape;
import com.nightlabs.editor2d.request.EditorEditShapeRequest;

// TODO; Use SelectEditPartTracker instead to avoid multiple Selections
public class ShapeEditTracker 
extends AbstractDragTracker 
{
  public static final Logger LOGGER = Logger.getLogger(ShapeEditTracker.class);
  
  protected ShapeDrawComponentEditPart getShapeDrawComponentEditPart() {
    return (ShapeDrawComponentEditPart) owner;
  }
  protected GeneralShape getGeneralShape() {
    return getShapeDrawComponentEditPart().getGeneralShape();
  }
//  protected ShapeFigure sourceFigure;  
  protected int pathSegmentIndex;
  
  public ShapeEditTracker(ShapeDrawComponentEditPart owner, int pathSegmentIndex) 
  {    
    super(owner);
    this.pathSegmentIndex = pathSegmentIndex;
  }
  
  protected String getCommandName() {
    return REQ_EDIT_SHAPE;
  }
  
  /**
   * @see org.eclipse.gef.tools.AbstractTool#deactivate()
   */
  public void deactivate() 
  {  	
  	super.deactivate();
//  	sourceFigure = null;  	
  }  
        
  /**
   * @see org.eclipse.gef.tools.AbstractTool#getDebugName()
   */
  protected String getDebugName() 
  {
  	return "Edit Shape Handle Tracker";//$NON-NLS-1$
  }  
        
  /**
   * @see org.eclipse.gef.tools.SimpleDragTracker#createSourceRequest()
   */
  protected Request createSourceRequest() 
  {    
  	EditorEditShapeRequest request = new EditorEditShapeRequest();
  	request.setType(REQ_EDIT_SHAPE);
  	request.setPathSegmentIndex(pathSegmentIndex);
  	request.setLocation(getLocation());
  	List selectedEditParts = getCurrentViewer().getSelectedEditParts();
  	if (selectedEditParts != null && !selectedEditParts.isEmpty()) {
  	  EditPart selectedEditPart = (EditPart)selectedEditParts.get(0);
  		request.setTargetEditPart(selectedEditPart);  	  
  	}  	  	  	  	
  	return request;        
  }  
        
  /**
   * @see org.eclipse.gef.tools.SimpleDragTracker#updateSourceRequest()
   */
  protected void updateSourceRequest() 
  {
    EditorEditShapeRequest request = (EditorEditShapeRequest) getSourceRequest();
	  request.setLocation(getLocation());    	  		
  }  
    
}
