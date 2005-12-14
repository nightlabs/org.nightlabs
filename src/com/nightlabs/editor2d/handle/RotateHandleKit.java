/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 16.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.handle;

import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Handle;

import com.nightlabs.editor2d.edit.AbstractDrawComponentEditPart;

public class RotateHandleKit 
{

  public RotateHandleKit() {
    super();
  }

  public static void addHandles(List selectedEditParts, List handles) 
  {
    int counter = 0;
    for (Iterator it = selectedEditParts.iterator(); it.hasNext(); ) {      
      GraphicalEditPart editPart = (GraphicalEditPart) it.next();
      if (editPart instanceof AbstractDrawComponentEditPart)
        createRotateHandles((AbstractDrawComponentEditPart)editPart, handles);      
    }
    
    handles.add(createRotateCenterHandle(selectedEditParts));
  }
  
  protected static Handle createRotateCenterHandle(List editParts)
  {
    RotateCenterHandle handle = new RotateCenterHandle(editParts);
    return handle;
  }
  
//  protected static void createHandles(GraphicalEditPart owner, List handles)
  protected static void createRotateHandles(AbstractDrawComponentEditPart owner, List handles)
  {
    handles.add(createRotateHandle(owner, PositionConstants.NORTH_WEST));
    handles.add(createRotateHandle(owner, PositionConstants.NORTH_EAST));
    handles.add(createRotateHandle(owner, PositionConstants.SOUTH_EAST));
    handles.add(createRotateHandle(owner, PositionConstants.SOUTH_WEST));
    
//    handles.add(createRotateCenterHandle(owner));
    
//    handles.add(createShearHandle(owner, PositionConstants.NORTH));
//    handles.add(createShearHandle(owner, PositionConstants.WEST));
//    handles.add(createShearHandle(owner, PositionConstants.SOUTH));
//    handles.add(createShearHandle(owner, PositionConstants.EAST));
  }
  
  protected static Handle createRotateHandle(AbstractDrawComponentEditPart owner, int direction) 
  {
    RotateHandle handle = new RotateHandle(owner, direction);
    return handle;
  }  
  
  protected static Handle createShearHandle(AbstractDrawComponentEditPart owner, int direction) 
  {
    ShearHandle handle = new ShearHandle(owner, direction);
    return handle;
  }   
}
