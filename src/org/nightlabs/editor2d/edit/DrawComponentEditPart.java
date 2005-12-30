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

import org.eclipse.gef.EditPolicy;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.editpolicy.DrawComponentEditPolicy;

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
