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

package org.nightlabs.editor2d.edit.tree;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.resource.SharedImages;
import org.nightlabs.editor2d.AbstractPaletteFactory;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorPlugin;


public class LineTreeEditPart 
extends DrawComponentTreeEditPart 
{
//  public static Image LINE_ICON = ImageDescriptor.createFromFile(EditorPlugin.class, "icons/line16.gif").createImage();
	public static Image LINE_ICON = SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), 
			AbstractPaletteFactory.class, "Line").createImage();	
  
  /**
   * @param drawComponent
   */
  public LineTreeEditPart(DrawComponent drawComponent) {
    super(drawComponent);
  }

  /* (non-Javadoc)
   * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getImage()
   */
  protected Image getImage() {
    return LINE_ICON;
  }

//	protected void propertyChanged(PropertyChangeEvent evt) 
//	{
//		super.propertyChanged(evt);
//		String propertyName = evt.getPropertyName();
//		if (propertyName.equals(LineDrawComponent.PROP_CONNECT)) {
//			LOGGER.debug(propertyName +" changed!");
//			refreshVisuals();			
//		}
//	}  
}
