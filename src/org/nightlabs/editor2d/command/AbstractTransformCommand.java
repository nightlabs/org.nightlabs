/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 14.04.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.command;

import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorPlugin;


public abstract class AbstractTransformCommand 
extends Command 
{ 
  protected Map dc2AffineTransform;  
  public AbstractTransformCommand() 
  {
    super();
    setLabel(EditorPlugin.getResourceString("command.transform"));
  }

  protected List editParts;  
  public List getEditParts() {
    return editParts;
  }
  public void setEditParts(List editParts) {
    this.editParts = editParts;
  }
  
  protected AffineTransform affineTransform;    
  public AffineTransform getAffineTransform() {
    return affineTransform;
  }
  public void setAffineTransform(AffineTransform affineTransform) {
    this.affineTransform = affineTransform;
  }
  
  public void execute() 
  {
    dc2AffineTransform = new HashMap(getEditParts().size());
    for (Iterator it = getEditParts().iterator(); it.hasNext(); ) 
    {
      EditPart editPart = (EditPart) it.next();
      DrawComponent dc = (DrawComponent) editPart.getModel();
      AffineTransform oldAffineTransform = dc.getAffineTransform(); 
      dc2AffineTransform.put(dc, oldAffineTransform);
      AffineTransform at = new AffineTransform(oldAffineTransform);
      at.preConcatenate(getAffineTransform());
      dc.setAffineTransform(at); 
    }    
  }
  public void redo() 
  {
    for (Iterator it = dc2AffineTransform.keySet().iterator(); it.hasNext(); ) 
    {
      DrawComponent dc = (DrawComponent) it.next(); 
      dc.setAffineTransform(getAffineTransform()); 
    }        
  }
  public void undo() 
  {
    for (Iterator it = dc2AffineTransform.keySet().iterator(); it.hasNext(); ) 
    {
      DrawComponent dc = (DrawComponent) it.next(); 
      AffineTransform oldAffineTransform = (AffineTransform) dc2AffineTransform.get(dc);
      dc.setAffineTransform(oldAffineTransform); 
    }     
  }
}
