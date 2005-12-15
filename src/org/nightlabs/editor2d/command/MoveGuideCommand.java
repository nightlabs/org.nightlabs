/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 11.11.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.command;

import java.util.Iterator;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorGuide;
import org.nightlabs.editor2d.EditorPlugin;

public class MoveGuideCommand 
extends Command 
{
  private int pDelta;
  private EditorGuide guide;
  	
  public MoveGuideCommand(EditorGuide guide, int positionDelta) {
  	super(EditorPlugin.getResourceString("command_move_guide"));
  	this.guide = guide;
  	pDelta = positionDelta;
  }

  public void execute() 
  {
  	guide.setPosition(guide.getPosition() + pDelta);
  	Iterator iter = guide.getMap().keySet().iterator();
  	while (iter.hasNext()) {
  		DrawComponent part = (DrawComponent)iter.next();
  		Point location = new Point(part.getX(), part.getY()).getCopy();  		
//  		Point location = part.getLocation().getCopy();
//  		Point location = new Point(part.getX(), part.getY());
  		
  		if (guide.isHorizontal()) {
  			location.y += pDelta;
  		} else {
  			location.x += pDelta;
  		}
//  		part.setLocation(location);
  		part.setX(location.x);
  		part.setY(location.y);
  	}
  }

  public void undo() {
  	guide.setPosition(guide.getPosition() - pDelta);
  	Iterator iter = guide.getMap().keySet().iterator();
  	while (iter.hasNext()) {
  	  DrawComponent part = (DrawComponent)iter.next();
//		Point location = part.getLocation().getCopy();
  		Point location = new Point(part.getX(), part.getY());
  		if (guide.isHorizontal()) {
  			location.y -= pDelta;
  		} else {
  			location.x -= pDelta;
  		}
//		part.setLocation(location);
  		part.setX(location.x);
  		part.setY(location.y);
  	}
  }
  
}
