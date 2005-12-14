/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 13.04.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.command;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;

import com.nightlabs.editor2d.DrawComponent;
import com.nightlabs.editor2d.EditorPlugin;
import com.nightlabs.editor2d.request.EditorRotateCenterRequest;
import com.nightlabs.editor2d.util.EditorUtil;


public class RotateCenterCommand 
extends Command 
{
  protected Map dc2RotationCenter;
  protected EditorRotateCenterRequest request;
  
  protected Point rotationCenter;    
  public Point getRotationCenter() {
    return rotationCenter;
  }
  public void setRotationCenter(Point rotationCenter) {
    this.rotationCenter = rotationCenter;
  }
  
  protected boolean multiple = false;    
  public boolean isMultiple() {
    return multiple;
  }
  public void setMultiple(boolean multiple) {
    this.multiple = multiple;
  }
  
  public RotateCenterCommand(EditorRotateCenterRequest request) 
  {
    super();
    this.request = request;
    this.multiple = request.isMultiple();
    setLabel(EditorPlugin.getResourceString("command.rotate.center"));
  }

  public void execute() 
  {
    dc2RotationCenter = new HashMap(request.getEditParts().size());
    for (Iterator it = request.getEditParts().iterator(); it.hasNext(); ) 
    {
      EditPart editPart = (EditPart) it.next();
      DrawComponent dc = (DrawComponent) editPart.getModel();
      Point oldRotationCenter = new Point(dc.getRotationX(), dc.getRotationY());
      dc2RotationCenter.put(dc, oldRotationCenter);
      if (isMultiple()) {
        dc.setTmpRotationX(rotationCenter.x);
        dc.setTmpRotationY(rotationCenter.y);
      } else {
        dc.setRotationX(rotationCenter.x); 
        dc.setRotationY(rotationCenter.y);        
      }      
    }
    EditorUtil.selectEditParts(request.getEditParts());
  } 
  
  public void redo() 
  {
    for (Iterator it = dc2RotationCenter.keySet().iterator(); it.hasNext(); ) 
    {
      DrawComponent dc = (DrawComponent) it.next();
      if (isMultiple()) {
        dc.setTmpRotationX(rotationCenter.x);
        dc.setTmpRotationY(rotationCenter.y);
      } else {
        dc.setRotationX(rotationCenter.x); 
        dc.setRotationY(rotationCenter.y);        
      }      
    }
    EditorUtil.selectEditParts(request.getEditParts());    
  }
  
  public void undo() 
  {
    for (Iterator it = dc2RotationCenter.keySet().iterator(); it.hasNext(); ) 
    {
      DrawComponent dc = (DrawComponent) it.next();
      Point oldRotationCenter = (Point) dc2RotationCenter.get(dc);
      if (isMultiple()) {
        dc.setTmpRotationX(DrawComponent.ROTATION_X_DEFAULT);
        dc.setTmpRotationY(DrawComponent.ROTATION_Y_DEFAULT);
      } else {
        dc.setRotationX(oldRotationCenter.x);
        dc.setRotationY(oldRotationCenter.y);
      }       
    }
    EditorUtil.selectEditParts(request.getEditParts());    
  }   
}
