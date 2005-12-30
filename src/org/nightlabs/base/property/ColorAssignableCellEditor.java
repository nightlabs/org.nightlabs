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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

import org.nightlabs.base.util.ImageUtil;
import org.nightlabs.util.IAssignable;
import org.nightlabs.util.IColorAssignable;

public class ColorAssignableCellEditor 
extends AbstractColorComboBoxCellEditor
{
	protected Collection assignables;
	public ColorAssignableCellEditor(Collection colorAssignables, Composite parent, int style) {
		super(parent, style);
		this.assignables = colorAssignables;
		populateComboBoxItems();
	}
	
	protected Map index2Assignable = null;
	protected TableItem items[] = null;
	protected TableItem[] getItems() {
		return items;
	}	
  protected void populateComboBoxItems() 
  {
  	index2Assignable = new HashMap();
  	Map id2Assignable = AssignablePropertyDescriptor.createID2Assignables(assignables);
  	
  	items = new TableItem[assignables.size()];
    if (getComboBox() != null && assignables != null) 
    {
    	getComboBox().removeAll();      
      int index = 0;
//      Table table = getComboBox().getTable();
      for (Iterator it = id2Assignable.keySet().iterator(); it.hasNext(); ) 
      {
  			Object key = it.next();
  			Object o = id2Assignable.get(key);
  			if (o instanceof IColorAssignable) {
  				IColorAssignable assignable = (IColorAssignable) o;
  				Image colorImage = ImageUtil.createColorImage(assignable.getColor());
  				getComboBox().add(colorImage, assignable.getName(), index);
//  				items[index] = assignable.getName();
  				index2Assignable.put(new Integer(index), assignable);
  				index++;
  			}
  		}
      setValueValid(true);
      selection = 0;
    }
  }	

	protected Object getReturnValue() 
	{
		return new Integer(getID(getComboBox().getSelectionIndex()));
	}

  protected int getID(int selectionIndex) 
  {
  	IAssignable assignable = (IAssignable) index2Assignable.get(new Integer(selectionIndex));
  	if (assignable != null)
  		return assignable.getId();
  	else
  		return 0;
  }	
}
