/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 16.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.custom;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.nightlabs.editor2d.EditorPlugin;

public class EditorImages 
{
  public EditorImages() 
  {
    super();
  }

  private static ImageDescriptor createDescriptor(String filename) 
  {
    return ImageDescriptor.createFromFile(EditorPlugin.class, filename);
  }  

  public static final ImageDescriptor ROTATE_NW_16;
  public static final ImageDescriptor ROTATE_SW_16;
  public static final ImageDescriptor ROTATE_NE_16;
  public static final ImageDescriptor ROTATE_SE_16;
  public static final ImageDescriptor SHEAR_HORIZONTAL_16;  
  public static final ImageDescriptor SHEAR_VERTICAL_16;
  public static final ImageDescriptor ZOOM_ALL_16;
  public static final ImageDescriptor ZOOM_SELECTION_16;
     
  static 
  {
    ROTATE_NW_16 = createDescriptor("icons/rotateNW16.gif"); //$NON-NLS-1$
    ROTATE_NE_16 = createDescriptor("icons/rotateNE16.gif"); //$NON-NLS-2$
    ROTATE_SE_16 = createDescriptor("icons/rotateSE16.gif"); //$NON-NLS-3$
    ROTATE_SW_16 = createDescriptor("icons/rotateSW16.gif"); //$NON-NLS-4$
    SHEAR_HORIZONTAL_16 = createDescriptor("icons/shearHorizontal16.gif"); //$NON-NLS-5$
    SHEAR_VERTICAL_16 = createDescriptor("icons/shearVertical16.gif"); //$NON-NLS-6$
    ZOOM_ALL_16 = createDescriptor("icons/zoom16.gif"); //$NON-NLS-7$
    ZOOM_SELECTION_16 = createDescriptor("icons/zoom16.gif"); //$NON-NLS-7$
  }
  
  /**
   * Returns the RotationImage or ShearImage corresponding to the given direction, defined in 
   * {@link PositionConstants}.
   * @param direction The relative direction of the desired Rotate/Shear Image
   * @return The appropriate directional Rotate/Shear Image
   */
  public static Image getDirectionalRotationImage(int direction) 
  {
    switch (direction) 
    {
      case PositionConstants.NORTH :
        return SHEAR_HORIZONTAL_16.createImage();
      case PositionConstants.SOUTH:
        return SHEAR_HORIZONTAL_16.createImage();
      case PositionConstants.EAST :
        return SHEAR_VERTICAL_16.createImage();
      case PositionConstants.WEST:
        return SHEAR_VERTICAL_16.createImage();
      case PositionConstants.SOUTH_EAST:
        return ROTATE_SE_16.createImage();
      case PositionConstants.SOUTH_WEST:
        return ROTATE_SW_16.createImage();
      case PositionConstants.NORTH_EAST:
        return ROTATE_NE_16.createImage();
      case PositionConstants.NORTH_WEST:
        return ROTATE_NW_16.createImage();
      default:
        break;
    }
    return null;
  }  
}
