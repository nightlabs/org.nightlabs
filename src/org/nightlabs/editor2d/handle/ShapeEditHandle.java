/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 13.01.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.handle;

import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.handles.SquareHandle;

import org.nightlabs.editor2d.edit.ShapeDrawComponentEditPart;
import org.nightlabs.editor2d.figures.ShapeFigure;
import org.nightlabs.editor2d.tools.ShapeEditTracker;


public class ShapeEditHandle  
extends SquareHandle
{
  protected ShapeDrawComponentEditPart owner;
  protected int pathSegmentIndex;
  
  public ShapeEditHandle(ShapeDrawComponentEditPart owner, int pathSegmentIndex) 
  {
    super(owner, new ShapeHandleLocator(owner, pathSegmentIndex));
    this.owner = owner;
    this.pathSegmentIndex = pathSegmentIndex;
    initialize();
//    setLocation(p);
  }
    
  /* (non-Javadoc)
   * @see org.eclipse.gef.handles.AbstractHandle#createDragTracker()
   */
  protected DragTracker createDragTracker() {
    return new ShapeEditTracker(owner, pathSegmentIndex);    
  }
  
  public static final int DEFAULT_SIZE = 6;
      
  /**
   * Initializes the handle.  Sets the {@link DragTracker} and
   * DragCursor.
   */
  protected void initialize() {
  	setOpaque(false);
  	setBorder(new LineBorder(1));
  	setCursor(Cursors.CROSS);
  	setPreferredSize(new Dimension(DEFAULT_SIZE, DEFAULT_SIZE));
  	setSize(DEFAULT_SIZE, DEFAULT_SIZE);
  	
//  	setLocation(getPathSegmentLocation());
  }  
     
  public void setBounds(Rectangle rect) 
  {
    super.setBounds(new Rectangle(rect.x, rect.y, DEFAULT_SIZE, DEFAULT_SIZE));
  }
  
  private boolean fixed = false;
  
  /**
   * Returns true if the handle cannot be dragged.
   * @return <code>true</code> if the handle cannot be dragged
   */
  protected boolean isFixed() {
  	return fixed;
  }
  
  /**
   * Sets whether the handle is fixed and cannot be moved
   * @param fixed <code>true</code> if the handle should be unmovable
   */
  public void setFixed(boolean fixed) 
  {
  	this.fixed = fixed;
  	if (fixed)
  		setCursor(Cursors.NO);
  	else
  		setCursor(Cursors.CROSS);
  }     
    
//  protected Point getPathSegmentLocation() 
//  {
//    ShapeFigure sf = (ShapeFigure) owner.getFigure();
//    PathSegment ps = sf.getGeneralShape().getPathSegment(pathSegmentIndex);
//    if (ps != null) {
//      return J2DUtil.toDraw2D(ps.getPoint());
//    }
//    return new Point();    
//  }
 
}
