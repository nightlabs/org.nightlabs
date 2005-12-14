/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 18.01.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.tools;

import org.eclipse.gef.Tool;
import org.eclipse.gef.palette.PanningSelectionToolEntry;


public class EditorSelectionToolEntry 
extends PanningSelectionToolEntry 
{

  /**
   * 
   */
  public EditorSelectionToolEntry() {
    super();
  }

  /**
   * @param label
   */
  public EditorSelectionToolEntry(String label) {
    super(label);

  }

  /**
   * @param label
   * @param shortDesc
   */
  public EditorSelectionToolEntry(String label, String shortDesc) {
    super(label, shortDesc);
  }

  
  public Tool createTool() {
    return new EditorSelectionTool();
  }
}
