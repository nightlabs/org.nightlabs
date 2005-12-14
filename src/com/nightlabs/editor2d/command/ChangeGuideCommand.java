/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 12.11.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.command;

import org.eclipse.gef.commands.Command;

import com.nightlabs.editor2d.DrawComponent;
import com.nightlabs.editor2d.EditorGuide;

public class ChangeGuideCommand 
extends Command 
{
  private DrawComponent part;
  private EditorGuide oldGuide, newGuide;
  private int oldAlign, newAlign;
  private boolean horizontal;

  public ChangeGuideCommand(DrawComponent part, boolean horizontalGuide) {
  	super();
  	this.part = part;
  	horizontal = horizontalGuide;
  }

  protected void changeGuide(EditorGuide oldGuide, EditorGuide newGuide, int newAlignment) {
  	if (oldGuide != null && oldGuide != newGuide) {
  		oldGuide.detachPart(part);
  	}
  	// You need to re-attach the part even if the oldGuide and the newGuide are the same
  	// because the alignment could have changed
  	if (newGuide != null) {
  		newGuide.attachPart(part, newAlignment);
  	}
  }

  public void execute() {
  	// Cache the old values
  	oldGuide = horizontal ? part.getHorizontalGuide() : part.getVerticalGuide();		
  	if (oldGuide != null)
  		oldAlign = oldGuide.getAlignment(part);
  	
  	redo();
  }

  public void redo() {
  	changeGuide(oldGuide, newGuide, newAlign);
  }

  public void setNewGuide(EditorGuide guide, int alignment) {
  	newGuide = guide;
  	newAlign = alignment;
  }

  public void undo() {
  	changeGuide(newGuide, oldGuide, oldAlign);
  }
}
