/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 13.04.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.handle;

import java.util.List;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;

import com.nightlabs.editor2d.custom.EditorCursors;
import com.nightlabs.editor2d.edit.AbstractDrawComponentEditPart;
import com.nightlabs.editor2d.tools.RotateCenterTracker;


public class RotateCenterHandle 
extends EditorAbstractHandle
{
  protected List editParts;
  public RotateCenterHandle(List editParts) 
  {
    super();
    if (editParts.size() == 1) {
      setLocator(new RotateCenterLocator((AbstractDrawComponentEditPart)editParts.get(0)));
    } else {
      setLocator(new MultipleCenterLocator(editParts));
      multiple = true;
    }
    setOwner((AbstractDrawComponentEditPart)editParts.get(0));
    setCursor(EditorCursors.CROSS);
  }

  protected DragTracker createDragTracker() {
    return new RotateCenterTracker((AbstractDrawComponentEditPart)getOwner());
  }
  
  public void paintFigure(Graphics g) 
  {
    Rectangle r = getBounds();
    r.shrink(1, 1);
    try {    
      g.fillOval(r);      
      g.drawOval(r);
//      g.drawLine(r.x + r.width/2, r.y, r.x + r.width/2, r.y + r.height);
//      g.drawLine(r.x, r.y + r.height/2, r.x + r.width, r.y + r.height/2);
    } finally {
      //We don't really own rect 'r', so fix it.
      r.expand(1, 1);
    }    
  }   
  
  protected boolean multiple = false;  
  public boolean isMultiple() {
    return multiple;
  }
}
