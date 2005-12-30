/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.base.property;

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
