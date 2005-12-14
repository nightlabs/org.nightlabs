/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 08.12.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.edit;

import com.nightlabs.editor2d.ShapeDrawComponent;


public class RectangleEditPart 
extends ShapeDrawComponentEditPart 
{
  /**
   * @param drawComponent
   */
  public RectangleEditPart(ShapeDrawComponent drawComponent) {
    super(drawComponent);
  }

//  /* (non-Javadoc)
//   * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
//   */
//  protected IFigure createFigure() 
//  {
//	  RectangleFigure rectangle = new RectangleFigure();		  
//	  setShapeProperties((RectangleDrawComponent)getModel(), rectangle);
//		return rectangle;
//  }
}
