/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
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
package org.nightlabs.editor2d.editpolicy;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;
import org.nightlabs.editor2d.figures.AbstractShapeFigure;
import org.nightlabs.editor2d.figures.FeedbackShapeFigure;
import org.nightlabs.editor2d.figures.ShapeFigure;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.request.EditorBoundsRequest;
import org.nightlabs.editor2d.request.EditorCreateRequest;
import org.nightlabs.editor2d.util.EditorUtil;
import org.nightlabs.editor2d.util.feedback.FeedbackUtil;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public abstract class FeedbackContainerXYLayoutEditPolicy 
extends GuideContainerXYLayoutEditPolicy 
{
	public static final Logger LOGGER = Logger.getLogger(FeedbackContainerXYLayoutEditPolicy.class);
	
	public FeedbackContainerXYLayoutEditPolicy() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createAddCommand(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	@Override
	protected Command createAddCommand(EditPart child, Object constraint) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createChangeConstraintCommand(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	@Override
	protected Command getCreateCommand(CreateRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getDeleteDependantCommand(org.eclipse.gef.Request)
	 */
	@Override
	protected Command getDeleteDependantCommand(Request request) {
		// TODO Auto-generated method stub
		return null;
	}

	protected IFigure createSizeOnDropFeedback(EditorCreateRequest editorRequest) 
	{
    Rectangle constrainedBounds = (Rectangle)getConstraintFor((EditorBoundsRequest)editorRequest);	    
    
    if (editorRequest.isUseShape()) 
    {
      Shape shape = editorRequest.getShape();
      if (shape != null) 
      {
        shape.setBounds(constrainedBounds);            
        addFeedback(shape);
        return shape;
      }	            
    }
    else 
    {
      GeneralShape gp = editorRequest.getGeneralShape();
	    if (gp != null) 
	    {
	      if (editorRequest.getMode() == EditorCreateRequest.BOUNDS_FIX_MODE) 
	      {
			    ShapeFigure shapeFigure = new AbstractShapeFigure();			    			    
			    shapeFigure.setGeneralShape(gp);			    
			    shapeFigure.setFill(false);
			    addFeedback(shapeFigure);		   		    		    
			    return shapeFigure;	        
	      }
	      else 
	      {
			    FeedbackShapeFigure shapeFigure = new FeedbackShapeFigure();			    			    
			    shapeFigure.setGeneralShape(gp);			    
			    shapeFigure.setFill(false);
			    addFeedback(shapeFigure);		   		    		    
			    return shapeFigure;	      		      	        
	      }
	    }
    }
    return null;
	}
		
	/**
	 * Lazily creates and returns the Figure to use for size-on-drop feedback.
	 * @return the size-on-drop feedback figure
	 */
	protected IFigure createDefaultSizeOnDropFeedback() 
	{
		IFigure	sizeOnDropFeedbackDefault = new RectangleFigure();
		sizeOnDropFeedbackDefault.setBackgroundColor(FeedbackUtil.DEFAULT_PAINT_DESCRIPTOR.getBackgroundColor());
		sizeOnDropFeedbackDefault.setForegroundColor(FeedbackUtil.DEFAULT_PAINT_DESCRIPTOR.getForegroundColor());
		if (sizeOnDropFeedbackDefault instanceof Shape) 
		{
			Shape s = (Shape) sizeOnDropFeedbackDefault;
			s.setLineStyle(Graphics.LINE_SOLID);
			s.setFillXOR(true);
			s.setOutlineXOR(true);  			
		}
		addFeedback(sizeOnDropFeedbackDefault);
		return sizeOnDropFeedbackDefault;
	} 
	
	protected boolean showFeedbackText = true;	
  protected Label feedbackText;	
	
	protected void eraseSizeOnDropFeedback(Request request) 
	{
		LOGGER.debug("eraseSizeOnDropFeedback!");
		
		super.eraseSizeOnDropFeedback(request);								
		if (showFeedbackText) {
			eraseFeedbackText();
		}		
	}	
		
  protected Label getFeedbackTextFigure()
  {
  	if (feedbackText == null)
  		feedbackText = createFeedbackTextFigure("");
  	return feedbackText;
  }
  
  protected Rectangle getInitialFeedbackBounds() 
  {
  	return Rectangle.SINGLETON;
  }
  
  protected Label createFeedbackTextFigure(String text) 
  {       	
    Label l = new Label(text);
  	l.setBounds(getInitialFeedbackBounds());
  	addFeedback(l);
  	return l;
  }  
  
  protected void showFeedbackText(CreateRequest request) 
  {
  	Label feedbackText = getFeedbackTextFigure();  	
  	feedbackText.setText(getText(request));
  	feedbackText.setLocation(getFeedbackTextLocation(request));
  	feedbackText.setSize(100, 20);
  	  	
  	getFeedbackLayer().repaint();  	
  }
  
  protected Point getFeedbackTextLocation(CreateRequest request) 
  {
  	// TODO: set location always on mouse location 
  	Point loc = request.getLocation();
  	Dimension size = request.getSize();
//  	Rectangle feedbackBounds = getSizeOnDropFeedback().getBounds();
  	Point location = new Point(loc.x + size.width, loc.y + size.height);
  	location.translate(EditorUtil.getScrollOffset(getHost()));
  	return location;  	
  }
    
  protected String getText(CreateRequest request) 
  {
  	Point relativeSize = new Point(request.getSize().width, request.getSize().height);
  	Point absoluteSize = EditorUtil.toAbsolute(getHost(), relativeSize.x, relativeSize.y);
  	  	
  	String width = "W";
  	String height = "H";
  	StringBuffer sb = new StringBuffer();
  	sb.append(width+" ");
  	sb.append(absoluteSize.x);
  	sb.append(", ");
  	sb.append(height+" ");
  	sb.append(absoluteSize.y);
  	return sb.toString();
  }
  
  protected void eraseFeedbackText() 
  {
    if (feedbackText != null)
      removeFeedback(feedbackText);
    
    feedbackText = null;
  } 	
}
