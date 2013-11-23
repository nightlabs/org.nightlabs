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

package org.nightlabs.editor2d.util;

import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;

import javax.imageio.ImageIO;
import javax.media.jai.ColorCube;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PlanarImage;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ImageUtil
{
	private static final Logger logger = Logger.getLogger(ImageUtil.class);

	private ImageUtil() { }

	//	private static BufferedImage bi = null;
	/**
	 * returns the {@link ColorModel} for the given imageType
	 *
	 * @param imageType the imageType {@link BufferedImage}
	 * @return the {@link ColorModel} for the appropriate imageType
	 * @see BufferedImage
	 */
	public static ColorModel getColorModel(int imageType)
	{
		BufferedImage bi = new BufferedImage(1, 1, imageType);
		return bi.getColorModel();
	}

	/**
	 * clones a {@link BufferedImage}
	 *
	 * @param bufferedImage the BufferedImage to clone
	 * @return a cloned {@link BufferedImage}
	 */
	public static BufferedImage cloneImage(BufferedImage bufferedImage)
	{
		if (bufferedImage == null)
			throw new IllegalArgumentException("bufferedImage must not be null!");

		BufferedImage clone = new BufferedImage(bufferedImage.getColorModel(),
				bufferedImage.getData().createCompatibleWritableRaster(), bufferedImage.isAlphaPremultiplied(), null);
		clone.setData(bufferedImage.copyData(clone.getRaster()));
		return clone;
	}

	/**
	 * clones a {@link BufferedImage} with the given imageType
	 *
	 * @param bufferedImage the BufferedImage to clone
	 * @param imageType the imageType of the cloned BufferedImage
	 * @return a cloned BufferedImage with the given imageType
	 */
	public static BufferedImage cloneImage(BufferedImage bufferedImage, int imageType)
	{
		if (bufferedImage == null)
			throw new IllegalArgumentException("bufferedImage must not be null!");

		BufferedImage b = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), imageType);
		Graphics g = b.createGraphics();
		g.drawImage(bufferedImage, 0, 0, null);
		return b;
	}

	//	/**
	//	* clones a {@link BufferedImage} with the given imageType
	//	*
	//	* @param bi the BufferedImage to clone
	//	* @param imageType the imageType of the cloned BufferedImage
	//	* @return a cloned BufferedImage with the given imageType
	//	*/
	//	public static BufferedImage cloneImage(BufferedImage bi, int imageType)
	//	{
	//		BufferedImage clone = new BufferedImage(bi.getWidth(), bi.getHeight(), imageType);
	//		clone.setData(bi.copyData(clone.getRaster()));
	//		return clone;
	//	}

	//	public static BufferedImage cloneImage(BufferedImage src)
	//	{
	//		BufferedImage clone = src.getSubimage(0, 0, src.getWidth(), src.getHeight());
	//		WritableRaster cloneRaster = null;
	//		clone.setData(src.copyData(clone.getRaster()));
	//		return clone;
	//	}

	//	/**
	//	 * clones a {@link BufferedImage}
	//	 *
	//	 * @param bi the BufferedImage to clone
	//	 * @return a cloned {@link BufferedImage}
	//	 */
	//	public static BufferedImage cloneImage(BufferedImage bi)
	//	{
	//		if (bi.getType() != BufferedImage.TYPE_CUSTOM)
	//			return cloneImage(bi, bi.getType());
	//		else
	//			return cloneImage(bi, BufferedImage.TYPE_INT_ARGB);
	//	}
	//
	//  public static BufferedImage cloneImage(BufferedImage in, int type)
	//  {
	//		if(in.getType() == type)
	//		    return in;
	//		int w = in.getWidth();
	//		int h = in.getHeight();
	//		BufferedImage out = new BufferedImage(w, h, type);
	//		for(int y = 0; y < h; y++) {
	//			for(int x = 0; x < w; x++)
	//				out.setRGB(x, y, in.getRGB(x, y));
	//		}
	//		return out;
	//  }

	//  public static BufferedImage cloneImage(BufferedImage in)
	//  {
	//    int type = in.getType();
	//    int w = in.getWidth();
	//    int h = in.getHeight();
	//    BufferedImage out = new BufferedImage(w, h, type);
	//    for(int y = 0; y < h; y++) {
	//      for(int x = 0; x < w; x++)
	//      	out.setRGB(x, y, in.getRGB(x, y));
	//    }
	//    return out;
	//  }

	/**
	 * logs all available file formats of the {@link ImageIO}
	 * @param level the log level the {@link Logger}
	 * @see Level
	 */
	public static void logAvailableFileFormats(Level level)
	{
		// log all available ImageFormats
		logger.log(level, "ImageIO.getReaderFormatNames() = ");
		String[] readerNames = ImageIO.getReaderFormatNames();
		for (int i=0; i<readerNames.length; i++) {
			logger.log(level, "FormatName "+i+" = "+readerNames[i]);
		}

		logger.log(level, "ImageIO.getWriterFormatNames() = ");
		String[] writerNames = ImageIO.getWriterFormatNames();
		for (int i=0; i<writerNames.length; i++) {
			logger.log(level, "FormatName "+i+" = "+writerNames[i]);
		}
	}

//	protected static AffineTransform transform;
//	protected static AffineTransformOp op;

	public static BufferedImage rotate(BufferedImage image, double radians, double x, double y)
	{
		AffineTransform transform;
		AffineTransformOp op;

		transform = new AffineTransform();
		transform.rotate(radians,x,y);
		op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
		return op.filter(image,null);
	}

	public static BufferedImage rotate(BufferedImage image, double radians, double x, double y, int renderingHint)
	{
		AffineTransform transform;
		AffineTransformOp op;

		transform = new AffineTransform();
		transform.rotate(radians,x,y);
		op = new AffineTransformOp(transform, renderingHint);
		return op.filter(image,null);
	}

	public static BufferedImage scale(BufferedImage image, double scaleX, double scaleY)
	{
		AffineTransform transform;
		AffineTransformOp op;

		transform = new AffineTransform();
		transform.scale(scaleX, scaleY);
		op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
		return op.filter(image,null);
	}

	public static BufferedImage scale(BufferedImage image, double scaleX, double scaleY, int renderingHint)
	{
		AffineTransform transform;
		AffineTransformOp op;

		transform = new AffineTransform();
		transform.scale(scaleX, scaleY);
		op = new AffineTransformOp(transform, renderingHint);
		return op.filter(image,null);
	}

	public static final String DITHER_MODE_ERROR_DIFFUSION = "errordiffusion";
	public static final String DITHER_MODE_ORDERED_DITHER = "ordereddither";

	/**
	 * dithers a {@link PlanarImage} tp 1 Bit Black White
	 *
	 * @param src the {@link PlanarImage} to dither
	 * @param ditherMode the ditherMode to use either DITHER_MODE_ERROR_DIFFUSION or DITHER_MODE_ORDERED_DITHER
	 * @param ditherAlgorithm the ditherAlgorithm to use
	 * either {@link KernelJAI#ERROR_FILTER_FLOYD_STEINBERG}, {@link KernelJAI#ERROR_FILTER_JARVIS} or {@link KernelJAI#ERROR_FILTER_STUCKI}
	 * @return the dithered PlanarImage
	 */
	public static PlanarImage ditherTo1Bit(PlanarImage src, String ditherMode, KernelJAI ditherAlgorithm)
	{
		// Load the ParameterBlock for the dithering operation
		// and set the operation name.
		int bitsPerPixel = src.getColorModel().getPixelSize();
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(src);
		String opName = null;
		if (ditherMode.equals(DITHER_MODE_ERROR_DIFFUSION))
		{
			opName = DITHER_MODE_ERROR_DIFFUSION;
			LookupTableJAI lut = getLookupTable(bitsPerPixel);
			pb.add(lut);
			pb.add(ditherAlgorithm);
		} else if (ditherMode.equals(DITHER_MODE_ORDERED_DITHER)){
			opName = DITHER_MODE_ORDERED_DITHER;
			ColorCube cube = getColorCube(bitsPerPixel);
			pb.add(cube);
			pb.add(KernelJAI.DITHER_MASK_441);
		}
		// Create a layout containing an IndexColorModel which maps
		// zero to zero and unity to 255; force SampleModel to be bilevel.
		ImageLayout layout = new ImageLayout();
		byte[] map = new byte[] {(byte)0x00, (byte)0xff};
		ColorModel cm = new IndexColorModel(1, 2, map, map, map);
		layout.setColorModel(cm);
		SampleModel sm = new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE,
				src.getWidth(),
				src.getHeight(),
				1);
		layout.setSampleModel(sm);
		// Create a hint containing the layout.
		RenderingHints hints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT,
				layout);
		// Dither the image.
		PlanarImage dst = JAI.create(opName, pb, hints);
		return dst;
	}

	/**
	 * returns a LookupTableJAI for dithering a image with the given bits per pixel
	 * to 1 Bit with the operation "errordiffusion"
	 *
	 * only 32, 24, 8 or 1 Bit per pixel are supported
	 *
	 * @param bitsPerPixel the bits per pixel
	 * @return the LookupTableJAI for the given bits per pixel
	 */
	public static LookupTableJAI getLookupTable(int bitsPerPixel)
	{
		LookupTableJAI lut = null;
		if (bitsPerPixel == 24) {
			lut = new LookupTableJAI(new byte[][] { {(byte)0x00, (byte)0xff},
					{(byte)0x00, (byte)0xff},
					{(byte)0x00, (byte)0xff}});
		}
		else if (bitsPerPixel == 32) {
			lut = new LookupTableJAI(new byte[][] { {(byte)0x00, (byte)0xff},
					{(byte)0x00, (byte)0xff},
					{(byte)0x00, (byte)0xff},
					{(byte)0x00, (byte)0xff}});
		}
		else if (bitsPerPixel == 8) {
			lut = new LookupTableJAI(new byte[] {(byte)0x00, (byte)0xff});
		}
		else if (bitsPerPixel == 1) {
			lut = new LookupTableJAI(new byte[] {(byte)0x00, (byte)0xff});
		}
		else {
			throw new IllegalArgumentException("unsupported amount of bitsPerPixel "+bitsPerPixel+", only 32, 24 or 8 are supported!");
		}
		return lut;
	}

	/**
	 * returns a {@link ColorCube} for dithering a image with the given bits per pixel
	 * to 1 Bit with the operation "ordereddither"
	 *
	 * only 32, 24, 8 or 1 Bit per pixel are supported
	 *
	 * @param bitsPerPixel the bits per pixel
	 * @return the ColorCube for the given bits per pixel
	 */
	public static ColorCube getColorCube(int bitsPerPixel)
	{
		ColorCube cube = null;
		if (bitsPerPixel == 24) {
			cube = ColorCube.createColorCube(DataBuffer.TYPE_BYTE, 0, new int[] {2, 2, 2});
		}
		else if (bitsPerPixel == 32) {
			cube = ColorCube.createColorCube(DataBuffer.TYPE_BYTE, 0, new int[] {2, 2, 2, 2});
		}
		else if (bitsPerPixel == 8) {
			cube = ColorCube.createColorCube(DataBuffer.TYPE_BYTE, 0, new int[] {2});
		}
		else if (bitsPerPixel == 1) {
			cube = ColorCube.createColorCube(DataBuffer.TYPE_BYTE, 0, new int[] {2});
		}
		else {
			throw new IllegalArgumentException("unsupported amount of bitsPerPixel "+bitsPerPixel+", only 32, 24 or 8 are supported!");
		}
		return cube;
	}

	/**
	 * returns a ImageLayout necessary for 1Bit Dithering
	 *
	 * @param width the width of the image
	 * @param height the height of the image
	 * @return the {@link ImageLayout} to set in the renderingHints
	 */
	public static ImageLayout get1BitDitherImageLayout(int width, int height)
	{
		ImageLayout layout = new ImageLayout();
		byte[] map = new byte[] {(byte)0x00, (byte)0xff};
		ColorModel cm = new IndexColorModel(1, 2, map, map, map);
		layout.setColorModel(cm);
		SampleModel sm = new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE,
				width,
				height,
				1);
		layout.setSampleModel(sm);
		return layout;
	}

	//	/**
	//	 * returns an appropriate ImageLayout for the given BufferedImage
	//	 * with the ColorModel of the BufferedImage and the SampleModel of the BufferedImage
	//	 *
	//	 * @param bi the BufferedImage to get the ImageLayout from
	//	 * @return an appropriate ImageLayout for the given BufferedImage
	//	 */
	//	public static ImageLayout getImageLayout(BufferedImage bi)
	//	{
	//    ImageLayout layout = new ImageLayout();
	//    layout.setColorModel(bi.getColorModel());
	//    layout.setSampleModel(bi.getSampleModel());
	//    return layout;
	//	}

	/**
	 * logs the class, type, and components of the given ColorSpace
	 *
	 * @param cs the ColorSpace to log
	 * @param l the Log Level
	 */
	public static void logColorSpace(ColorSpace cs, Level l)
	{
		logger.log(l, "ColorSpace Class = "+cs.getClass());
		logger.log(l, "ColorSpace Type = "+cs.getType());
		logger.log(l, "ColorSpace number of components = "+cs.getNumComponents());
		for (int i=0; i<cs.getNumComponents(); i++) {
			logger.log(l, "Name of component "+i+" = "+cs.getName(i));
		}
	}

	/**
	 * converts a {@link RenderedImage} to a {@link BufferedImage}
	 * if the input is already a {@link BufferedImage} its just casted and returned,
	 * if the input is a {@link PlanarImage}, {@link PlanarImage#getAsBufferedImage()} is returned
	 *
	 * @param img the {@link RenderedImage} to convert
	 * @return the converted BufferedImage or null if no conversion could be done
	 */
	public static BufferedImage convertToBufferedImage(RenderedImage img)
	{
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		} else if (img instanceof PlanarImage) {
			PlanarImage pi = (PlanarImage) img;
			BufferedImage bi = pi.getAsBufferedImage();
			pi.dispose();
			return bi;
		}
		return null;
	}

	public static final ColorModel COLOR_MODEL_BLACK_WHITE = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY),
			new int[] {2}, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);

	public static final ColorModel COLOR_MODEL_GRAY = ImageUtil.getColorModel(BufferedImage.TYPE_BYTE_GRAY);

	public static final ColorModel COLOR_MODEL_RGB = ImageUtil.getColorModel(BufferedImage.TYPE_INT_ARGB);
}
