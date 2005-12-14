/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 18.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.properties;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class DoubleCellEditor 
extends CellEditor 
{
  protected Text text;
  
  public DoubleCellEditor() {
    super();
  }

  public DoubleCellEditor(Composite parent) {
    super(parent);
  }

  public DoubleCellEditor(Composite parent, int style) {
    super(parent, style);
  }

  protected Control createControl(Composite parent) {
    text = new Text(parent, getStyle());
    return text;
  }

  protected Object doGetValue() {
    String stringVal = text.getText();
    Double d = new Double(stringVal);
    return d; 
  }

  protected void doSetFocus() {
    if (text != null) {
      text.selectAll();
      text.setFocus();
    }     
  }

  protected void doSetValue(Object value) {
    Assert.isTrue(text != null && (value instanceof Double));
    Double val = (Double) value;
    String stringVal = Double.toString(val.doubleValue());
    text.setText(stringVal);        
  }

}
