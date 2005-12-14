/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 14.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.properties;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class LineStyleCellEditor 
extends DialogCellEditor 
{
  public LineStyleCellEditor() {
    super();
  }

  public LineStyleCellEditor(Composite parent) {
    super(parent);
  }

  public LineStyleCellEditor(Composite parent, int style) {
    super(parent, style);
  }

  protected Object openDialogBox(Control cellEditorWindow) 
  {
    LineStyleDialog dialog = new LineStyleDialog(cellEditorWindow.getShell());
    dialog.open();
    Object[] result = dialog.getResult(); 
    if (result != null)
      return result[0];  
    
    return new Integer(1);
  }

  protected void updateContents(Object value) 
  {
    super.updateContents(value);
  }
  
  
}
