/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 13.04.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.handle;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Locator;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import org.nightlabs.editor2d.edit.AbstractDrawComponentEditPart;


public class RotateCenterLocator 
implements Locator 
{
  protected AbstractDrawComponentEditPart owner;
  public RotateCenterLocator(AbstractDrawComponentEditPart owner) 
  {
    this.owner = owner;
  }

  protected Point getLocation() 
  {
    return new Point(owner.getDrawComponent().getRotationX(),
        owner.getDrawComponent().getRotationY()); 
  }
  
  public void relocate(IFigure target) 
  {    
    target.setLocation(calcCenterPoint(target.getBounds(), getReferencePoint(target)));    
  } 
  
  protected Point calcCenterPoint(Rectangle rect, Point point) 
  {
    Dimension boundsSize = rect.getSize();
    return new Point(point.x - boundsSize.width/2, point.y - boundsSize.height/2);
  }  
  
  protected Point getReferencePoint(IFigure target) 
  {
    Point p = getLocation();
    owner.getFigure().translateToAbsolute(p);
    target.translateToRelative(p);   		
    return p;
  }  
}
