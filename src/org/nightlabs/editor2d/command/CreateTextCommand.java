/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

package org.nightlabs.editor2d.command;

import java.awt.Font;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.TextDrawComponent;
import org.nightlabs.editor2d.impl.TextDrawComponentImpl;
import org.nightlabs.editor2d.request.TextCreateRequest;

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
