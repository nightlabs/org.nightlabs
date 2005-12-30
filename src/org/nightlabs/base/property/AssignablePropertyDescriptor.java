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
import java.util.Iterator;
import java.util.TreeMap;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import org.nightlabs.util.IAssignable;

public class AssignablePropertyDescriptor 
extends PropertyDescriptor
{
	protected Collection assignables = null;
	
  /**
   * Creates a new property descriptor with the given id and display name for the given Collection
   * of assignables
   * @see IAssignable
   */
  public AssignablePropertyDescriptor(Object id, String displayName, Collection assignables) 
  {
  	super(id, displayName);
  	this.assignables = assignables;
  }
    
  /**
   * @return a <code>AssignableLabelProvider</code> 
   */
  public ILabelProvider getLabelProvider() 
  {
  	return new AssignableLabelProvider(assignables);
  }    
  
  /**
   * The <code>AssignablePropertyDescriptor</code> implementation of this 
   * <code>IPropertyDescriptor</code> method creates and returns a new
   * <code>AssignableCellEditor</code>.
   * <p>
   * The editor is configured with the current validator if there is one.
   * </p>
   */
  public CellEditor createPropertyEditor(Composite parent) 
  {
    CellEditor editor = new AssignableCellEditor(assignables, parent, SWT.READ_ONLY);
    if (getValidator() != null)
      editor.setValidator(getValidator());
    return editor;
  }  
  
  public static TreeMap createID2Assignables(Collection assignables) 
	{
  	TreeMap id2Assignable = new TreeMap();
		for (Iterator it = assignables.iterator(); it.hasNext(); ) {
			IAssignable assignable = (IAssignable) it.next();
			id2Assignable.put(new Integer(assignable.getId()), assignable);
		}
		return id2Assignable;
	}
}
