/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 12.01.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.tools;

import java.util.List;

import org.eclipse.gef.tools.PanningSelectionTool;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

import com.nightlabs.editor2d.EditorStateManager;
import com.nightlabs.editor2d.request.EditorRequestConstants;


public class EditorSelectionTool 
extends PanningSelectionTool
implements EditorRequestConstants
{
  protected static final int STATE_ROTATE = PanningSelectionTool.MAX_STATE << 1;
  protected static final int STATE_ROTATE_IN_PROGRESS = STATE_ROTATE << 1;
  protected static final int MAX_STATE = STATE_ROTATE;
  
  public EditorSelectionTool() {
    super();
  }
  
  protected boolean handleDoubleClick(int button) 
  {
    if (EditorStateManager.getCurrentState() == EditorStateManager.STATE_NORMAL_SELECTION) 
    {
      List selectedParts = getCurrentViewer().getSelectedEditParts();
      if (!selectedParts.isEmpty()) {
        stateTransition(STATE_ROTATE, STATE_INITIAL);
        EditorStateManager.setRotateMode(selectedParts);
      }
    }
    else if (EditorStateManager.getCurrentState() == EditorStateManager.STATE_ROTATE) 
    {
      List selectedParts = getCurrentViewer().getSelectedEditParts();      
      if (!selectedParts.isEmpty()) {
        stateTransition(STATE_ROTATE, STATE_INITIAL);
        EditorStateManager.setNormalSelectionMode(selectedParts);
      }        
    }
    return true;
  }
    
  protected String getCommandName() 
  {
    if (isInState(STATE_ROTATE) || isInState(STATE_ROTATE_IN_PROGRESS))
      return REQ_ROTATE;
  
    return super.getCommandName();
  }
     
  public void activate() 
  {
    EditorStateManager.setCurrentState(EditorStateManager.STATE_NORMAL_SELECTION);    
    super.activate();
  }
    
  protected boolean handleKeyDown(KeyEvent e) 
  {
    if (e.character == SWT.ESC) 
    {
      EditorStateManager.setCurrentState(EditorStateManager.STATE_NORMAL_SELECTION);            
      getCurrentViewer().deselectAll();
    }
      
    return super.handleKeyDown(e);
  }
    
}
