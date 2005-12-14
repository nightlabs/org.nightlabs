/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 11.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.command;

import java.awt.Font;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import com.nightlabs.editor2d.DrawComponentContainer;
import com.nightlabs.editor2d.EditorPlugin;
import com.nightlabs.editor2d.TextDrawComponent;
import com.nightlabs.editor2d.impl.TextDrawComponentImpl;
import com.nightlabs.editor2d.request.TextCreateRequest;

public class CreateTextCommand 
extends Command 
{  
  /** DrawComponentContainer to add to. */
  protected DrawComponentContainer parent;
  public void setParent(DrawComponentContainer parent) {
    this.parent = parent;
  }  
  
  protected Rectangle rect;
  public void setLocation(Rectangle rect) {
    this.rect = rect;
  }
  protected Rectangle getLocation() {
    return rect;
  }

  protected boolean shapeAdded;
  protected int drawOrderIndex;  
  protected TextCreateRequest request;
  
  public CreateTextCommand(TextCreateRequest request) 
  {
    super();
    setLabel(EditorPlugin.getResourceString("command.create.text"));  
    this.request = request; 
    this.textDrawComponent = (TextDrawComponent) request.getNewObject();
  }

  protected TextDrawComponent textDrawComponent;      
  public void execute() 
  {
    int x = getLocation().x;
    int y = getLocation().y;
    Font newFont = new Font(request.getFontName(), request.getFontStyle(), request.getFontSize());    
    textDrawComponent = new TextDrawComponentImpl(request.getText(), newFont, x, y);
    
    parent.addDrawComponent(textDrawComponent);
    textDrawComponent.setName(request.getText());
    shapeAdded = true;
    drawOrderIndex = parent.getDrawComponents().indexOf(textDrawComponent);    
  }
  
  public void redo() 
  { 
    parent.addDrawComponent(textDrawComponent, drawOrderIndex);    
  }  
  
  /* (non-Javadoc)
   * @see org.eclipse.gef.commands.Command#undo()
   */
  public void undo() 
  { 
    parent.removeDrawComponent(textDrawComponent);
  }
     
}
