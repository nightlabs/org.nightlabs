/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 23.02.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.tools;

import org.eclipse.gef.Tool;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.jface.resource.ImageDescriptor;


public class ZoomToolEntry 
extends ToolEntry 
{     
  /**
   * @param label
   * @param shortDesc
   * @param iconSmall
   * @param iconLarge
   */
  public ZoomToolEntry(String label, String shortDesc,
      ImageDescriptor iconSmall, ImageDescriptor iconLarge) 
  {
    super(label, shortDesc, iconSmall, iconLarge);
  }

  /* (non-Javadoc)
   * @see org.eclipse.gef.palette.ToolEntry#createTool()
   */
  public Tool createTool() {
    return new ZoomTool();
  }

}
