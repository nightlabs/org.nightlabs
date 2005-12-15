/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 25.11.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.figures;

import org.eclipse.draw2d.geometry.Rectangle;

import org.nightlabs.editor2d.j2d.GeneralShapeFactory;
import org.nightlabs.editor2d.util.EditorGeneralShapeFactory;

public class RectangleFigure 
extends AbstractShapeFigure 
{
  public RectangleFigure() 
  {
    super();
    setGeneralShape(GeneralShapeFactory.createRectangle(0,0,10,10));
  }

  public RectangleFigure(Rectangle rect) 
  {
    super();          
    setGeneralShape(EditorGeneralShapeFactory.createRectangle(rect));
  }
  
}
