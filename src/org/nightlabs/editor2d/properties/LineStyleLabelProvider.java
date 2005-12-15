/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 14.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.properties;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class LineStyleLabelProvider 
extends LabelProvider 
{

  public LineStyleLabelProvider() 
  {
    super();
  }

  public Image getImage(Object element) 
  {
    if (element instanceof Integer) 
    {
      int lineStyle = ((Integer)element).intValue();
      return org.nightlabs.rcp.util.ImageUtil.createLineStyleImage(lineStyle);
    }
    return super.getImage(element);
  }

  public String getText(Object element) 
  {
    if (element instanceof Integer) 
    {
      int lineStyle = ((Integer)element).intValue();
      return "";
    }
    return super.getText(element);
  }
  
}
