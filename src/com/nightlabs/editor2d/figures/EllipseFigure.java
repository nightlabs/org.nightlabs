/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 25.11.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.figures;


public class EllipseFigure 
extends AbstractShapeFigure 
{

  public EllipseFigure() 
  {
    super();
//    createableFromBounds = true;
//    getGeneralShape();
  }
    
//  /* (non-Javadoc)
//   * @see com.nightlabs.editor2d.figures.AbstractShapeFigure#getGeneralPath()
//   */
//  public GeneralShape getGeneralShape() 
//  {
//    if (gp == null) {
////      Arc2D arc = new Arc2D.Double(getGPBounds().x, getGPBounds().y, getGPBounds().width, getGPBounds().height, startAngle, endAngle, Arc2D.OPEN);      
//      Arc2D arc = new Arc2D.Double(bounds.x, bounds.y, bounds.width, bounds.height, startAngle, endAngle, Arc2D.OPEN);      
//      gp = new GeneralShape(arc);
//    }       
//    return gp;
//  }
  
  protected double endAngle = 360;  
  public double getEndAngle() {
    return endAngle;
  }
  public void setEndAngle(double endAngle) {
    this.endAngle = endAngle;
  }
  
  protected double startAngle = 0;
  public double getStartAngle() {
    return startAngle;
  }
  public void setStartAngle(double startAngle) {
    this.startAngle = startAngle;
  }
}
