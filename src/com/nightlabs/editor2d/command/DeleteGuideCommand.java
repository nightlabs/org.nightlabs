/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 11.11.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.command;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.gef.commands.Command;

import com.nightlabs.editor2d.DrawComponent;
import com.nightlabs.editor2d.EditorGuide;
import com.nightlabs.editor2d.EditorPlugin;
import com.nightlabs.editor2d.EditorRuler;

public class DeleteGuideCommand 
extends Command 
{
  private EditorRuler parent;
  private EditorGuide guide;
  private Map oldParts;

  public DeleteGuideCommand(EditorGuide guide, EditorRuler parent) {
  	super(EditorPlugin.getResourceString("command_delete_guide"));
  	this.guide = guide;
  	this.parent = parent;
  }

  public boolean canUndo() {
  	return true;
  }

  public void execute() 
  {
  	oldParts = new HashMap(guide.getMap());
  	Iterator iter = oldParts.keySet().iterator();
  	while (iter.hasNext()) {
  		guide.detachPart((DrawComponent)iter.next());
  	}
  	parent.removeGuide(guide);
  }
  
  public void undo() 
  {
  	parent.addGuide(guide);
  	Iterator iter = oldParts.keySet().iterator();
  	while (iter.hasNext()) {
  	  DrawComponent part = (DrawComponent)iter.next();
  		guide.attachPart(part, ((Integer)oldParts.get(part)).intValue());
  	}
  }
  	  
}
