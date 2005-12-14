/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 18.02.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.properties;

import org.eclipse.jface.viewers.ICellEditorValidator;


public class BooleanValidator 
implements ICellEditorValidator 
{
  public String isValid(Object value) 
  {
    if (value instanceof Boolean)
      return null;
    else
      return "value is not instanceof Boolean!";
  }

}