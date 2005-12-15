/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 17.02.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.figures;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.geometry.Rectangle;

import org.nightlabs.editor2d.util.J2DUtil;


public class FeedbackShapeFigure 
extends AbstractShapeFigure 
{
  public static final Logger LOGGER = Logger.getLogger(FeedbackShapeFigure.class);
  
  public void setBounds(Rectangle newBounds) 
  {
  	Rectangle oldBounds = getBounds();
  	J2DUtil.transformGeneralShape(getGeneralShape(), oldBounds, newBounds);
  	
  	super.repaint();
  }
  
//  public void setBounds(Rectangle newBounds) 
//  {        
//    Rectangle oldBounds = getBounds();
//    AffineTransform at = J2DUtil.getAffineTransform(oldBounds, newBounds);
//    getGeneralShape().transform(at);
//    for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
//      IFigure child = (IFigure) it.next();
//      if (child instanceof ShapeFigure) {
//        ShapeFigure shapeFigure = (ShapeFigure) child;
//        shapeFigure.transform(at);
//      }
//    }    
//    super.setBounds(J2DUtil.toDraw2D(getGPBounds()));       
//  }  
   
//  public Rectangle getBounds()
//  {
//    Rectangle totalBounds = new Rectangle();
//    Rectangle gpBounds = J2DUtil.toDraw2D(getGeneralShape().getBounds());
//    for (Iterator it = getChildren().iterator(); it.hasNext(); ) 
//    {
//      IFigure child = (IFigure) it.next();
//      if (child instanceof ShapeFigure) {
//        ShapeFigure childFigure = (ShapeFigure) child;
//        Rectangle childBounds = J2DUtil.toDraw2D(childFigure.getGeneralShape().getBounds());
//        gpBounds = gpBounds.union(childBounds);
//      }
//    }
//    return gpBounds;
//  }
  
//  public void transform(AffineTransform at) 
//  {
//    getGeneralShape().transform(at);
//    outlineArea = null;
//    bounds = J2DUtil.toDraw2D(getGeneralShape().getBounds());
//    for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
//      IFigure child = (IFigure) it.next();
//      if (child instanceof ShapeFigure) {
//        ShapeFigure shapeFigure = (ShapeFigure) child;
//        shapeFigure.transform(at);
//      }
//    }
//    repaint();    
//  }   
}
