/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 02.05.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.actions;

import org.eclipse.gef.ui.actions.EditorPartAction;
import org.eclipse.ui.IEditorPart;

import com.nightlabs.editor2d.AbstractEditor;
import com.nightlabs.editor2d.MultiLayerDrawComponent;


public abstract class AbstractRendererModeAction 
extends EditorPartAction 
{  
  protected MultiLayerDrawComponent mldc;
  
  /**
   * @param editor
   */
  public AbstractRendererModeAction(IEditorPart editor) {
    super(editor);
  }

  // TODO: check currentMode in RenderModeManager and if != getRenderMode() return true else false
  protected boolean calculateEnabled() 
  {
    return true;
  }
    
  public void run() 
  {
    if (getEditorPart() instanceof AbstractEditor) {
      AbstractEditor editor = ((AbstractEditor)getEditorPart());
      mldc = editor.getMultiLayerDrawComponent();
//      mldc.setRenderMode(getRenderMode());
      mldc.getRenderModeManager().setCurrentRenderMode(getRenderMode());
      editor.updateViewer();      
    }    
  }
    
  public abstract int getRenderMode();
  protected abstract void init();
}
