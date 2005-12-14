/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 11.11.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.actions;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.ui.IEditorPart;

import com.nightlabs.editor2d.model.ModelCreationFactory;


public class EditorPasteTemplateAction 
extends PasteTemplateAction 
{

  /**
   * Constructor for LogicPasteTemplateAction.
   * @param editor
   */
  public EditorPasteTemplateAction(IEditorPart editor) {
  	super(editor);
  }

  /**
   * @see org.eclipse.gef.ui.actions.PasteTemplateAction#getFactory(java.lang.Object)
   */
  protected CreationFactory getFactory(Object template) 
  {
    // TODO see if it works with class
    if (template instanceof Class)      
      return new ModelCreationFactory((Class)template);
    
    return null;
  }

  /**
   * 
   * @see org.eclipse.gef.examples.logicdesigner.actions.PasteTemplateAction#getPasteLocation()
   */
  protected Point getPasteLocation() {
  	return new Point(10, 10);
  }

}
