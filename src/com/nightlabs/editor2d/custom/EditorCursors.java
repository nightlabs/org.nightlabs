/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 16.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.custom;

import org.eclipse.draw2d.Cursors;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;

import com.nightlabs.editor2d.EditorPlugin;

public class EditorCursors 
extends Cursors 
{

//  public static Cursor ROTATE = new Cursor(null, new ImageData(EditorPlugin.getResourceString("icon.rotate.nw")), 8, 8); 
  public static final Cursor ROTATE;
  public static final Cursor SHEAR_HORIZONTAL;
  public static final Cursor SHEAR_VERTICAL;
  
  public EditorCursors() {
    super();
  }
  
  private static ImageDescriptor createDescriptor(String filename) {
    return ImageDescriptor.createFromFile(EditorPlugin.class, filename);
  }   
  
  private static Cursor createCursor(String fileName) 
  {
    ImageDescriptor desc = createDescriptor(fileName);
    Image image = desc.createImage();
    return new Cursor(null, desc.getImageData(), image.getBounds().x, image.getBounds().y);
  }
  
  static 
  {
    ROTATE = createCursor("icons/cursorRotate16.gif"); //$NON-NLS-1$
    SHEAR_HORIZONTAL = createCursor("icons/shearHorizontal16.gif"); //$NON-NLS-2$
    SHEAR_VERTICAL = createCursor("icons/shearVertical16.gif"); //$NON-NLS-2$
  }  
}
