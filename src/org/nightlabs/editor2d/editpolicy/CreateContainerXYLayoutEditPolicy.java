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

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.command.CreateDrawComponentCommand;
import org.nightlabs.editor2d.command.CreateImageCommand;
import org.nightlabs.editor2d.command.CreateShapeCommand;
import org.nightlabs.editor2d.command.CreateTextCommand;
import org.nightlabs.editor2d.request.EditorBoundsRequest;
import org.nightlabs.editor2d.request.EditorCreateRequest;
import org.nightlabs.editor2d.request.ImageCreateRequest;
import org.nightlabs.editor2d.request.LineCreateRequest;
import org.nightlabs.editor2d.request.TextCreateRequest;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class CreateContainerXYLayoutEditPolicy 
extends FeedbackContainerXYLayoutEditPolicy 
{

	public CreateContainerXYLayoutEditPolicy() {
		super();
	}
	
  public DrawComponentContainer getDrawComponentContainer() 
  {
    DrawComponentContainer container = (DrawComponentContainer)getHost().getModel();
    return container.getRoot().getCurrentLayer();    
  }	
	
	protected Command getCreateCommand(CreateRequest request) 
	{ 
	  if (request instanceof EditorCreateRequest) 
	  {	      
	    EditorCreateRequest req = (EditorCreateRequest) request;	    
	    return getEditorCreateCommand(req);
	  } 
	  else {
	    CreateDrawComponentCommand create = new CreateDrawComponentCommand();
			DrawComponent newPart = (DrawComponent)request.getNewObject();
			create.setChild(newPart);			
			create.setParent(getDrawComponentContainer());
			
			Rectangle constraint = (Rectangle)getConstraintFor(request);
			create.setBounds(constraint);			
			
			Command cmd = chainGuideAttachmentCommand(request, newPart, create, true);
			return chainGuideAttachmentCommand(request, newPart, cmd, false);			
	  }		
	}
		
	protected Command getEditorCreateCommand(EditorCreateRequest request) 
	{
	  // TODO: Optimize Command (dont create each time a new Command)
    CreateShapeCommand create = new CreateShapeCommand();	    
    create.setGeneralShape(request.getGeneralShape());
    ShapeDrawComponent newPart = (ShapeDrawComponent)request.getNewObject();
    create.setChild(newPart);				     
		create.setParent(getDrawComponentContainer());    
			
		Rectangle constraint = new Rectangle();
		if (request instanceof LineCreateRequest) {
		  LineCreateRequest lineRequest = (LineCreateRequest) request;
		  if (lineRequest.getCreationBounds() != null)
		    constraint = getConstraintRectangleFor(lineRequest.getCreationBounds());
		}
		else 
		  constraint = (Rectangle)getConstraintFor((EditorBoundsRequest)request);
		
		create.setBounds(constraint);    
		Command cmd = chainGuideAttachmentCommand(request, newPart, create, true);
		return chainGuideAttachmentCommand(request, newPart, cmd, false);				      			    	  
	}	
	
//  protected EditPolicy createChildEditPolicy(EditPart child) {  	
//  	return new DrawComponentResizeEditPolicy();
//  }
  
  public Command getCommand(Request request) 
  {
    if (request instanceof TextCreateRequest)
      return getCreateTextCommand((TextCreateRequest)request);
        
    if (request instanceof ImageCreateRequest)
      return getCreateImageCommand((ImageCreateRequest)request);
    
//    if (request instanceof EditorShearRequest)
//      return getShearCommand((EditorShearRequest) request);
      
  	return super.getCommand(request);
  }  
  
//  protected Command getShearCommand(EditorShearRequest request) 
//  {
//    ShearCommand cmd = new ShearCommand();
//    cmd.setEditParts(request.getEditParts());
//    cmd.setAffineTransform(request.getAffineTransform());
//    return cmd;
//  }
  
  public Command getCreateTextCommand(TextCreateRequest request) 
  {
    // TODO: Optimize Command (dont create each time a new Command)
    CreateTextCommand create = new CreateTextCommand(request);                 
    create.setParent(getDrawComponentContainer());    
      
    Rectangle constraint = new Rectangle();
    constraint = (Rectangle)getConstraintFor((EditorBoundsRequest)request);    
    create.setBounds(constraint);
    
    return create;
  }  
  
  public Command getCreateImageCommand(ImageCreateRequest request) 
  {
    CreateImageCommand create = new CreateImageCommand();
    create.setFileName(request.getFileName());    
    DrawComponent newPart = (DrawComponent)request.getNewObject();
    create.setChild(newPart);     
    create.setParent(getDrawComponentContainer());    
    Rectangle constraint = (Rectangle)getConstraintFor(request);
    create.setBounds(constraint);  
    return create;
  }
  
}
