/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 14.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

import com.nightlabs.editor2d.EditorPlugin;

public class LineStyleDialog 
extends ListDialog
//extends org.eclipse.jface.dialogs.Dialog
//extends Dialog
{

  public LineStyleDialog(Shell parent) 
  {
    super(parent);
    setTitle(EditorPlugin.getResourceString("dialog.lineStyle.title"));
    setMessage(EditorPlugin.getResourceString("dialog.lineStyle.message"));
    setContentProvider(contentProvider);
    setLabelProvider(new LineStyleLabelProvider());
    setInput(null);
  }
    
  protected IStructuredContentProvider contentProvider = new IStructuredContentProvider() 
  {  
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) 
    {
      
    }
  
    public void dispose() {
    }
  
    public Object[] getElements(Object inputElement) 
    {
      List lineStyles = new ArrayList();
      lineStyles.add(new Integer(SWT.LINE_SOLID));
      lineStyles.add(new Integer(SWT.LINE_DOT));
      lineStyles.add(new Integer(SWT.LINE_DASH));
      lineStyles.add(new Integer(SWT.LINE_DASHDOT));
      lineStyles.add(new Integer(SWT.LINE_DASHDOTDOT));
      return lineStyles.toArray();
    }
  }; 
  
}
