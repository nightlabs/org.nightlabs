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

package org.nightlabs.editor2d.impl;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;

import org.apache.log4j.Logger;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.image.BlackWhiteColorConvertDelegate;
import org.nightlabs.editor2d.image.GrayscaleColorConvertDelegate;
import org.nightlabs.editor2d.image.ImageInfo;
import org.nightlabs.editor2d.image.RGBColorConvertDelegate;
import org.nightlabs.editor2d.image.RenderModeConstants;
import org.nightlabs.editor2d.image.RenderModeMetaData;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.j2d.GeneralShapeFactory;
import org.nightlabs.editor2d.render.BaseRenderer;
import org.nightlabs.editor2d.render.RenderConstants;
import org.nightlabs.editor2d.render.Renderer;
import org.nightlabs.editor2d.render.j2d.J2DImageDefaultRenderer;
import org.nightlabs.editor2d.util.ImageUtil;
import org.nightlabs.i18n.unit.resolution.DPIResolutionUnit;
import org.nightlabs.i18n.unit.resolution.IResolutionUnit;
import org.nightlabs.i18n.unit.resolution.Resolution;
import org.nightlabs.i18n.unit.resolution.ResolutionImpl;
import org.nightlabs.io.DataBuffer;
import org.nightlabs.io.pcx.PCXImageReaderSPI;
import org.nightlabs.util.IOUtil;
import org.nightlabs.util.Util;

public class ImageDrawComponentImpl
extends DrawComponentImpl
implements ImageDrawComponent
{
	private static final long serialVersionUID = 1L;
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(ImageDrawComponentImpl.class);

	public ImageDrawComponentImpl()
	{
		super();
		initDefaultRenderModeMetaData();
		// check if pcx is already registered and if not add PCXImageWriterSPI to IIORegistry
		if (!ImageIO.getImageReadersByFormatName("pcx").hasNext()) {
			IIORegistry.getDefaultInstance().registerServiceProvider(new PCXImageReaderSPI());
		}
	}

	private transient BufferedImage image = null; // = ImageDrawComponent.IMAGE_DEFAULT;

	/**
	 * @see ImageDrawComponent#getImage()
	 */
	public BufferedImage getImage() {
		return image;
	}

	private transient byte[] originalImageData = null;
	/**
	 * @see ImageDrawComponent#getOriginalImageData()
	 */
	public byte[] getOriginalImageData() {
		return originalImageData;
	}

	private String originalImageFileName = null;
	/**
	 * @see ImageDrawComponent#getOriginalImageFileName()
	 */
	public String getOriginalImageFileName() {
		return originalImageFileName;
	}

	private long originalImageFileLastModified = 0;
	/**
	 * @see ImageDrawComponent#getOriginalImageFileLastModified()
	 */
	public long getOriginalImageFileLastModified() {
		return originalImageFileLastModified;
	}

	/**
	 * @see ImageDrawComponent#reloadImage(InputStream)
	 */
	public void reloadImage(InputStream in)
	{
		loadOriginalImage(in);
		if (originalImage != null) {
			primSetImage(originalImage, originalX, originalY);
			imageShape.transform(getAffineTransform());
		}
	}

	private int originalX;
	public int getOriginalX() {
		return originalX;
	}

	private int originalY;
	public int getOriginalY() {
		return originalY;
	}

	/**
	 * @see ImageDrawComponent#loadImage(String, long, InputStream)
	 */
	public void loadImage(
			String originalImageFileName,
			long originalImageFileLastModified,
			InputStream in)
	{
		if (originalImageFileName == null)
			throw new IllegalArgumentException("originalImageFileName must not be null");

		this.originalImageFileName = originalImageFileName;
		this.originalImageFileLastModified = originalImageFileLastModified;
		this.imageFileExtension = IOUtil.getFileExtension(originalImageFileName);
		this.setName(originalImageFileName);
		loadOriginalImage(in);
//		bitsPerPixel = originalImage.getColorModel().getPixelSize();
//		imageKey = generateImageKey(originalImageData);
		originalX = x;
		originalY = y;
		primSetImage(originalImage, originalX, originalY);
		firePropertyChange(PROP_IMAGE, null, image);
	}

	protected void loadOriginalImage(InputStream in)
	{
		if (in == null)
			throw new IllegalArgumentException("param in must not be null!");

		try {
			DataBuffer dataBuffer = new DataBuffer(10240);
			OutputStream out = dataBuffer.createOutputStream();
			try {
				IOUtil.transferStreamData(in, out);
			} finally {
				out.close();
			}
			this.originalImageData = dataBuffer.createByteArray();
			InputStream ins = dataBuffer.createInputStream();
			try {
				originalImage = ImageIO.read(ins);
				if (originalImage == null)
					throw new IllegalStateException("Could not load originalImageData. ImageID.read() returned null!");
				logger.debug("OriginalImage "+originalImageFileName+" loaded!");
			} finally {
				ins.close();
			}
			bitsPerPixel = originalImage.getColorModel().getPixelSize();
			imageKey = generateImageKey(originalImageData);
		} catch (IOException e) {
			this.originalImage = null;
			logger.debug("OriginalImage "+originalImageFileName+" load FAILED!");
			throw new RuntimeException(e);
		}
	}

	private byte[] imageKey = null;

	@Override
	public byte[] getImageKey() {
		return imageKey;
	}

	protected byte[] generateImageKey(byte[] imageData)
	{
		try {
			return Util.hash(imageData, Util.HASH_ALGORITHM_MD5);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private int bitsPerPixel = -1;

	@Override
	public int getBitsPerPixel() {
		return bitsPerPixel;
	}

	protected void determineImageResolution()
	{
		if (originalImageData != null) {
			try {
				ByteArrayInputStream bis = new ByteArrayInputStream(originalImageData);
				ImageInfo imageInfo = new ImageInfo();
				imageInfo.setInput(bis);
				imageInfo.setDetermineImageNumber(true);
				imageInfo.setCollectComments(true);
				boolean check = imageInfo.check();
				bis.close();
				if (check)
				{
					int dpiHeight = imageInfo.getPhysicalHeightDpi();
					int dpiWidth = imageInfo.getPhysicalWidthDpi();

					Resolution resolution = new ResolutionImpl();
					resolution.setResolutionUnit(new DPIResolutionUnit());

					if (dpiWidth != -1)
						resolution.setResolutionX(dpiWidth);
					else
						resolution.setResolutionX(DEFAULT_IMAGE_RESOLUTION);

					if (dpiHeight != -1)
						resolution.setResolutionY(dpiHeight);
					else
						resolution.setResolutionY(DEFAULT_IMAGE_RESOLUTION);

					this.imageResolution = resolution;

					logger.debug("dpi height = "+dpiHeight);
					logger.debug("dpi width = "+dpiWidth);
					return;
				}
				else {
					logger.debug("ImageInfo could not recognize file format!");
				}
			} catch (IOException ioe) {
				imageResolution = new ResolutionImpl(new DPIResolutionUnit(), DEFAULT_IMAGE_RESOLUTION);
			}
		}
		imageResolution = new ResolutionImpl(new DPIResolutionUnit(), DEFAULT_IMAGE_RESOLUTION);
	}

	private transient BufferedImage originalImage = null;

	@Override
	public BufferedImage getOriginalImage() {
		return originalImage;
	}

	private transient GeneralShape imageShape = null;
	private transient GeneralShape originalImageShape = null;

	@Override
	public GeneralShape getImageShape() {
		return imageShape;
	}

	@Override
	public GeneralShape getOriginalImageShape() {
		return originalImageShape;
	}

	/**
	 * @return the originalImageFileName + hashCode()
	 */
	@Override
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		result.append(originalImageFileName);
		result.append('.');
		result.append(hashCode());
		return result.toString();
	}

	@Override
	public Rectangle getBounds()
	{
		if (bounds == null) {
			if (image != null && imageShape != null) {
				bounds = imageShape.getBounds();
			}
			else
				return super.getBounds();
		}
		return bounds;
	}

	protected BufferedImage cloneImage(BufferedImage src) {
		return ImageUtil.cloneImage(src);
	}

	@Override
	public Class<? extends DrawComponent> getRenderModeClass() {
		return ImageDrawComponent.class;
	}

	@Override
	public String getTypeName() {
		return "Image";
	}

	private String imageFileExtension = null;

	@Override
	public String getImageFileExtension() {
		return imageFileExtension;
	}

	/**
	 * @return a deep copy of the ImageDrawComponent
	 * @see DrawComponent#clone(DrawComponentContainer)
	 */
	@Override
	public Object clone(DrawComponentContainer parent)
	{
		ImageDrawComponentImpl imageDC = (ImageDrawComponentImpl) super.clone(parent);
		if (this.image != null)
			imageDC.image = cloneImage(this.image);

		imageDC.bitsPerPixel = bitsPerPixel;
		if (this.imageShape != null)
			imageDC.imageShape = (GeneralShape) this.imageShape.clone();

		if (this.originalImageShape != null)
			imageDC.originalImageShape = (GeneralShape) this.originalImageShape.clone();

//		imageDC.op = new AffineTransformOp(op.getTransform(), interpolationType);
		if (this.originalImage != null)
			imageDC.originalImage = cloneImage(this.originalImage);

//		imageDC.interpolationType = interpolationType;
		imageDC.imageFileExtension = imageFileExtension;
		imageDC.originalImageFileLastModified = originalImageFileLastModified;
		imageDC.originalImageFileName = originalImageFileName;
		imageDC.originalImageData = originalImageData;

		if (this.imageResolution != null)
			imageDC.imageResolution = (Resolution) imageResolution.clone();

		// clone all RenderModeMetaData
		List<RenderModeMetaData> cloneList = new LinkedList<RenderModeMetaData>();
		Map<String, RenderModeMetaData> cloneMap = new HashMap<String, RenderModeMetaData>();
		for (Iterator<RenderModeMetaData> it = renderModeMetaDataList.iterator(); it.hasNext(); ) {
			RenderModeMetaData rmmd = it.next();
			RenderModeMetaData clone = (RenderModeMetaData) rmmd.clone();
			cloneMap.put(clone.getId(), clone);
			cloneList.add(clone);
		}
		imageDC.renderModeMetaDataList = cloneList;
		imageDC.id2RenderModeMetaData = cloneMap;
		imageDC.originalX = originalX;
		imageDC.originalY = originalY;
		imageDC.imageKey = imageKey;
		return imageDC;
	}

	@Override
	public void transform(AffineTransform at, boolean fromParent)
	{
		if (originalImageShape != null)
		{
			// transform imageShape to determine bounds
			super.transform(at, fromParent);
			imageShape = (GeneralShape) originalImageShape.clone();
			imageShape.transform(getAffineTransform());
		}

		if (!fromParent && getParent() != null)
			getParent().notifyChildTransform(this);
	}

//	private transient BufferedImage modifiedImage;
//	private transient BufferedImage newImage;
//	protected BufferedImage getTransformedImage(BufferedImage image, AffineTransform at)
//	{
//		if (image == null)
//			throw new IllegalArgumentException("Param image must not be null");
//
//		if (at == null)
//			throw new IllegalArgumentException("Param at must not be null");
//
//		long start = System.currentTimeMillis();
//		if (logger.isDebugEnabled()) {
//			logger.debug("memory before applying image renderModeMetaData");
//			logMemory();
//		}
//		modifiedImage = createRenderedImage(getRenderMode(), image);
//		if (logger.isDebugEnabled()) {
//			logger.debug("memory after applying image renderModeMetaData");
//			logMemory();
//		}
//
//		op = new AffineTransformOp(at, getInterpolationType());
//		Rectangle transformedBounds = op.getBounds2D(modifiedImage).getBounds();
//		int minX = (int) transformedBounds.getMinX();
//		int minY = (int) transformedBounds.getMinY();
//
//		// translate always to the origin
//		AffineTransform newAT = new AffineTransform(at);
//		AffineTransform translateAT = AffineTransform.getTranslateInstance(-minX, -minY);
//		newAT.preConcatenate(translateAT);
//		op = new AffineTransformOp(newAT, getInterpolationType());
//		if (logger.isDebugEnabled()) {
//			logger.debug("memory before transforming image");
//			logMemory();
//		}
//		newImage = op.filter(modifiedImage, null);
//		if (logger.isDebugEnabled()) {
//			logger.debug("memory after transforming image");
//			logMemory();
//		}
//
//		if (logger.isDebugEnabled()) {
//			long end = System.currentTimeMillis() - start;
//			logger.debug("getTransformedImage() took "+end+" ms!");
//		}
//		return newImage;
//	}

	@Override
	protected void primSetRenderMode(String mode)
	{
		renderMode = mode;
		renderImage(getRenderMode());
	}

	public void renderImage(String renderMode)
	{
		if (originalImage != null)
			image = createRenderedImage();
	}

	private Resolution imageResolution = null;
	@Override
	public Resolution getImageResolution()
	{
		if (imageResolution == null) {
			determineImageResolution();
		}
		return imageResolution;
	}

	@Override
	public void setImageResolution(Resolution imageResolution) {
		this.imageResolution = imageResolution;
	}

	protected void initDefaultRenderModeMetaData()
	{
		// RGB Color Conversion
		Set<String> rgbRenderModes = new HashSet<String>();
		rgbRenderModes.add(RenderConstants.RGB_MODE);
		String rgbRenderClassName = RGBColorConvertDelegate.class.getName();
		RenderModeMetaData metaDataRGB = new RenderModeMetaData(RenderModeConstants.COLOR_CONVERT_RGB,
				rgbRenderModes, rgbRenderClassName, null, true);
		addRenderModeMetaData(metaDataRGB);

		// Gray Color Conversion
		Set<String> grayRenderModes = new HashSet<String>();
		grayRenderModes.add(RenderConstants.GRAY_MODE);
		String grayRenderClassName = GrayscaleColorConvertDelegate.class.getName();
		RenderModeMetaData metaDataGray = new RenderModeMetaData(RenderModeConstants.COLOR_CONVERT_GREYSCALE,
				grayRenderModes, grayRenderClassName, null, true);
		addRenderModeMetaData(metaDataGray);

		// Black White Color Conversion
		Set<String> bwRenderModes = new HashSet<String>();
		bwRenderModes.add(RenderConstants.BLACK_WHITE_MODE);
		String bwRenderClassName = BlackWhiteColorConvertDelegate.class.getName();
		RenderModeMetaData metaDataBW = new RenderModeMetaData(RenderModeConstants.COLOR_CONVERT_BLACK_WHITE,
				bwRenderModes, bwRenderClassName, null, true);
		addRenderModeMetaData(metaDataBW);
	}

	/**
	 * Get a (hopefully) unique key for the image in the current situation (e.g. render-mode, scaling etc.).
	 * In contrast to {@link #getImageKey()}, which returns the key of the original image, this method
	 * takes all parameters into account that affect the half-rendered image returned by {@link #createRenderedImage()}
	 * and {@link #getImage()}.
	 *
	 * @return the key specifying the rendered image returned by {@link #getImage()}.
	 */
	public String getRenderedImageKey()
	{
		// generate cache-key for the image:
		//   * hash (imageKey) as String
		//   * render mode
		//   * render mode meta data (somehow encoded)
		String imageKey = Util.encodeHexStr(getImageKey());
		StringBuilder sb = new StringBuilder();
		sb.append(imageKey);
		for (RenderModeMetaData renderModeMetaData : renderModeMetaDataList) {
			sb.append(File.separatorChar);
			sb.append(renderModeMetaData.getRenderModeMetaDataKey());
		}

		return sb.toString();
	}

	protected BufferedImage createRenderedImage() // String renderMode) // , RenderedImage img)
	{
//		String renderedImageKey = getRenderedImageKey();
		// get the cached image. if not null, clone and return it.

		// cached image does not exist => create it and put it into the cache.
		RenderedImage img = null;
		for (Iterator<RenderModeMetaData> it = renderModeMetaDataList.iterator(); it.hasNext(); )
		{
			RenderModeMetaData renderModeMetaData = it.next();
			if (renderModeMetaData.getSupportedRenderModes().contains(renderMode))
			{
				try {
					img = renderModeMetaData.getRendererDelegate().render(renderMode, this, originalImage, renderModeMetaData);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

		if (img == null) // no rendering done
			return originalImage;

		return ImageUtil.convertToBufferedImage(img);
	}

	private List<RenderModeMetaData> renderModeMetaDataList = new LinkedList<RenderModeMetaData>();
	/**
	 * @see ImageDrawComponent#getRenderModeMetaDataList()
	 */
	public List<RenderModeMetaData> getRenderModeMetaDataList() {
		return Collections.unmodifiableList(renderModeMetaDataList);
	}

	private transient Map<String, RenderModeMetaData> id2RenderModeMetaData = null;
	protected Map<String, RenderModeMetaData> getID2RenderModeMetaData() {
		if (id2RenderModeMetaData == null)
			id2RenderModeMetaData = new HashMap<String, RenderModeMetaData>();
		return id2RenderModeMetaData;
	}

	/**
	 * @see ImageDrawComponent#addRenderModeMetaData(RenderModeMetaData, int)
	 */
	public void addRenderModeMetaData(RenderModeMetaData renderModeMetaData, int index)
	{
		if (getID2RenderModeMetaData().containsKey(renderModeMetaData.getId())) {
			RenderModeMetaData oldMetaData = getID2RenderModeMetaData().remove(renderModeMetaData.getId());
			renderModeMetaDataList.remove(oldMetaData);
		}
		getID2RenderModeMetaData().put(renderModeMetaData.getId(), renderModeMetaData);
		renderModeMetaDataList.add(index, renderModeMetaData);
	}

	/**
	 * @see ImageDrawComponent#addRenderModeMetaData(RenderModeMetaData)
	 */
	public void addRenderModeMetaData(RenderModeMetaData renderModeMetaData)
	{
		int index = renderModeMetaDataList.size() - 1;
		if (index < 0)
			index = 0;
		addRenderModeMetaData(renderModeMetaData, index);
	}

	/**
	 * @see ImageDrawComponent#removeRenderModeMetaData(RenderModeMetaData)
	 */
	public void removeRenderModeMetaData(RenderModeMetaData renderModeMetaData)
	{
		getID2RenderModeMetaData().remove(renderModeMetaData.getId());
		renderModeMetaDataList.remove(renderModeMetaData);
	}

//******************************* BEGIN Resolution Image **********************************

	protected void primSetImage(BufferedImage newImage, int x, int y)
	{
		originalImage = newImage;
		image = createRenderedImage(); // getRenderMode(), originalImage); // TODO is the rendermode already correct here?
		clearBounds();
		originalImageShape = GeneralShapeFactory.createRectangle(x, y,
				(int) (originalImage.getWidth() * getResolutionFactor()),
				(int) (originalImage.getHeight() * getResolutionFactor()));
		imageShape = (GeneralShape) originalImageShape.clone();
	}

	public static final double DEFAULT_RESOLUTION_FACTOR = -1;
	private double resolutionFactor = DEFAULT_RESOLUTION_FACTOR;
	public double getResolutionFactor()
	{
		if (resolutionFactor == DEFAULT_RESOLUTION_FACTOR) {
			Resolution documentRes = getRoot().getResolution();
			IResolutionUnit dpiUnit = new DPIResolutionUnit();
			double documentResolutionX =  documentRes.getResolutionX(dpiUnit);
			double documentResolutionY =  documentRes.getResolutionY(dpiUnit);
			double imageResolutionX = getImageResolution().getResolutionX(dpiUnit);
			double imageResolutionY = getImageResolution().getResolutionX(dpiUnit);
			double factorX = imageResolutionX / documentResolutionX;
			double factorY = imageResolutionY / documentResolutionY;
			resolutionFactor = Math.max(factorX, factorY);
			logger.debug("resolutionFactor = "+resolutionFactor);
		}
		return resolutionFactor;
	}

//	/**
//	 * determines that the document resolution has changed
//	 */
//	public void resolutionChanged() {
//		resolutionFactor = DEFAULT_RESOLUTION_FACTOR;
//	}

	@Override
	protected Renderer initDefaultRenderer() {
		Renderer r = new BaseRenderer();
		r.addRenderContext(new J2DImageDefaultRenderer());
		return r;
	}

	@Override
	public void dispose()
	{
		super.dispose();
		id2RenderModeMetaData = null;
		if (image != null)
			image.flush();
		image = null;
		if (originalImage != null)
			originalImage.flush();
		originalImage = null;
		originalImageData = null;
		imageShape = null;
		originalImageShape = null;
	}

	public void reloadImage(BufferedImage image)
	{
		if (image != null)
		{
			originalImage = cloneImage(image);
			if (originalImage.getData().getDataBuffer() instanceof DataBufferByte) {
				DataBufferByte dataBufferByte = (DataBufferByte) originalImage.getData().getDataBuffer();
				originalImageData = dataBufferByte.getData();
			}
			primSetImage(originalImage, originalX, originalY);
			imageShape.transform(getAffineTransform());
			this.originalImageFileLastModified = System.currentTimeMillis();
			bitsPerPixel = originalImage.getColorModel().getPixelSize();
			if (originalImageData != null)
				imageKey = generateImageKey(originalImageData);
		}
	}

	@Override
	public void setOriginalX(int originalX) {
		this.originalX = originalX;
	}

	@Override
	public void setOriginalY(int originalY) {
		this.originalY = originalY;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.editor2d.DrawComponent#resolutionChanged(org.nightlabs.i18n.unit.resolution.Resolution, org.nightlabs.i18n.unit.resolution.Resolution)
	 */
	@Override
	public void resolutionChanged(Resolution oldResolution, Resolution newResolution)
	{
		resolutionFactor = DEFAULT_RESOLUTION_FACTOR;
		primSetImage(originalImage, originalX, originalY);
		super.resolutionChanged(oldResolution, newResolution);
	}

} //ImageDrawComponentImpl
