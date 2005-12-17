/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 18.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.properties;

import org.eclipse.swt.widgets.Composite;

import org.nightlabs.base.property.ComboBoxCellEditor;

public class RotationCellEditor 
extends ComboBoxCellEditor 
{
  public RotationCellEditor(Composite parent, String[] items, int style) {
    super(parent, items, style);
  }

  /**
   * The <code>ComboBoxCellEditor</code> implementation of
   * this <code>CellEditor</code> framework method returns
   * the String of the Selection
   *
   * @return the zero-based index of the current selection wrapped
   *  as an <code>Integer</code>
   */
  protected Object doGetValue() 
  {
    if (comboBox.getText().equals(""))
      return oldValue;
    try {
      Double d = new Double(comboBox.getText());
    } catch (NumberFormatException e) {
      return oldValue;
    }
    return new Double(comboBox.getText());
//    return new Double(items[comboBox.getSelectionIndex()]);
  }    
}
