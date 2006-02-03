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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;


public class IntCellEditor 
//extends CellEditor 
extends XCellEditor
{
  protected Text text;
  
  /**
   * @param parent
   */
  public IntCellEditor(Composite parent) {
    super(parent);
  }

  /**
   * @param parent
   * @param style
   */
  public IntCellEditor(Composite parent, int style) {
    super(parent, style);
  }

  /**
   * @param parent
   * @param style
   * @param readOnly
   */
  public IntCellEditor(Composite parent, int style, boolean readOnly) {
    super(parent, style, readOnly);
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.CellEditor#createControl(org.eclipse.swt.widgets.Composite)
   */
  protected Control createControl(Composite parent) {
  	text = new Text(parent, getStyle());
  	return text;
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.CellEditor#doGetValue()
   */
  protected Object doGetValue() 
  {
    String stringVal = text.getText();
    Integer i = new Integer(stringVal);
    return i;     
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.CellEditor#doSetFocus()
   */
  protected void doSetFocus() 
  {
  	if (text != null) {
  		text.selectAll();
  		text.setFocus();
  	}    
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.CellEditor#doSetValue(java.lang.Object)
   */
  protected void doSetValue(Object value) 
  {
  	super.doSetValue(value);
  	Assert.isTrue(text != null && (value instanceof Integer));
  	Integer val = (Integer) value;
  	String stringVal = Integer.toString(val.intValue());
  	text.setText(stringVal);    
  }

}
