/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.editor2d.custom;

import org.eclipse.draw2d.Cursors;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;

import org.nightlabs.editor2d.EditorPlugin;

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
