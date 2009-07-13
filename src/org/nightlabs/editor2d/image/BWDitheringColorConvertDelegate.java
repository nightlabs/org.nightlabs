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
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.Map;

import javax.media.jai.ColorCube;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.LookupTableJAI;

import org.apache.log4j.Logger;
import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.render.RenderConstants;
import org.nightlabs.editor2d.util.ImageUtil;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class BWDitheringColorConvertDelegate
implements ImageRendererDelegate
{
	public static final String KEY_DITHER_MODE = "ditherMode";
	public static final String VALUE_DITHER_MODE_QUALITY = "errordiffusion";
	public static final String VALUE_DITHER_MODE_SPEDD = "ordereddither";
	
	public static final String KEY_DITHER_ALGORITHM = "ditherAlgorithm";
	public static final String DITHER_ALGORITHM_FLOYD_STEINBERG = "FloydSteinberg";
	public static final String DITHER_ALGORITHM_JARVIS = "Jarvis";
	public static final String DITHER_ALGORITHM_STUCKI = "Stucki";
//	public static final String DITHER_ALGORITHM_441 = "441";
	
	/**
	* LOG4J logger used by this class
	*/
	private static final Logger logger = Logger.getLogger(BlackWhiteColorConvertDelegate.class);
	
	/**
	* @see org.nightlabs.editor2d.image.ImageRendererDelegate#render(java.lang.String, org.nightlabs.editor2d.ImageDrawComponent, java.awt.image.RenderedImage, org.nightlabs.editor2d.image.RenderModeMetaData)
	*/
	public RenderedImage render(String renderMode,
			ImageDrawComponent imageDrawComponent, RenderedImage image,
			RenderModeMetaData renderModeMetaData)
	{
		if (image == null)
			image = imageDrawComponent.getOriginalImage();
	
		if (renderMode.equals(RenderConstants.BLACK_WHITE_MODE))
			return ditherTo1Bit(image, renderModeMetaData.getParameters());
		
		return image;
	}
	
	private LookupTableJAI lookupTable = null;
	private ColorCube colorCube = null;
	private ImageLayout imagelayout = null;
	protected void initDithering(RenderedImage image, int bitsPerPixel)
	{
		lookupTable = ImageUtil.getLookupTable(bitsPerPixel);
		colorCube = ImageUtil.getColorCube(bitsPerPixel);
		imagelayout = ImageUtil.get1BitDitherImageLayout(image.getWidth(), image.getHeight());
	}
		
	protected RenderedImage ditherTo1Bit(RenderedImage src, Map<String, Object> parameters)
	{
		initDithering(src, src.getColorModel().getPixelSize());
		
		String ditherMode = (String) parameters.get(KEY_DITHER_MODE);
		String ditherAlgorithm = (String) parameters.get(KEY_DITHER_ALGORITHM);
    ParameterBlock pb = new ParameterBlock();
    pb.addSource(src);
    String opName = null;
    if (ditherMode.equals(VALUE_DITHER_MODE_QUALITY))
    {
        opName = VALUE_DITHER_MODE_QUALITY;
        LookupTableJAI lut = lookupTable;
        pb.add(lut);
        pb.add(getKernelJAI(ditherAlgorithm));
    } else if (ditherMode.equals(VALUE_DITHER_MODE_SPEDD)){
        opName = VALUE_DITHER_MODE_SPEDD;
        ColorCube cube = colorCube;
        pb.add(cube);
        pb.add(KernelJAI.DITHER_MASK_441);
    }
    // Create a hint containing the layout.
    RenderingHints hints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT,
                                              imagelayout);
    try {
			return JAI.create(opName, pb, hints);
		} catch (Exception e) {
			return convertBlackWhiteFallback(src);
		}
	}
	
	protected BufferedImage convertBlackWhiteFallback(RenderedImage src)
	{
		logger.debug("ditherTo1Bit with JAI failed, so fallback BlackWhite conversion performed!");
		BufferedImage bi = ImageUtil.convertToBufferedImage(src);
		return ImageUtil.cloneImage(bi, BufferedImage.TYPE_BYTE_BINARY);
	}

	protected KernelJAI getKernelJAI(String ditherAlgorithm)
	{
		if (ditherAlgorithm.equals(DITHER_ALGORITHM_FLOYD_STEINBERG))
			return KernelJAI.ERROR_FILTER_FLOYD_STEINBERG;
		if (ditherAlgorithm.equals(DITHER_ALGORITHM_JARVIS))
			return KernelJAI.ERROR_FILTER_JARVIS;
		if (ditherAlgorithm.equals(DITHER_ALGORITHM_STUCKI))
			return KernelJAI.ERROR_FILTER_STUCKI;
		
		return null;
	}
}
