/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 17.02.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.tools;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.requests.CreationFactory;

import com.nightlabs.editor2d.command.CreateShapeCommand;
import com.nightlabs.editor2d.j2d.GeneralShape;
import com.nightlabs.editor2d.request.EditorCreateRequest;
import com.nightlabs.editor2d.util.EditorGeneralShapeFactory;


public class RectangleTool 
extends EditorCreationTool 
{
  public static final Logger LOGGER = Logger.getLogger(RectangleTool.class);
  
  public RectangleTool(CreationFactory aFactory) 
  {
    super(aFactory);
  }  
  
  /**
   * @see org.eclipse.gef.Tool#deactivate()
   */
  public void deactivate() 
  {
  	super.deactivate();
  	helper = null;
  } 
  
  protected Rectangle bounds;
  protected boolean handleButtonDown(int button) 
  {
    if (button == 1) 
    {
	  	Point p = getLocation();
	  	getEditorCreateRequest().setLocation(p);    		    	  
  		lockTargetEditPart(getTargetEditPart());
  		// Snap only when size on drop is employed
  		helper = (SnapToHelper)getTargetEditPart().getAdapter(SnapToHelper.class);
  		    		    		    		
  		bounds = new Rectangle(p.x, p.y, p.x+1, p.y+1);
  		RectangleFigure rectFigure = new RectangleFigure();
  		rectFigure.setBounds(bounds);
  		getEditorCreateRequest().setUseShape(true);
  		getEditorCreateRequest().setShape(rectFigure);
  		
  		stateTransition(STATE_INITIAL, STATE_DRAG_IN_PROGRESS);
  		setCursor(getDefaultCursor());    		    	
    }    	  
  	return true;
  }   
  
  protected void updateTargetRequest() 
  {
  	EditorCreateRequest req = (EditorCreateRequest) getCreateRequest();
  	if (isInState(STATE_DRAG_IN_PROGRESS)) 
  	{
  		Point loq = getStartLocation();
  		Rectangle bounds = new Rectangle(loq, loq);
  		bounds.union(loq.getTranslated(getDragMoveDelta()));
  		req.setSize(bounds.getSize());
  		req.setLocation(bounds.getLocation());
  		req.getExtendedData().clear();
  		if (!getCurrentInput().isAltKeyDown() && helper != null) {
  			PrecisionRectangle baseRect = new PrecisionRectangle(bounds);
  			PrecisionRectangle result = baseRect.getPreciseCopy();
  			helper.snapRectangle(req, PositionConstants.NSEW, 
  				baseRect, result);
  			req.setLocation(result.getLocation());
  			req.setSize(result.getSize());
  		}
  	} else {
  		req.setSize(null);
  		req.setLocation(getLocation());
  	}    
  }
  
  public void performCreation(int button) 
  {
    GeneralShape gs = EditorGeneralShapeFactory.createRectangle(bounds);
		getEditorCreateRequest().setUseShape(false);
		if (getCurrentCommand() instanceof CreateShapeCommand) {
		  CreateShapeCommand command = (CreateShapeCommand) getCurrentCommand();		  
		  command.setGeneralShape(gs);		  
		}
		
		super.performCreation(button);
  }   
}
