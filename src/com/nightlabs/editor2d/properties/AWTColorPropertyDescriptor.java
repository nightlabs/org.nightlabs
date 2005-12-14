/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 02.11.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class AWTColorPropertyDescriptor 
extends PropertyDescriptor 
{
  /**
   * @param id
   * @param displayName
   */
  public AWTColorPropertyDescriptor(Object id, String displayName) 
  {
    super(id, displayName);    
    setLabelProvider(new AWTColorLabelProvider());    
  }

  public CellEditor createPropertyEditor(Composite parent) 
  {
  	CellEditor editor = new AWTColorCellEditor(parent);
  	if (getValidator() != null)
  		editor.setValidator(getValidator());
            
  	return editor;
  }  
}
