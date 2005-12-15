/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 17.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.request;

import java.util.List;

public interface EditorGroupRequest 
{
  /**
   * Returns a List containing the EditParts making this Request.
   *
   * @return A List containing the EditParts making this Request.
   */
  public List getEditParts();
  
  /**
   * Sets the EditParts making this Request to the given List.
   *
   * @param list The List of EditParts.
   */
  public void setEditParts(List list);
    
}
