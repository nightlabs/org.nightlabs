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

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.SnapToGuides;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.rulers.RulerProvider;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorGuide;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.command.ChangeGuideCommand;
import org.nightlabs.editor2d.command.SetConstraintCommand;
import org.nightlabs.editor2d.request.EditorRequestConstants;
import org.nightlabs.editor2d.util.J2DUtil;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public abstract class GuideContainerXYLayoutEditPolicy
extends ConstrainedContainerXYLayoutPolicy
implements EditorRequestConstants 
{

	public GuideContainerXYLayoutEditPolicy() {
		super();
	}
	
	protected Command createAddCommand(Request request, EditPart childEditPart, 
			Object constraint) 
	{	  
	  DrawComponent part = (DrawComponent)childEditPart.getModel();
		Rectangle rect = (Rectangle)constraint;

		SetConstraintCommand setConstraint = new SetConstraintCommand();
		setConstraint.setBounds(J2DUtil.toAWTRectangle(rect));
		setConstraint.setPart(part);
		setConstraint.setLabel(EditorPlugin.getResourceString("command.add.drawComponent"));
		setConstraint.setDebugLabel("MLDC_XYEP setConstraint");//$NON-NLS-1$
		
		Command cmd = setConstraint;
		cmd = chainGuideAttachmentCommand(request, part, cmd, true);
		cmd = chainGuideAttachmentCommand(request, part, cmd, false);
		cmd = chainGuideDetachmentCommand(request, part, cmd, true);
		return chainGuideDetachmentCommand(request, part, cmd, false);
	}		
	
  protected Command createChangeConstraintCommand(ChangeBoundsRequest request, 
      EditPart child, Object constraint) 
  {
		SetConstraintCommand cmd = new SetConstraintCommand();
		DrawComponent part = (DrawComponent)child.getModel();
		cmd.setPart(part);
		cmd.setBounds(J2DUtil.toAWTRectangle((Rectangle)constraint));
		Command result = cmd;
		
		if ((request.getResizeDirection() & PositionConstants.NORTH_SOUTH) != 0) 
		{
			Integer guidePos = (Integer)request.getExtendedData().get(SnapToGuides.KEY_HORIZONTAL_GUIDE);
			if (guidePos != null) {
			  result = chainGuideAttachmentCommand(request, part, result, true);
			} 
			else if (part.getHorizontalGuide() != null) 
			{
				// SnapToGuides didn't provide a horizontal guide, but this part is attached
				// to a horizontal guide.  Now we check to see if the part is attached to
				// the guide along the edge being resized.  If that is the case, we need to
				// detach the part from the guide; otherwise, we leave it alone.
				int alignment = part.getHorizontalGuide().getAlignment(part);
				int edgeBeingResized = 0;
				if ((request.getResizeDirection() & PositionConstants.NORTH) != 0)
				  edgeBeingResized = -1;
				else
				  edgeBeingResized = 1;
				if (alignment == edgeBeingResized)
				  result = result.chain(new ChangeGuideCommand(part, true));
			}
		}
		
		if ((request.getResizeDirection() & PositionConstants.EAST_WEST) != 0) 
		{
		  Integer guidePos = (Integer)request.getExtendedData().get(SnapToGuides.KEY_VERTICAL_GUIDE);
		  if (guidePos != null) {
		    	result = chainGuideAttachmentCommand(request, part, result, false);
		  } 
		  else if (part.getVerticalGuide() != null) 
		  {
				int alignment = part.getVerticalGuide().getAlignment(part);
				int edgeBeingResized = 0;
				if ((request.getResizeDirection() & PositionConstants.WEST) != 0)
				edgeBeingResized = -1;
				else
				edgeBeingResized = 1;
				if (alignment == edgeBeingResized)
				  result = result.chain(new ChangeGuideCommand(part, false));
			}
		}
		
		if (request.getType().equals(REQ_MOVE_CHILDREN)
		|| request.getType().equals(REQ_ALIGN_CHILDREN)) 
		{
			result = chainGuideAttachmentCommand(request, part, result, true);
			result = chainGuideAttachmentCommand(request, part, result, false);
			result = chainGuideDetachmentCommand(request, part, result, true);
			result = chainGuideDetachmentCommand(request, part, result, false);
		}
		
		return result;
  }  
	
	protected Command chainGuideAttachmentCommand(Request request, DrawComponent part, Command cmd, boolean horizontal) 
	{
		Command result = cmd;
		
		// Attach to guide, if one is given
		Integer guidePos = (Integer)request.getExtendedData()
				.get(horizontal ? SnapToGuides.KEY_HORIZONTAL_GUIDE
				                : SnapToGuides.KEY_VERTICAL_GUIDE);
		if (guidePos != null) {
			int alignment = ((Integer)request.getExtendedData()
					.get(horizontal ? SnapToGuides.KEY_HORIZONTAL_ANCHOR
					                : SnapToGuides.KEY_VERTICAL_ANCHOR)).intValue();
			ChangeGuideCommand cgm = new ChangeGuideCommand(part, horizontal);
			cgm.setNewGuide(findGuideAt(guidePos.intValue(), horizontal), alignment);
			result = result.chain(cgm);
		}

		return result;
	}	
	
	protected Command chainGuideDetachmentCommand(Request request, DrawComponent part,
			Command cmd, boolean horizontal) {
		Command result = cmd;
		
		// Detach from guide, if none is given
		Integer guidePos = (Integer)request.getExtendedData()
				.get(horizontal ? SnapToGuides.KEY_HORIZONTAL_GUIDE
				                : SnapToGuides.KEY_VERTICAL_GUIDE);
		if (guidePos == null)
			result = result.chain(new ChangeGuideCommand(part, horizontal));

		return result;
	}	
  
	protected EditorGuide findGuideAt(int pos, boolean horizontal) 
	{
		RulerProvider provider = ((RulerProvider)getHost().getViewer().getProperty(
				horizontal ? RulerProvider.PROPERTY_VERTICAL_RULER 
				: RulerProvider.PROPERTY_HORIZONTAL_RULER));
		return (EditorGuide)provider.getGuideAt(pos);
	}  
}
