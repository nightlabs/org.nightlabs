/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 14.01.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.handle;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Locator;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.nightlabs.editor2d.ShapeDrawComponent;
import com.nightlabs.editor2d.edit.ShapeDrawComponentEditPart;
import com.nightlabs.editor2d.figures.ShapeFigure;
import com.nightlabs.editor2d.j2d.GeneralShape;
import com.nightlabs.editor2d.j2d.PathSegment;
import com.nightlabs.editor2d.util.J2DUtil;


public class ShapeHandleLocator  
implements Locator
{
  protected IFigure figure;  
  protected IFigure getFigure() {
    return figure;
  }

  protected ShapeDrawComponentEditPart sdcep;
  public GeneralShape getGeneralShape() {
    return sdcep.getGeneralShape();
  }
      
  protected int index;  
  public int getIndex() {
    return index;
  }
  public void setIndex(int index) {
    this.index = index;
  }
  
  public ShapeHandleLocator(ShapeDrawComponentEditPart sdcep, int index) 
  {
    super();
    this.figure = sdcep.getFigure();
    this.sdcep = sdcep;
    this.index = index;
  }
  
  protected Point getReferencePoint(IFigure target) 
  {
    Point p = getLocation();
    getFigure().translateToAbsolute(p);
    target.translateToRelative(p);
   		
    return p;
  }
  
  protected Point getLocation() 
  {
    PathSegment ps = getGeneralShape().getPathSegment(index);
    if (ps != null) {
      return J2DUtil.toDraw2D(ps.getPoint());
    }
    return new Point();    
  }  
  
  protected IFigure getReference() {
    return figure;
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
}
