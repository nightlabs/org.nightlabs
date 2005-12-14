/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 15.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.properties;

import com.nightlabs.rcp.property.ComboBoxPropertyDescriptor;
import com.nightlabs.util.FontUtil;

public class FontNamePropertyDescriptor 
extends ComboBoxPropertyDescriptor 
{
  public FontNamePropertyDescriptor(Object id, String displayName) 
  {
    super(id, displayName, FontUtil.getSystemFonts());
  }
}
