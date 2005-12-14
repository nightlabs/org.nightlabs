/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 11.11.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.dnd;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.requests.CreationFactory;

import com.nightlabs.editor2d.model.ModelCreationFactory;


public class EditorTemplateTransferDropTargetListener 
extends TemplateTransferDropTargetListener 
{
  public EditorTemplateTransferDropTargetListener(EditPartViewer viewer) {
  	super(viewer);
  }

  protected CreationFactory getFactory(Object template) 
  {
//  	if (template instanceof String)
//  		return new ModelCreationFactory((String)template);
    // TODO see if working
  	if (template instanceof Class)
  		return new ModelCreationFactory((Class)template);    
  	return null;
  }
}
