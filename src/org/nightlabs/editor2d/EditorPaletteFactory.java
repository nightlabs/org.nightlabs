/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 26.10.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d;

import org.eclipse.gef.requests.CreationFactory;

import org.nightlabs.editor2d.model.ModelCreationFactory;


public class EditorPaletteFactory
extends AbstractPaletteFactory
{
  public CreationFactory getCreationFactory(Class targetClass) 
  {          
    return new ModelCreationFactory(targetClass);
  } 
}
