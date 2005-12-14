/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 26.10.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.edit;

import org.eclipse.gef.EditPolicy;

import com.nightlabs.editor2d.DrawComponent;
import com.nightlabs.editor2d.editpolicy.DrawComponentEditPolicy;

public abstract class DrawComponentEditPart 
extends AbstractDrawComponentEditPart
{
	/**
	 * @param drawComponent
	 */
	public DrawComponentEditPart(DrawComponent drawComponent)
	{
		super(drawComponent);
	}

//	protected abstract IFigure createFigure();
	
	/* 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies()
	{
		// allow removal of the associated model element
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new DrawComponentEditPolicy());
	}
			
//	protected void refreshVisuals() 
//	{
////	  LOGGER.debug("refreshVisuals()!");
//		// transfer the size and location from the model instance to the corresponding figure
////		Rectangle bounds = new Rectangle(getCastedModel().getX(), getCastedModel().getY(),
////				getCastedModel().getWidth(), getCastedModel().getHeight());
//		Rectangle bounds = new Rectangle(J2DUtil.toDraw2D(getCastedModel().getBounds()));
//	  
//		figure.setBounds(bounds);
//		// notify parent container of changed position & location
//		// if this line is removed, the XYLayoutManager used by the parent container 
//		// (the Figure of the ShapesDiagramEditPart), will not know the bounds of this figure
//		// and will not draw it correctly.
//		((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure, bounds);		
//	}	
//	
//	private DrawComponent getCastedModel() {
//		return (DrawComponent) getModel();
//	}	
}
