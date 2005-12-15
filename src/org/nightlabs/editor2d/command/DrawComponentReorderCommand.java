/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 14.12.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.command;

import org.eclipse.gef.commands.Command;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.EditorPlugin;


public class DrawComponentReorderCommand 
extends Command 
{
  private int oldIndex, newIndex;
  private DrawComponent child;
  private DrawComponentContainer parent;

  public DrawComponentReorderCommand(DrawComponent child, DrawComponentContainer parent, int newIndex ) 
  {
  	super(EditorPlugin.getResourceString("command_reorder_drawcomponent"));
  	this.child = child;
  	this.parent = parent;
  	this.newIndex = newIndex;
  }

  public void execute() 
  {
  	oldIndex = parent.getDrawComponents().indexOf(child);
  	parent.getDrawComponents().remove(child);
  	parent.getDrawComponents().add(newIndex, child);
  }

  public void undo() 
  {
  	parent.getDrawComponents().remove(child);
  	parent.getDrawComponents().add(oldIndex, child);
  }
}
