/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 03.11.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.properties;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;


public class IntCellEditor 
extends CellEditor 
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
  	Assert.isTrue(text != null && (value instanceof Integer));
  	Integer val = (Integer) value;
  	String stringVal = Integer.toString(val.intValue());
  	text.setText(stringVal);    
  }

}
