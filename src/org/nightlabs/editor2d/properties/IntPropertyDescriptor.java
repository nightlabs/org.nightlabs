/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 03.11.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;


public class IntPropertyDescriptor 
extends PropertyDescriptor 
{

  /**
   * @param id
   * @param displayName
   */
  public IntPropertyDescriptor(Object id, String displayName) {
    super(id, displayName);
  }
  
  public CellEditor createPropertyEditor(Composite parent) 
  {
  	CellEditor editor = new IntCellEditor(parent);
  	if (getValidator() != null)
  		editor.setValidator(getValidator());
  	return editor;
  }
  
}
