/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.editor2d.command;

import java.awt.geom.PathIterator;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.j2d.GeneralShape;


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
