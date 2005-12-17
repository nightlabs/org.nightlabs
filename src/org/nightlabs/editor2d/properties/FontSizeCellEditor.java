/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 14.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.properties;


import org.eclipse.swt.widgets.Composite;

import org.nightlabs.base.property.ComboBoxCellEditor;
import org.nightlabs.util.FontUtil;

public class FontSizeCellEditor 
extends ComboBoxCellEditor 
{

  public FontSizeCellEditor(Composite parent) {
    super(parent, FontUtil.getFontSizes());
  }

//  protected Object doGetValue() 
//  {      	
//    return new Integer(items[comboBox.getSelectionIndex()]); 
//  }   
  protected Object doGetValue() 
  {      	
    return new Integer(items[comboBox.getSelectionIndex()]); 
  }    
  
  protected void doSetValue(Object value) 
  {
    String string = "";
    
    if (value instanceof String) {
      string = (String) value;
    }
    else if (value instanceof Integer) {
      string = ((Integer)value).toString();
    }
    
    for (int i=0; i<items.length; i++) {
      String s = items[i];
      if (s.equals(string)) {
        comboBox.select(i);
        break;
      }          
    }
    
  }
  
}
