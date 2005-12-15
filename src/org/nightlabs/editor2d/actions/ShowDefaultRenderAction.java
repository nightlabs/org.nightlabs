/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 02.05.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.actions;

import org.eclipse.ui.IEditorPart;

import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.render.RenderConstants;


public class ShowDefaultRenderAction 
extends AbstractRendererModeAction
{
  public static final String ID = ShowDefaultRenderAction.class.getName();  
  
  public ShowDefaultRenderAction(IEditorPart editor) {
    super(editor);
  }

  public void init() 
  {
    setText(EditorPlugin.getResourceString("action.showDefaultRender.text"));
    setToolTipText(EditorPlugin.getResourceString("action.showDefaultRender.tooltip"));
    setId(ID);
//    setImageDescriptor(ImageDescriptor.createFromFile(EditorPlugin.class, "icons/sun16.gif"));
  } 
    
  public int getRenderMode() 
  {
    return RenderConstants.DEFAULT_MODE;
  }
}
