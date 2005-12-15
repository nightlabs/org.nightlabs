/**
 * <p> Project: org.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 24.01.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
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
