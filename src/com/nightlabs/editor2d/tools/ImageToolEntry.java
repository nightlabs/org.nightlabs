/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 21.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.tools;

import org.eclipse.gef.Tool;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.jface.resource.ImageDescriptor;

public class ImageToolEntry 
extends CombinedTemplateCreationEntry 
{
  protected final CreationFactory factory;
  
  public ImageToolEntry(String label, String shortDesc, Object template, 
      CreationFactory factory, ImageDescriptor iconSmall, ImageDescriptor iconLarge)
  {
    super(label, shortDesc, template, factory, iconSmall, iconLarge);
    this.factory = factory;
  }
  
  public Tool createTool() 
  {
    return new ImageTool(factory);
  }   
}
