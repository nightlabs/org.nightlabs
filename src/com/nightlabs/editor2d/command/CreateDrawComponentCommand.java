/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 26.10.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.command;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import com.nightlabs.editor2d.DrawComponent;
import com.nightlabs.editor2d.DrawComponentContainer;
import com.nightlabs.editor2d.EditorPlugin;
import com.nightlabs.editor2d.util.J2DUtil;

public class CreateDrawComponentCommand 
extends Command
{
  public static final Logger LOGGER = Logger.getLogger(CreateDrawComponentCommand.class);
  
	/** The DrawComponent to add */
	protected DrawComponent drawComponent;
	/** DrawComponentContainer to add to. */
	protected DrawComponentContainer parent;
	/** True, if newDrawComponent was added to parent. */
	protected boolean shapeAdded;
	/** the DrawOrderIndex of the DrawComponentContainer */
	protected int drawOrderIndex;
	
	protected Rectangle rect;
	
	/**
	 * Create a command that will add a new DrawComponent to a MultiLayerDrawComponent.
	 * @param parent the MultiLayerDrawComponent that will hold the new element
	 * @param req     a request to create a new DrawComponent
	 * @throws IllegalArgumentException if any parameter is null, or the request
	 * 						  does not provide a new DrawComponent instance
	 */	
	public CreateDrawComponentCommand() 
	{
	  super(EditorPlugin.getResourceString("command_create_drawcomponent"));	  
	}
	
	public CreateDrawComponentCommand(String name) 
	{
	  super(name);	  
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	public boolean canUndo() {
		return shapeAdded;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() 
	{
    drawComponent.setBounds(J2DUtil.toAWTRectangle(rect));
	  
    parent.addDrawComponent(drawComponent);
    shapeAdded = true;
		drawOrderIndex = parent.getDrawComponents().indexOf(drawComponent);
    
    if (drawComponent instanceof DrawComponentContainer) {
      ((DrawComponentContainer)drawComponent).setParent(parent);
    }
	}	
		
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() 
	{ 
    parent.addDrawComponent(drawComponent, drawOrderIndex);    
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() 
	{ 
    parent.removeDrawComponent(drawComponent);
	}	
	
	public void setParent(DrawComponentContainer newParent) {
		parent = newParent;
	}	
	
	public void setLocation(Rectangle r) {
		rect = r;
	}	
	
	public void setChild(DrawComponent dc) {
		drawComponent = dc;
	}
	
	public DrawComponent getChild() {
	  return drawComponent;
	}
	
	public void setIndex(int index) {
	  this.drawOrderIndex = index;
	}
  
//  protected boolean ignoreSize = false;
//  public void setIgnoreSize(boolean ignoreSize) {
//    this.ignoreSize = ignoreSize;
//  }
}
