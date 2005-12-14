/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 22.12.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.figures;

import java.awt.geom.AffineTransform;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Translatable;

import com.nightlabs.editor2d.handle.HandleShape;
import com.nightlabs.editor2d.j2d.GeneralShape;


public interface ShapeFigure 
extends IFigure,
				Translatable,
				HandleShape
{
  public GeneralShape getGeneralShape();
  public void setGeneralShape(GeneralShape generalShape);  
//  public double getRotation();
//  public java.awt.Rectangle setRotation(double rot, boolean init);
  public void transform(AffineTransform at); 
  public void setXOR(boolean b);  
  public void setFill(boolean b);
  public int getLineWidth();
  public void setLineWidth(int lineWidth);
  public int getLineStyle();
  public void setLineStyle(int lineStyle);  
}
