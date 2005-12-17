/**
 * <p> Project: org.nightlabs.gui </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 29.07.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.base.property;

import java.util.Collection;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ColorAssignablePropertyDescriptor 
extends AssignablePropertyDescriptor
{
  public ColorAssignablePropertyDescriptor(Object id, String displayName, Collection assignables) 
  {
  	super(id, displayName, assignables);
  }
  
  public CellEditor createPropertyEditor(Composite parent) 
  {
    CellEditor editor = new ColorAssignableCellEditor(assignables, parent, SWT.READ_ONLY);
    if (getValidator() != null)
      editor.setValidator(getValidator());
    return editor;
  }    
}
