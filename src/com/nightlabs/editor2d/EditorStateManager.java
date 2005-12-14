/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 20.01.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d;

import java.util.ArrayList;
import java.util.List;

import com.nightlabs.editor2d.edit.ShapeDrawComponentEditPart;
import com.nightlabs.editor2d.util.EditorUtil;


public class EditorStateManager 
{
  public static final int STATE_NORMAL_SELECTION = 1;
  public static final int STATE_EDIT_SHAPE = 2;
  public static final int STATE_ROTATE = 3;
  
  public EditorStateManager() {
    super();
  }  
  
  protected static int currentState = STATE_NORMAL_SELECTION;  
  public static int getCurrentState() {
    return currentState;
  }
  public static void setCurrentState(int currentState) {
    EditorStateManager.currentState = currentState;
  }
   
  public static void setEditShapeMode(ShapeDrawComponentEditPart sdc) 
  {
    setCurrentState(STATE_EDIT_SHAPE);
//    sdc.getViewer().deselectAll();
//    sdc.getViewer().select(sdc);
    EditorUtil.selectEditPart(sdc);
  }

  public static void setNormalSelectionMode(List editParts) 
  {
    setCurrentState(STATE_NORMAL_SELECTION);
    EditorUtil.selectEditParts(editParts);    
  }
  
  public static void setRotateMode(List editParts) 
  {
    setCurrentState(STATE_ROTATE);
    EditorUtil.selectEditParts(editParts);
  }
    
}
