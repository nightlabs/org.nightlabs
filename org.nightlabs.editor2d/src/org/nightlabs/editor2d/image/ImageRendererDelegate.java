/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
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
package org.nightlabs.editor2d.image;

import java.awt.image.RenderedImage;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.ImageDrawComponent;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public interface ImageRendererDelegate
{
 	/**
	 * @param renderMode the renderMode to which the given image should be converted for rendering
	 * @param imageDrawComponent the {@link ImageDrawComponent} to render, may not be null
	 * @param image Is usually identical to {@link ImageDrawComponent#getOriginalImage()}, but might
	 *		be different for preview or other purposes. You must use this image as source and generate your
	 *		manipulated image from this.
	 * @param renderModeMetaData the renderModeMetaData to use for rendering the given image
	 * @return a {@link RenderedImage} for the corresponding renderMode
	 * @see DrawComponent#setRenderMode(String)
	 */
	RenderedImage render(
			String renderMode,
			ImageDrawComponent imageDrawComponent,
			RenderedImage image,
			RenderModeMetaData renderModeMetaData);
//	throws ImageRenderException;
}
