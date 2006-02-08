/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.editor2d.util;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.figures.FeedbackShapeFigure;
import org.nightlabs.editor2d.figures.ShapeFigure;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.j2d.GeneralShapeFactory;


public class FeedbackUtil 
{
//  protected static DefaultFeedbackConfigModule defaultFeedbackConfig;
  
  public FeedbackUtil() 
  {
    super(); 
//    try {
//      defaultFeedbackConfig = (DefaultFeedbackConfigModule) Config.sharedInstance().createConfigModule(DefaultFeedbackConfigModule.class);
//    } catch (ConfigException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
  }
  
  public static Color GHOST_FILL_COLOR = new Color(null, 31, 31, 31);
  public static final Color DEFAULT_BGCOLOR = ColorConstants.darkGray;
  public static final Color DEFAULT_FGCOLOR = ColorConstants.white;
  
  protected static Color bgColor = DEFAULT_BGCOLOR;
  public static Color getBackgroundColor() {
  	return bgColor;
  }
  
  protected static Color fgColor = DEFAULT_FGCOLOR;
  public static Color getForegroundColor() {
  	return fgColor;
  }
  
  public static ShapeFigure getCustomFeedbackFigure(Object modelPart) 
	//protected IFigure getCustomFeedbackFigure(Object modelPart)
	{
	  GeneralShape gs = null;
	  if (modelPart instanceof ShapeDrawComponent) {
	    ShapeDrawComponent sdc = (ShapeDrawComponent) modelPart;
	    gs = (GeneralShape)sdc.getGeneralShape().clone();
	  }
	  else if (modelPart instanceof ImageDrawComponent) {
	    ImageDrawComponent idc = (ImageDrawComponent) modelPart;
	    gs = (GeneralShape) idc.getImageShape().clone();
	  }
	  else if (modelPart instanceof DrawComponentContainer) 
	  {
	    DrawComponentContainer container = (DrawComponentContainer) modelPart;
	    ShapeFigure containerFigure = new FeedbackShapeFigure();
	    GeneralShape containerShape = GeneralShapeFactory.createRectangle(1,1,1,1);
	    containerFigure.setGeneralShape(containerShape);
	    containerFigure.setXOR(true);
	    containerFigure.setFill(true);
	    containerFigure.setBackgroundColor(bgColor);
	    containerFigure.setForegroundColor(fgColor);      
	//    for (Iterator it = container.getDrawComponents().iterator(); it.hasNext(); ) {
	//      DrawComponent dc = (DrawComponent) it.next(); 
	//      ShapeFigure figure = getCustomFeedbackFigure(dc);
	//      containerFigure.add(figure);
	//    }
	    return containerFigure;      
	  }    
	  else {
	    gs = GeneralShapeFactory.createRectangle(0, 0, 10, 10);      
	  }
	  ShapeFigure shapeFigure = new FeedbackShapeFigure();
	  shapeFigure.setGeneralShape(gs);
	  shapeFigure.setXOR(true);
	  shapeFigure.setFill(true);
	  shapeFigure.setBackgroundColor(bgColor);       
	  shapeFigure.setForegroundColor(fgColor);
	  return shapeFigure;    
	}    
  
//  public static ShapeFigure createCustomFeedbackFigure(Object modelPart) 
//	{
//	  GeneralShape gs = null;
//	  if (modelPart instanceof ShapeDrawComponent) {
//	    ShapeDrawComponent sdc = (ShapeDrawComponent) modelPart;
//	    gs = (GeneralShape)sdc.getGeneralShape().clone();
//	  }
//	  else if (modelPart instanceof ImageDrawComponent) {
//	    ImageDrawComponent idc = (ImageDrawComponent) modelPart;
//	    gs = (GeneralShape) idc.getImageShape().clone();
//	  }
//	  else if (modelPart instanceof DrawComponentContainer) 
//	  {
//	    DrawComponentContainer container = (DrawComponentContainer) modelPart;
//	    ShapeFigure containerFigure = new FeedbackShapeFigure();
//	    GeneralShape containerShape = GeneralShapeFactory.createRectangle(1,1,1,1);
//	    containerFigure.setGeneralShape(containerShape);
//	    containerFigure.setXOR(true);
//	    containerFigure.setFill(true);
//	    containerFigure.setBackgroundColor(ColorConstants.darkGray);
//	    containerFigure.setForegroundColor(ColorConstants.white);      
//	    for (Iterator it = container.getDrawComponents().iterator(); it.hasNext(); ) {
//	      DrawComponent dc = (DrawComponent) it.next(); 
//	      ShapeFigure figure = createCustomFeedbackFigure(dc);
//	      containerFigure.add(figure);
//	    }
//	    return containerFigure;      
//	  }    
//	  else {
//	    gs = GeneralShapeFactory.createRectangle(0, 0, 10, 10);      
//	  }
//	  ShapeFigure shapeFigure = new FeedbackShapeFigure();
//	  shapeFigure.setGeneralShape(gs);
//	  shapeFigure.setXOR(true);
//	  shapeFigure.setFill(true);
//	  shapeFigure.setBackgroundColor(ColorConstants.darkGray);
//	  shapeFigure.setForegroundColor(ColorConstants.white);
//	  return shapeFigure;    
//	}  
  
//public static ShapeFigure getCustomFeedbackFigure(Object modelPart) 
//{
//  GeneralShape gs = null;
//  if (modelPart instanceof ShapeDrawComponent) {
//    ShapeDrawComponent sdc = (ShapeDrawComponent) modelPart;
//    gs = (GeneralShape)sdc.getGeneralShape().clone();
//  }
//  else if (modelPart instanceof ImageDrawComponent) {
//    ImageDrawComponent idc = (ImageDrawComponent) modelPart;
//    gs = (GeneralShape) idc.getImageShape().clone();
//  }
//  else if (modelPart instanceof DrawComponentContainer) 
//  {
//    DrawComponentContainer container = (DrawComponentContainer) modelPart;
//    ShapeFigure containerFigure = new FeedbackShapeFigure();
//    GeneralShape containerShape = GeneralShapeFactory.createRectangle(1,1,1,1);
//    containerFigure.setGeneralShape(containerShape);
//    setDefaultFeedback(containerFigure);
//    for (Iterator it = container.getDrawComponents().iterator(); it.hasNext(); ) {
//      DrawComponent dc = (DrawComponent) it.next(); 
//      ShapeFigure figure = getCustomFeedbackFigure(dc);
//      containerFigure.add(figure);
//    }
//    return containerFigure;      
//  }    
//  else {
//    gs = GeneralShapeFactory.createRectangle(0, 0, 10, 10);      
//  }
//  ShapeFigure shapeFigure = new FeedbackShapeFigure();
//  shapeFigure.setGeneralShape(gs);
//  setDefaultFeedback(shapeFigure);
//  return shapeFigure;    
//}   
  
//  public static void setDefaultFeedback(ShapeFigure sf) 
//  {
//    sf.setBackgroundColor(J2DUtil.toSWTColor(defaultFeedbackConfig.getBgColor()));
//    sf.setForegroundColor(J2DUtil.toSWTColor(defaultFeedbackConfig.getFgColor()));
//    sf.setXOR(defaultFeedbackConfig.isXor());
//    sf.setFill(defaultFeedbackConfig.isFill());
//  }
  
  
}
