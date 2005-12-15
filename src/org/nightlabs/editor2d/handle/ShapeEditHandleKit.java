/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 13.01.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.handle;

import java.util.List;

import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.PointList;

import org.nightlabs.editor2d.edit.ShapeDrawComponentEditPart;
import org.nightlabs.editor2d.util.J2DUtil;


public class ShapeEditHandleKit 
{

  public ShapeEditHandleKit() {
    super();
  }
    
  /**
   * Fills the given List with handles at PathSegment of the GeneralShape of the
   * ShapeDrawComponentEditPart.
   * 
   * @param part the handles' ShapeDrawComponentEditPart
   * @param handles the List to add the handles to
   */
  public static void addHandles(ShapeDrawComponentEditPart part, List handles) 
  {    
//    Polyline polyline = J2DUtil.toPolyline(part.getGeneralShape()); 
//    PointList points = polyline.getPoints();
//    for (int i=0; i<points.size(); i++) 
//    {
//      ShapeEditHandle handle = new ShapeEditHandle(part, i);
//      handles.add(handle);
//    }
    
    Polyline polyline = J2DUtil.toPolyline(part.getGeneralShape()); 
    PointList points = polyline.getPoints();
    for (int i=0; i<points.size(); i++) 
    {
      ShapeEditHandle handle = new ShapeEditHandle(part, i);
      handles.add(handle);
    }      
    
  }
  
}
