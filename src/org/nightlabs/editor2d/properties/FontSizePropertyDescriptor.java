/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 14.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

import org.nightlabs.base.property.ComboBoxPropertyDescriptor;
import org.nightlabs.util.FontUtil;

public class FontSizePropertyDescriptor  
extends ComboBoxPropertyDescriptor
{

  public FontSizePropertyDescriptor(Object id, String displayName) {
    super(id, displayName, FontUtil.getFontSizes());
  }

  public CellEditor createPropertyEditor(Composite parent) 
  {
    CellEditor editor = new FontSizeCellEditor(parent);
    if (getValidator() != null)
      editor.setValidator(getValidator());
            
    return editor;
  }   
}
