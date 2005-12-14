/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 13.01.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.command;

import java.awt.geom.PathIterator;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import com.nightlabs.editor2d.EditorPlugin;
import com.nightlabs.editor2d.ShapeDrawComponent;
import com.nightlabs.editor2d.j2d.GeneralShape;


public class EditShapeCommand 
extends Command 
{
  protected GeneralShape oldGeneralShape;    
  protected GeneralShape generalShape;  
  
  protected int pathSegmentIndex;      
	public void setPathSegmentIndex(int pathSegmentIndex) {
	  this.pathSegmentIndex = pathSegmentIndex;
	}
		
	protected Point location;	
  public void setLocation(Point location) {
    this.location = location;
  }
  
	/** The ShapeDrawComponent to edit */
	protected ShapeDrawComponent shape;
	public void setShapeDrawComponent(ShapeDrawComponent sdc) {
	  shape = sdc;
	}  
	  
  /**
   * 
   */
  public EditShapeCommand() 
  {
    super();
    setLabel(EditorPlugin.getResourceString("command_edit_shape"));
  }

  /**
   * @param label
   */
  public EditShapeCommand(String label) 
  {
    super(label);
  }
    
  public void execute() 
  {        
    oldGeneralShape = (GeneralShape) shape.getGeneralShape().clone();
//    generalShape = (GeneralShape) shape.getGeneralShape();
//    float[] coords = new float[] {location.x, location.y};
//    if (pathSegmentIndex == 0) {
//      generalShape.setPathSegment(PathIterator.SEG_MOVETO, pathSegmentIndex, coords);
//    } else {
//      generalShape.setPathSegment(PathIterator.SEG_LINETO, pathSegmentIndex, coords);      
//    }
    
    generalShape = new GeneralShape();
    float[] coords = new float[6];
    int index = 0;
    boolean indexSet = false;
    for (PathIterator pi = oldGeneralShape.getPathIterator(null); !pi.isDone(); pi.next()) 
    {
      if (index == pathSegmentIndex) 
      {
        if (pathSegmentIndex == PathIterator.SEG_MOVETO)
          generalShape.moveTo(location.x, location.y);
        else
          generalShape.lineTo(location.x, location.y);
        
        index = -1;
        indexSet = true;
        continue;
      }
      int segType = pi.currentSegment(coords);      
      switch (segType) 
      {
	      case (PathIterator.SEG_MOVETO):
	        generalShape.moveTo(coords[0], coords[1]);
	      	if (!indexSet)
	      	  index++;
	        break;
	      case (PathIterator.SEG_LINETO):
	        generalShape.lineTo(coords[0], coords[1]);
	      	if (!indexSet)
	      	  index++;
	        break;
	      case (PathIterator.SEG_QUADTO):
	        generalShape.quadTo(coords[0], coords[1], coords[2], coords[3]);
	      	if (!indexSet)
	      	  index++;
	        break;
	      case (PathIterator.SEG_CUBICTO):
	        generalShape.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
	      	if (!indexSet)
	      	  index++;	      
	        break;
	      case (PathIterator.SEG_CLOSE):
	        generalShape.closePath();
	      	if (!indexSet)
	      	  index++;      
	        break;
      }
    }
    shape.setGeneralShape(generalShape);
  }
  
  public void undo() 
  {
    shape.setGeneralShape(oldGeneralShape); 
  }
  
  public void redo() 
  {    
    shape.setGeneralShape(generalShape);
  }
}
