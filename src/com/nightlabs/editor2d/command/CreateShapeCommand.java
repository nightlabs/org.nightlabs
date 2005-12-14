/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 29.12.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.command;

import org.apache.log4j.Logger;

import com.nightlabs.editor2d.EditorPlugin;
import com.nightlabs.editor2d.LineDrawComponent;
import com.nightlabs.editor2d.ShapeDrawComponent;
import com.nightlabs.editor2d.j2d.GeneralShape;


public class CreateShapeCommand 
//extends Command 
extends CreateDrawComponentCommand
{
  public static final Logger LOGGER = Logger.getLogger(CreateShapeCommand.class);
  	
  protected GeneralShape generalShape;  
  public void setGeneralShape(GeneralShape generalShape) {
    this.generalShape = generalShape;
  }
  	
	/**
	 * Create a command that will add a new DrawComponent to a MultiLayerDrawComponent.
	 * @param parent the MultiLayerDrawComponent that will hold the new element
	 * @param req     a request to create a new DrawComponent
	 * @throws IllegalArgumentException if any parameter is null, or the request
	 * 						  does not provide a new DrawComponent instance
	 */	
	public CreateShapeCommand() 
	{
	  super(EditorPlugin.getResourceString("command_create_shape"));	
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() 
	{		
	  if (generalShape != null)
      getShapeDrawComponent().setGeneralShape(generalShape);	    	    
	      		
		if (drawComponent instanceof LineDrawComponent)
      getShapeDrawComponent().setFill(false);
    
    super.execute();
	}	
			
  protected ShapeDrawComponent getShapeDrawComponent() {
   return (ShapeDrawComponent) getChild();
  }
}
