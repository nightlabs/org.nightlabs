/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 16.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.command;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;

import com.nightlabs.editor2d.DrawComponent;
import com.nightlabs.editor2d.EditorPlugin;
import com.nightlabs.editor2d.request.EditorRotateRequest;

public class RotateCommand 
extends Command 
{
  public static final Logger LOGGER = Logger.getLogger(RotateCommand.class);  
  protected Map dc2Rotation; 
  protected Map dc2RotationCenter;
  protected EditorRotateRequest request;
  protected boolean multiple;
  protected Point rotationCenter;
  
  public RotateCommand(EditorRotateRequest request) 
  {
    super();
    setLabel(EditorPlugin.getResourceString("command.rotate"));
    this.request = request;
    this.multiple = request.isMultiple();
    this.rotationCenter = request.getRotationCenter();
  }

  public void execute() 
  {
    dc2Rotation = new HashMap(request.getEditParts().size());
    dc2RotationCenter = new HashMap(request.getEditParts().size());
    for (Iterator it = request.getEditParts().iterator(); it.hasNext(); ) 
    {
      EditPart editPart = (EditPart) it.next();
      DrawComponent dc = (DrawComponent) editPart.getModel();
      dc2Rotation.put(dc, new Double(dc.getRotation()));
      if (multiple) {
        dc2RotationCenter.put(dc, new Point(dc.getTmpRotationX(), dc.getTmpRotationY()));
      } else {
        dc2RotationCenter.put(dc, new Point(dc.getRotationX(), dc.getRotationY()));        
      }
      double realRotation = rotation + dc.getRotation();
      if (multiple) {
        dc.setTmpRotationX(rotationCenter.x);
        dc.setTmpRotationY(rotationCenter.y);
      } else {
        dc.setRotationX(rotationCenter.x);
        dc.setRotationY(rotationCenter.y);
      }
      dc.setRotation(realRotation); 
      dc.setTmpRotationX(DrawComponent.ROTATION_X_DEFAULT);
      dc.setTmpRotationY(DrawComponent.ROTATION_Y_DEFAULT);
    }
  }   
  
  public void redo() 
  {
    for (Iterator it = dc2Rotation.keySet().iterator(); it.hasNext(); ) 
    {
      DrawComponent dc = (DrawComponent) it.next();
      if (multiple) {
        dc.setTmpRotationX(rotationCenter.x);
        dc.setTmpRotationY(rotationCenter.y);
      } else {
        dc.setRotationX(rotationCenter.x);
        dc.setRotationY(rotationCenter.y);
      }
      double realRotation = rotation + dc.getRotation();
      dc.setRotation(realRotation);
      dc.setTmpRotationX(DrawComponent.ROTATION_X_DEFAULT);
      dc.setTmpRotationY(DrawComponent.ROTATION_Y_DEFAULT);      
    }    
  }

  public void undo() 
  {
    for (Iterator it = dc2Rotation.keySet().iterator(); it.hasNext(); ) 
    {
      DrawComponent dc = (DrawComponent) it.next();
      double oldRotation = ((Double)dc2Rotation.get(dc)).doubleValue();
      Point oldRotationCenter = (Point) dc2RotationCenter.get(dc);
      if (multiple) {
        dc.setTmpRotationX(rotationCenter.x);
        dc.setTmpRotationY(rotationCenter.y);                
      } else {
        dc.setRotationX(oldRotationCenter.x);
        dc.setRotationY(oldRotationCenter.y);
      }      
      dc.setRotation(oldRotation);
      dc.setTmpRotationX(DrawComponent.ROTATION_X_DEFAULT);
      dc.setTmpRotationY(DrawComponent.ROTATION_Y_DEFAULT);      
    }
  }

  protected double rotation;
  public double getRotation() {
    return rotation;
  }
  public void setRotation(double rotation) {
    this.rotation = rotation;
  }
}
