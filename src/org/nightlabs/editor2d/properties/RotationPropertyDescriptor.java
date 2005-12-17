/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 18.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import org.nightlabs.base.property.ComboBoxLabelProvider;

public class RotationPropertyDescriptor 
//extends ComboBoxPropertyDescriptor 
extends PropertyDescriptor
{
  public static final String[] defaultRotations = new String[] {"-180", "-90", "-45", "0", "45", "90", "180"};
  
  public RotationPropertyDescriptor(Object id, String displayName) 
  {
    super(id, displayName);
  }

  /**
   * The <code>ComboBoxPropertyDescriptor</code> implementation of this 
   * <code>IPropertyDescriptor</code> method creates and returns a new
   * <code>ComboBoxCellEditor</code>.
   * <p>
   * The editor is configured with the current validator if there is one.
   * </p>
   */
  public CellEditor createPropertyEditor(Composite parent) 
  {
    CellEditor editor = new RotationCellEditor(parent, defaultRotations, SWT.NONE);
    if (getValidator() != null)
      editor.setValidator(getValidator());
    return editor;
  }    
  
  public ILabelProvider getLabelProvider() 
  {
    if (isLabelProviderSet())
      return super.getLabelProvider();
    else
      return new ComboBoxLabelProvider(defaultRotations);
  }  
}
