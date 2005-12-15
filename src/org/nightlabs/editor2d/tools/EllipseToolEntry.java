/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 16.02.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.tools;

import org.eclipse.gef.Tool;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.jface.resource.ImageDescriptor;


public class EllipseToolEntry 
extends CombinedTemplateCreationEntry 
{
  /**
   * The creation factory used with the returned creation tool.
   */
  protected final CreationFactory factory;
  
  /**
   * @param label
   * @param shortDesc
   * @param iconSmall
   * @param iconLarge
   */
  public EllipseToolEntry(String label, String shortDesc, Object template, 
      CreationFactory factory, ImageDescriptor iconSmall, ImageDescriptor iconLarge) 
  {
    super(label, shortDesc, template, factory, iconSmall, iconLarge);
    this.factory = factory;
  }

  /* (non-Javadoc)
   * @see org.eclipse.gef.palette.ToolEntry#createTool()
   */
  public Tool createTool() 
  {
    return new EllipseTool(factory);
  }
}
