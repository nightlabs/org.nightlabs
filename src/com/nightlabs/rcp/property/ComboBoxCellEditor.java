/**
 * <p> Project: com.nightlabs.gui </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 15.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.rcp.property;

import org.eclipse.jface.util.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;


public class ComboBoxCellEditor 
extends AbstractComboBoxCellEditor 
{

  /**
   * The list of items to present in the combo box.
   */
  protected String[] items;

  /**
   * the SWT style parameter for all contained Composites 
   */
  protected static final int style = SWT.NONE;
  
  public ComboBoxCellEditor(Composite parent, String[] items) 
  {
    this(parent, items, style);
  }

  public ComboBoxCellEditor(Composite parent, String[] items, int style) 
  {
    super(parent, style);
    setItems(items);
  }

  /**
   * Returns the list of choices for the combo box
   *
   * @return the list of choices for the combo box
   */
  public String[] getItems() {
    return this.items;
  }

  /**
   * Sets the list of choices for the combo box
   *
   * @param items the list of choices for the combo box
   */
  public void setItems(String[] items) 
  {
    Assert.isNotNull(items);
    this.items = items;
    populateComboBoxItems();
  }  
      
  /**
   * The <code>ComboBoxCellEditor</code> implementation of
   * this <code>CellEditor</code> framework method returns
   * the String of the Selection
   *
   * @return the String of the Selection
   */
  protected Object getReturnValue() 
  {
		return items[getComboBox().getSelectionIndex()];
  }   
//  protected Object doGetValue() 
//  {
//    return items[getComboBox().getSelectionIndex()]; 
//  }  
  
  /**
   * Updates the list of choices for the combo box for the current control.
   */
  protected void populateComboBoxItems() 
  {
    if (getComboBox() != null && items != null) {
    	getComboBox().removeAll();
      for (int i = 0; i < items.length; i++)
      	getComboBox().add(items[i], i);

      setValueValid(true);
      selection = 0;
    }
  }
 
}
