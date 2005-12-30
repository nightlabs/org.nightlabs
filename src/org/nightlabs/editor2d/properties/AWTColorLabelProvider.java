/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 14.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.properties;

import java.awt.Color;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.util.ImageUtil;

public class AWTColorLabelProvider 
//implements ILabelProvider
extends LabelProvider
{  
  public AWTColorLabelProvider() 
  {
    super();    
  }
        
  public Image getImage(Object element) 
  {
    if (element instanceof Color)
      return ImageUtil.createColorImage((Color)element);
    
    return null;
  }

  public String getText(Object element) 
  {
    if (element instanceof Color)
    {
      Color color = (Color) element;
      return new String("("+color.getRed()+","+color.getGreen()+","+color.getBlue()+")");
    }
    return element == null ? "" : element.toString();//$NON-NLS-1$
  }

  public void dispose() 
  {
    
  }

}
