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

package org.nightlabs.editor2d.edit;

import java.beans.PropertyChangeEvent;

import org.eclipse.gef.DragTracker;
import org.eclipse.gef.Request;
import org.eclipse.ui.views.properties.IPropertySource;

import org.nightlabs.editor2d.EditorStateManager;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.figures.ShapeFigure;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.model.ShapeDrawComponentPropertySource;
import org.nightlabs.editor2d.request.EditorEditShapeRequest;
import org.nightlabs.editor2d.tools.ShapeEditTracker;


//public abstract class ShapeDrawComponentEditPart 
public class ShapeDrawComponentEditPart
extends DrawComponentEditPart 
{
  /**
   * @param drawComponent
   */
  public ShapeDrawComponentEditPart(ShapeDrawComponent shapeDrawComponent) {
    super(shapeDrawComponent);
  }

//  /* (non-Javadoc)
//   * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
//   */
//  protected IFigure createFigure() 
//  {
//    AbstractShapeFigure shapeFigure = new ShapeFigureImpl();
//    shapeFigure.setGeneralShape(getShapeDrawComponent().getGeneralShape());
//    setShapeProperties(getShapeDrawComponent(), shapeFigure);
//    
//    if (getRoot() instanceof ScalableRootEditPart) {
//      ((ScalableRootEditPart) getRoot()).getZoomManager().addZoomListener(shapeFigure.getZoomListener());
//    }
//    
//    return shapeFigure;
//  }

//	protected Color defaultFillColor = ColorConstants.white;
//	protected Color defaultLineColor = ColorConstants.black;
	 
//	protected void setShapeProperties(ShapeDrawComponent sdc, ShapeFigure s)	
//	{	  
//	  // TODO: Color should come from ConfigModule
//	  Color fillColor = J2DUtil.toSWTColor(sdc.getFillColor());	  
//	  Color lineColor = J2DUtil.toSWTColor(sdc.getLineColor());
//	  	  	 	  
//	  s.setBackgroundColor(fillColor);
//	  s.setForegroundColor(lineColor);
//	  s.setLineWidth(sdc.getLineWidth());
//	  s.setLineStyle(sdc.getLineStyle());
//	  s.setFill(sdc.isFill());
//	}
  	
//	protected void refreshVisuals() 
//	{ 
//	  GeneralShape gs = (GeneralShape) getShapeDrawComponent().getGeneralShape().clone();
//	  getShapeFigure().setGeneralShape(gs);
////	  setShapeProperties(getShapeDrawComponent(), getShapeFigure());
//	  super.refreshVisuals();
//	}
		
	protected void propertyChanged(PropertyChangeEvent evt) 
	{
		super.propertyChanged(evt);
		String propertyName = evt.getPropertyName();
		if (propertyName.equals(ShapeDrawComponent.PROP_FILL_COLOR)) {
			LOGGER.debug(propertyName +" changed!");
			refreshVisuals();			
		}
		else if (propertyName.equals(ShapeDrawComponent.PROP_LINE_COLOR)) {
			LOGGER.debug(propertyName +" changed!");
			refreshVisuals();			
		}
		else if (propertyName.equals(ShapeDrawComponent.PROP_FILL)) {
			LOGGER.debug(propertyName +" changed!");
			refreshVisuals();			
		}
		else if (propertyName.equals(ShapeDrawComponent.PROP_LINE_STYLE)) {
			LOGGER.debug(propertyName +" changed!");
			refreshVisuals();			
		}
		else if (propertyName.equals(ShapeDrawComponent.PROP_LINE_WIDTH)) {
			LOGGER.debug(propertyName +" changed!");
			refreshVisuals();			
		}
		else if (propertyName.equals(ShapeDrawComponent.PROP_GENERAL_SHAPE)) {
			LOGGER.debug(propertyName +" changed!");
			refreshVisuals();			
		}		
	}
		
  public ShapeDrawComponent getShapeDrawComponent() {
    return (ShapeDrawComponent) getModel();
  }
 
//  public ShapeFigure getShapeFigure() {
//    return (ShapeFigure) getFigure();
//  }
  
  public GeneralShape getGeneralShape() 
  {
    GeneralShape gs = getShapeDrawComponent().getGeneralShape();
    if (gs == null) {
      gs = ((ShapeFigure)getFigure()).getGeneralShape();
      getShapeDrawComponent().setGeneralShape(gs);
    }
    return gs;     
  }
    
  /**
   * Overridden to return a default <code>DragTracker</code> for GraphicalEditParts.
   * @see org.eclipse.gef.EditPart#getDragTracker(Request)
   */
  public DragTracker getDragTracker(Request request) 
  { 
    if (request.getType().equals(REQ_EDIT_SHAPE)) {
      EditorEditShapeRequest req = (EditorEditShapeRequest) request; 
      return new ShapeEditTracker(this, req.getPathSegmentIndex());
    }
          
  	return super.getDragTracker(request);
  }
    
  public void performRequest(Request req) 
  {
    // TODO: set somehow the EditShapeMode to the corresponding ShapeFigure 
    if (req.getType().equals(REQ_EDIT_SHAPE)) 
    {
      EditorEditShapeRequest request = (EditorEditShapeRequest) req;
      EditorStateManager.setEditShapeMode(this);      
    }
    
    super.performRequest(req);
  }
          
  public boolean understandsRequest(Request req) 
  {
    if (req.getType().equals(REQ_EDIT_SHAPE))
      return true;
    
    return super.understandsRequest(req);
  }
  
  /* (non-Javadoc)
   * @see com.ibm.itso.sal330r.gefdemo.edit.WorkflowElementEditPart#getPropertySource()
   */
  protected IPropertySource getPropertySource()
  {
    if (propertySource == null)
    {
      propertySource =
        new ShapeDrawComponentPropertySource(getShapeDrawComponent());
    }
    return propertySource;
  }  
}
