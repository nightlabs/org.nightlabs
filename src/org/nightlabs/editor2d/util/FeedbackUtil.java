/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 27.04.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.util;

import java.util.Iterator;

import org.eclipse.draw2d.ColorConstants;

import org.nightlabs.editor2d.DrawComponent;
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
  
  public static ShapeFigure createCustomFeedbackFigure(Object modelPart) 
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
	    containerFigure.setBackgroundColor(ColorConstants.darkGray);
	    containerFigure.setForegroundColor(ColorConstants.white);      
	    for (Iterator it = container.getDrawComponents().iterator(); it.hasNext(); ) {
	      DrawComponent dc = (DrawComponent) it.next(); 
	      ShapeFigure figure = createCustomFeedbackFigure(dc);
	      containerFigure.add(figure);
	    }
	    return containerFigure;      
	  }    
	  else {
	    gs = GeneralShapeFactory.createRectangle(0, 0, 10, 10);      
	  }
	  ShapeFigure shapeFigure = new FeedbackShapeFigure();
	  shapeFigure.setGeneralShape(gs);
	  shapeFigure.setXOR(true);
	  shapeFigure.setFill(true);
	  shapeFigure.setBackgroundColor(ColorConstants.darkGray);
	  shapeFigure.setForegroundColor(ColorConstants.white);
	  return shapeFigure;    
	}  
  
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
