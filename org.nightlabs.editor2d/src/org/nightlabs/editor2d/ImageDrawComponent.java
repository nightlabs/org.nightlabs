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

package org.nightlabs.editor2d;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.List;

import org.nightlabs.editor2d.image.ImageRendererDelegate;
import org.nightlabs.editor2d.image.RenderModeMetaData;
import org.nightlabs.editor2d.iofilter.XStreamFilter;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.render.Renderer;
import org.nightlabs.i18n.unit.resolution.Resolution;


public interface ImageDrawComponent
extends DrawComponent
{
	public static final String PROP_IMAGE = "image";
	public static final String PROP_ORIGINAL_FILE_NAME = "originalFileName";
	public static final String PROP_BITS_PER_PIXEL = "bitsPerPixel";
	public static final String PROP_RESOLUTION = "resolution";
	public static final String PROP_IMAGE_SHAPE = "imageShape";
	public static final String PROP_LAST_MODIFIED = "lastModified";
	public static final String PROP_IMAGE_KEY = "imageKey";
	public static final String PROP_RENDER_MODE_META_DATA = "renderModeMetaData";
	public static final int DEFAULT_IMAGE_RESOLUTION = 72;
//	public static final BufferedImage IMAGE_DEFAULT = null;

	/**
	 * returns the current manipulated/transformed BufferedImage of the ImageDrawComponent
	 *
	 * @return the BufferedImage of the ImageDrawComponent
	 */
  BufferedImage getImage();

  String getRenderedImageKey();

  /**
   * returns the originalImage which was initially loaded by
   * {@link ImageDrawComponent#loadImage(String, long, InputStream)}
   * this image should never be manipulated or transformed
   *
   * @return the original image (without transformation)
   */
  BufferedImage getOriginalImage();

  /**
   * returns the GeneralShape which represents the bounds of original loaded image
   *
   * @return the Shape which corresponds to the original loaded BufferedImage
   */
  GeneralShape getOriginalImageShape();

  /**
   * returns the GeneralShape which represents the bounds of the transformed
   * Image returned by {@link ImageDrawComponent#getImage()}
   *
   * @return the Shape which corresponds to the transformed BufferedImage
   */
  GeneralShape getImageShape();

//  /**
//   * IMPORTANT:
//   * This Method should never be called, only for deserialization
//   *
//   * @param newImageShape
//   */
//  void setImageShape(GeneralShape newImageShape);

  /**
   * returns the fileExtension of the original image (e.g. jpg)
   *
   * @return the fileExtension of the original image (e.g. jpg)
   */
  String getImageFileExtension();

  /**
   * returns the original name of the loaded file
   *
   * @return the original name of the loaded file
   */
  String getOriginalImageFileName();

  /**
   * reloads the image if all other necessary data, has been already loaded
   * e.g. the {@link XStreamFilter} already loads all data except the binary image data,
   * this method is than called afterwards
   */
  void reloadImage(InputStream in);

  /**
   * reloads the image if all other necessary data, has been already loaded
   * e.g. the {@link XStreamFilter} already loads all data except the binary image data,
   * this method is than called afterwards
   */
  void reloadImage(BufferedImage img);

  /**
   * loads the image
   *
   * @param originalImageFileName the original fileName of the image file
   * @param originalImageFileLastModified the timeStamp of the image file
   * @param in the {@link InputStream} to load the image from
   *
   * @see File#lastModified()
   */
  void loadImage(String originalImageFileName, long originalImageFileLastModified, InputStream in);

  /**
   * returns the original binary data of the original image, which has been created when
   * {@link ImageDrawComponent#loadImage(String, long, InputStream)} was called
   *
   * @return the original image data as byte array
   */
  byte[] getOriginalImageData();

  /**
   * returns the timeStamp of the original image
   *
   * @return the timeStamp of the original image
   */
  long getOriginalImageFileLastModified();

  /**
   * returns the color depth of the originalImage in bits per pixel
   * determined from the {@link ColorModel} of the BufferedImage
   *
   * @return the color depth of the originalImage in bits per pixel
   */
  int getBitsPerPixel();

  /**
   * returns the {@link Resolution} of the originalImage
   * if no resolution could be determined when the image was loaded
   * {@link ImageDrawComponent#DEFAULT_IMAGE_RESOLUTION} is used
   *
   * @return the resolution of the originalImage
   */
  Resolution getImageResolution();

  /**
   * sets the resolution of the originalImage
   * @param imageResolution the resolution to set
   */
  void setImageResolution(Resolution imageResolution);

//  /**
//   * determines that the document resolution has changed
//   */
//  void resolutionChanged();

  /**
   * return a unmodifiable {@link List} of all added {@link RenderModeMetaData}s
   * @return a unmodifiable List of all added RenderModeMetaDatas
   *
   * @see ImageDrawComponent#addRenderModeMetaData(RenderModeMetaData)
   */
  List<RenderModeMetaData> getRenderModeMetaDataList();

  /**
   * adds a {@link RenderModeMetaData} to the RenderModeMetaDataList at the given index
   * if already another RenderModeMetaData has been added with the same id, the previously
   * added will be removed
   * the index is imported to determine the order in which all {@link RenderModeMetaData}s will
   * be performed for a certain renderMode, when {@link ImageDrawComponent#renderImage(String)} or
   * {@link ImageDrawComponent#setRenderMode(String)} is called
   *
   * @param renderModeMetaData the {@link RenderModeMetaData} to add
   * @param index the index of the List where to add it
   *
   * @see ImageDrawComponent#getRenderModeMetaDataList()
   * @see RenderModeMetaData
   */
  void addRenderModeMetaData(RenderModeMetaData renderModeMetaData, int index);

  /**
   * adds a {@link RenderModeMetaData} to the end of the RenderModeMetaDataList
   *
   * @param renderModeMetaData the RenderModeMetaData to add
   *
   * @see ImageDrawComponent#getRenderModeMetaDataList()
   * @see RenderModeMetaData
   */
  void addRenderModeMetaData(RenderModeMetaData renderModeMetaData);

  /**
   * removes a {@link RenderModeMetaData} from the {@link RenderModeMetaData}-List
   *
   * @param renderModeMetaData the RenderModeMetaData to remove from the RenderModeMetaDataList
   *
   * @see ImageDrawComponent#getRenderModeMetaDataList()
   * @see RenderModeMetaData
   */
  void removeRenderModeMetaData(RenderModeMetaData renderModeMetaData);

  /**
   * renders the originalImage in a certain renderMode,
   * for a ImageDrawComponent not the {@link Renderer} is responsible for rendering the
   * different renderModes, but the contained List of {@link RenderModeMetaData}s with
   * the containing {@link ImageRendererDelegate}s.
   * The {@link ImageDrawComponent} is usally only painted by the {@link ImageDefaultRenderer}
   * which just paints the modified image, {@link ImageDrawComponent#getImage()} and all other
   * imageManipulations, are performed by the List of {@link RenderModeMetaData}s.
   *
   * @param renderMode the RenderMode in which the originalImage should be rendered
   * @see DrawComponent#setRenderMode(String)
   */
  void renderImage(String renderMode);

//  /**
//   * WARNING: only for deserialization
//   * should never be called, use {@link ImageDrawComponent#loadImage(String, long, InputStream)} instead
//   *
//   * @param originalFileName the originalFilenname to set
//   */
//  void setOriginalImageFileName(String originalFileName);
//
//  /**
//   * WARNING: only for deserialization
//   * should never be called, use {@link ImageDrawComponent#loadImage(String, long, InputStream)} instead
//   *
//   * @param lastModified the lastModified-TimeStamp of the originalImage to set
//   */
//  void setOriginalImageFileLastModified(long lastModified);

  /**
   * returns a unique MD5 key, of the originalImageData
   *
   * @return a unique MD5 key, of the originalImageData
   * @see MessageDigest
   */
  byte[] getImageKey();

//  /**
//   * return the interpolationType for the {@link AffineTransformOp}, which determines
//   * with which interpolationType the image is transformed
//   * the default is {@link ImageDrawComponent#DEFAULT_INTERPOLATION_TYPE}
//   *
//   * @return the interpolationType for transforming the image
//   * @see AffineTransformOp#TYPE_BICUBIC
//   * @see AffineTransformOp#TYPE_BILINEAR
//   * @see AffineTransformOp#TYPE_NEAREST_NEIGHBOR
//   */
//  int getInterpolationType();
//
//  /**
//   * sets the interpolationType for the {@link AffineTransformOp}, which determines
//   * with which interpolationType the image is transformed
//   *
//   * @param interpolationType the interpolationType to set
//   * @see AffineTransformOp#TYPE_BICUBIC
//   * @see AffineTransformOp#TYPE_BILINEAR
//   * @see AffineTransformOp#TYPE_NEAREST_NEIGHBOR
//   */
//  void setInterpolationType(int interpolationType);

  /**
   * Returns the original/initial X-Position of the original Image
   * @return the original X-Position
   */
  int getOriginalX();

  /**
   * Returns the original/initial Y-Position of the original Image
   * @return the original Y-Position
   */
  int getOriginalY();

  /**
   * Sets the the original/initial X Position of the image
   * @param originalX the original/initial X Position of the image to set
   */
  void setOriginalX(int originalX);

  /**
   * Sets the the original/initial Y Position of the image
   * @param originalX the original/initial Y Position of the image to set
   */
  void setOriginalY(int originalY);
} // ImageDrawComponent
