/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 11.11.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.command;

import org.eclipse.gef.commands.Command;

import org.nightlabs.editor2d.Editor2DFactory;
import org.nightlabs.editor2d.EditorGuide;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.EditorRuler;

public class CreateGuideCommand 
extends Command 
{
  private EditorGuide guide;  
  private EditorRuler parent;
  private int position;

  public CreateGuideCommand(EditorRuler parent, int position) {
  	super(EditorPlugin.getResourceString("command_create_guide"));
  	this.parent = parent;
  	this.position = position;
  }

  public boolean canUndo() {
  	return true;
  }

  public void execute() 
  {
    if (guide == null)
      guide = Editor2DFactory.eINSTANCE.createEditorGuide();
    
  	guide.setPosition(position);
  	guide.setHorizontal(!parent.isHorizontal());
  	parent.addGuide(guide);  
  }

  public void undo() {
    parent.removeGuide(guide);
  }

}
