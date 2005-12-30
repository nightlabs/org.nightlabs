/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

package org.nightlabs.editor2d.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public class CheckboxCellEditor  
extends CellEditor
{
  protected Button checkbox;
	
  /**
   * 
   */
  public CheckboxCellEditor() {
    super();
  }

  /**
   * @param parent
   */
  public CheckboxCellEditor(Composite parent) {
    super(parent);
  }

  /**
   * @param parent
   * @param style
   */
  public CheckboxCellEditor(Composite parent, int style) {
    super(parent, style);
  }
  
	/**
	 * The <code>CheckboxCellEditor</code> implementation of
	 * this <code>CellEditor</code> framework method does
	 * nothing and returns <code>null</code>.
	 */
	protected Control createControl(Composite parent) 
	{
	   checkbox = new Button(parent, SWT.CHECK);
	   return checkbox;
	}
	
	/**
	 * The <code>CheckboxCellEditor</code> implementation of
	 * this <code>CellEditor</code> framework method returns
	 * the checkbox setting wrapped as a <code>Boolean</code>.
	 *
	 * @return the Boolean checkbox value
	 */
	protected Object doGetValue() {
		return new Boolean(checkbox.getSelection());
	}
	
	/* (non-Javadoc)
	 * Method declared on CellEditor.
	 */
	protected void doSetFocus() 
	{
	  checkbox.setFocus();
	}
	
	/**
	 * The <code>CheckboxCellEditor</code> implementation of
	 * this <code>CellEditor</code> framework method accepts
	 * a value wrapped as a <code>Boolean</code>.
	 *
	 * @param value a Boolean value
	 */
	protected void doSetValue(Object value) 
	{
	  if (value instanceof Boolean)
	    checkbox.setSelection(((Boolean) value).booleanValue());
	}
	
}
