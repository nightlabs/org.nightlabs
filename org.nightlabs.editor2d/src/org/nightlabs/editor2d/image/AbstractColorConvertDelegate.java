/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2006 NightLabs - http://NightLabs.org                    *
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

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.RenderedImage;

import javax.media.jai.PlanarImage;

import org.nightlabs.editor2d.ImageDrawComponent;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractColorConvertDelegate
implements ImageRendererDelegate
{
	/**
	 * returns the RenderMode for the colorConversion
	 * @return the RenderMode for the colorConversion
	 */
	public abstract String getRenderMode();
		
	/**
	 * returns a {@link BufferedImageOp} for the given {@link RenderModeMetaData} which performs
	 * the colorConversion with the given paramters contained in the metaData
	 * 
	 * @param metaData the {@link RenderModeMetaData} for the colorConversion
	 * @return the {@link BufferedImageOp} for the colorConversion
	 */
	protected abstract BufferedImageOp getBufferedImageOp(RenderModeMetaData metaData);
	
	private RenderingHints renderHints = null;
	protected RenderingHints getRenderingHints(RenderModeMetaData renderModeMetaData)
	{
		if (renderHints == null) {
			renderHints = initializeRenderingHints(renderModeMetaData);
		}
		return renderHints;
	}
	
	protected RenderingHints initializeRenderingHints(RenderModeMetaData renderModeMetaData)
	{
		return new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
	}
	
	public RenderedImage render(String renderMode,
			ImageDrawComponent imageDrawComponent, RenderedImage image,
			RenderModeMetaData renderModeMetaData)
	{
		if (image == null)
			image = imageDrawComponent.getOriginalImage();

		if (renderMode.equals(getRenderMode())) {
			return convertColor(image, renderModeMetaData);
		}
		
		return image;
	}

	protected RenderedImage convertColor(RenderedImage img, RenderModeMetaData metaData)
	{
		BufferedImage bi = null;
		if (img instanceof BufferedImage)
			bi = (BufferedImage) img;
		else if (img instanceof PlanarImage)
		{
			PlanarImage pi = (PlanarImage) img;
			bi = pi.getAsBufferedImage();
		}
		return getBufferedImageOp(metaData).filter(bi, null);
	}
	
}
