/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 29.12.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.request;

import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;

/**
 * The set of constants used to identify <code>Requests</code> by their {@link
 * Request#getType() type}. Applications can extend this set of constants with their own.
 */
public interface EditorRequestConstants 
extends RequestConstants
{
  /**
   * Indicates the creation of a new Shape. 
   */
  String REQ_CREATE_SHAPE  = "create Shape";//$NON-NLS-1$

  /**
   * Indicates the Editing of a Shape. 
   */  
  String REQ_EDIT_SHAPE = "edit Shape";//$NON-NLS-2$

  /**
   * Indicates the Zooming of the View (e.g. with a ZoomTool) 
   */    
  String REQ_ZOOM_RECT = "zoom Rectangle";//$NON-NLS-3$
  
  /**
   * Indicates the Rotation of a DrawComponent
   */
  String REQ_ROTATE = "rotate";//$NON-NLS-4$
  
  /**
   * Indicates the Editing of the Rotation Center
   */  
  String REQ_EDIT_ROTATE_CENTER = "edit Rotation Center";//$NON-NLS-5$
  
  /**
   * Indicates the Shearing of a DrawComponent
   */
  String REQ_SHEAR = "shear";
  
//  /*
//   * Indicates the creation of a Text
//   */
//  String REQ_CREATE_TEXT = "create Text";//$NON-NLS-5$
}
