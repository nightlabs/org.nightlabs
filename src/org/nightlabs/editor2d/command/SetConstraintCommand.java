/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 26.10.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.command;

import org.eclipse.gef.commands.Command;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorPlugin;

public class SetConstraintCommand 
extends Command
{
	private static final String Command_Label_Location = EditorPlugin.getResourceString("command_change_location");
	private static final String Command_Label_Resize = EditorPlugin.getResourceString("command_resize");

	private DrawComponent part;
	private java.awt.Rectangle oldBounds;
	private java.awt.Rectangle newBounds;

	public void execute() 
	{
//	  oldBounds = new java.awt.Rectangle(part.getX(), part.getY(), part.getWidth(), part.getHeight());
	  oldBounds = new java.awt.Rectangle(part.getBounds());  
	  part.setBounds(newBounds);	  
	}
	
	public String getLabel() 
	{
	  if ((oldBounds.getWidth() == newBounds.getWidth()) && 
	      (oldBounds.getHeight() == newBounds.getHeight()))
	    return Command_Label_Location;
	  
		return Command_Label_Resize;
	}	
	
	public void redo() 
	{
	  part.setBounds(newBounds);
	}
		
	public void setPart(DrawComponent part) 
	{
		this.part = part;
	}
		
	public void undo() 
	{
	  part.setBounds(oldBounds);
	}
	
	public void setBounds(java.awt.Rectangle bounds) 
	{
	  newBounds = bounds;
	}
}
