/**
 * <p> Project: org.nightlabs.gui </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 15.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.rcp.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class ComboBoxPropertyDescriptor 
extends PropertyDescriptor
{
  /**
   * The list of possible values to display in the combo box
   */
  protected String[] values;
  
  public ComboBoxPropertyDescriptor(Object id, String displayName, String[] valuesArray) 
  {
    super(id, displayName);
    values = valuesArray;
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
    CellEditor editor = new ComboBoxCellEditor(parent, values, SWT.READ_ONLY);
    if (getValidator() != null)
      editor.setValidator(getValidator());
    return editor;
  }  

  /**
   * The <code>ComboBoxPropertyDescriptor</code> implementation of this 
   * <code>IPropertyDescriptor</code> method returns the value set by
   * the <code>setProvider</code> method or, if no value has been set
   * it returns a <code>ComboBoxLabelProvider</code> created from the 
   * valuesArray of this <code>ComboBoxPropertyDescriptor</code>.
   *
   * @see #setLabelProvider
   */
  public ILabelProvider getLabelProvider() 
  {
    if (isLabelProviderSet())
      return super.getLabelProvider();
    else
      return new ComboBoxLabelProvider(values);
  }  
}
