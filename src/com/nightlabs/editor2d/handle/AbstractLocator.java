/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 13.04.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.handle;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Locator;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;


public abstract class AbstractLocator 
implements Locator 
{
  protected GraphicalEditPart owner;
  protected IFigure figure;
  public AbstractLocator(GraphicalEditPart owner) 
  {
    super();
    this.owner = owner;
    this.figure = owner.getFigure();
  }

  public void relocate(IFigure target) 
  {
//    target.setLocation(calcCenterPoint(target.getBounds(), getLocation()));
    target.setLocation(calcCenterPoint(target.getBounds(), getConstrainedPoint(target)));
  }
  
  protected abstract Point getLocation();  
  
  protected Point calcCenterPoint(Rectangle rect, Point point) 
  {
    Dimension boundsSize = rect.getSize();
    return new Point(point.x - boundsSize.width/2, point.y - boundsSize.height/2);
  }  
  
  protected Point getConstrainedPoint(IFigure target) 
  {
    Point p = getLocation();
    figure.translateToAbsolute(p);
    target.translateToRelative(p);   		
    return p;
  }      
}
