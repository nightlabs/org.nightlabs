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
package org.nightlabs.editor2d.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.render.RenderModeManager;

/**
 * A helper to get tile-images (as {@link BufferedImage} or as {@link File}) of a {@link DrawComponent}.
 * Throw-away instances of this class are used to render a <code>DrawComponent</code>
 * into a grid of sub-images.
 *
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Khaled [at] NightLabs [dot] de
 * @author marco schulze - marco at nightlabs dot de
 */
public class ImageSplit
{
	private DrawComponent drawComponent;
	private int width = 1;
	private int height = 1;
	private int renderQuality = RenderingHintsManager.RENDER_MODE_DEFAULT;
	private String renderMode = null; // null causes the default renderer to be used.
	private RenderModeManager renderModeManager = null;
	private int imageType = BufferedImage.TYPE_INT_ARGB;

	public ImageSplit() { }

	public ImageSplit(DrawComponent drawComponent, int width, int height) {
		this.drawComponent = drawComponent;
		this.setWidth(width);
		this.setHeight(height);
	}

	/**
	 * Get the {@link DrawComponent} to be rendered into an image.
	 * @return the {@link DrawComponent} to be rendered into an image.
	 */
	public DrawComponent getDrawComponent() {
		return drawComponent;
	}
	/**
	 * Set the {@link DrawComponent} to be rendered into an image.
	 * @param drawComponent the {@link DrawComponent} to be rendered into an image.
	 */
	public void setDrawComponent(DrawComponent drawComponent) {
		this.drawComponent = drawComponent;
	}

	/**
	 * Get the width of the output image in pixels.
	 * @return the width of the output image in pixels.
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * Set the width of the output image in pixels.
	 * @param width the width of the output image in pixels.
	 */
	public void setWidth(int width) {
		if (width <= 0)
			throw new IllegalArgumentException("width <= 0");

		this.width = width;
	}
	/**
	 * Get the height of the output image in pixels.
	 * @return the height of the output image in pixels.
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * Set the height of the output image in pixels.
	 * @param height the height of the output image in pixels.
	 */
	public void setHeight(int height) {
		if (height <= 0)
			throw new IllegalArgumentException("height <= 0");

		this.height = height;
	}

	/**
	 * Get the render quality. This is one of {@link RenderingHintsManager#RENDER_MODE_DEFAULT},
	 * {@link RenderingHintsManager#RENDER_MODE_QUALITY},
	 * {@link RenderingHintsManager#RENDER_MODE_SPEED}. If not set by {@link #setRenderQuality(int)},
	 * this property has the default value {@link RenderingHintsManager#RENDER_MODE_DEFAULT}.
	 * @return the render quality.
	 */
	public int getRenderQuality() {
		return renderQuality;
	}
	/**
	 * Set the render quality. This is one of {@link RenderingHintsManager#RENDER_MODE_DEFAULT},
	 * {@link RenderingHintsManager#RENDER_MODE_QUALITY},
	 * {@link RenderingHintsManager#RENDER_MODE_SPEED}.
	 * @param renderQuality the render quality.
	 * @see #getRenderQuality()
	 */
	public void setRenderQuality(int renderQuality) {
		this.renderQuality = renderQuality;
	}
	/**
	 * Get the renderMode of the {@link DrawComponent}.
	 * @return the renderMode of the {@link DrawComponent}. Is <code>null</code>, if the default renderer is to be used.
	 * @see DrawComponent#getRenderMode()
	 */
	public String getRenderMode() {
		return renderMode;
	}
	/**
	 * Set the renderMode of the {@link DrawComponent}.
	 * @param renderMode the renderMode of the {@link DrawComponent}. Can be <code>null</code> to use the default renderer.
	 * @see #getRenderMode()
	 * @see DrawComponent#getRenderMode()
	 */
	public void setRenderMode(String renderMode) {
		this.renderMode = renderMode;
	}
	/**
	 * Get the <code>RenderModeManager</code> which contains the registered render-modes. Can be <code>null</code>, if none has been set.
	 * @return the <code>RenderModeManager</code> which contains the registered render-modes.
	 */
	public RenderModeManager getRenderModeManager() {
		return renderModeManager;
	}
	/**
	 * Set the <code>RenderModeManager</code> which contains the registered render-modes.
	 * @param renderModeManager the <code>RenderModeManager</code> which contains the registered render-modes.
	 */
	public void setRenderModeManager(RenderModeManager renderModeManager) {
		this.renderModeManager = renderModeManager;
	}

	/**
	 * Get the image type. If it has not been changed by {@link #setImageType(int)},
	 * this property has the default value {@link BufferedImage#TYPE_INT_ARGB}.
	 *
	 * @return the image type.
	 * @see #setImageType(int)
	 */
	public int getImageType() {
		return imageType;
	}
	/**
	 * Set the image type. Possible values are:
	 * <ul>
	 *		<li>{@link BufferedImage#TYPE_INT_ARGB}: This is the default value which is fast and recommended for screen
	 *		operations. When writing into a JPG file, however, this seems to make problems and you should use
	 *		{@link BufferedImage#TYPE_INT_RGB} instead.</li>
	 *		<li>{@link BufferedImage#TYPE_INT_RGB}: Use this when writing JPG files.</li>
	 *		<li>other TYPE_* constants in {@link BufferedImage}: Depending on your use case, you might need another type
	 *		(none of these have been tested by us, yet - 2008-09-01).</li>
	 * </ul>
	 *
	 * @param imageType the image type.
	 * @see #getImageType()
	 */
	public void setImageType(int imageType) {
		this.imageType = imageType;
	}

	/**
	 * renders the given {@link DrawComponent} into a image of the given size and splits
	 * this images then into the given amount tiles in x- and y-direction
	 *
	 * @param xTiles the amount of tiles in x-direction
	 * @param yTiles the amount of tiles in y-direction
	 * @return an array of {@link BufferedImage}s where the first dimension determines the x index
	 * and the second the y index
	 */
	public BufferedImage[][] createTiles(int xTiles, int yTiles)
	{
		if (drawComponent == null)
			throw new IllegalStateException("drawComponent is null! Call setDrawComponent(...) first!");

		if (xTiles <= 0)
			throw new IllegalArgumentException("Param xTiles must be > 0");
		if (yTiles <= 0)
			throw new IllegalArgumentException("Param yTiles must be > 0");

		ImageCreator ic = new ImageCreator(drawComponent, width, height);
		ic.setRenderMode(renderMode);
		ic.setRenderModeManager(renderModeManager);
		ic.setImageType(imageType);

//		BufferedImage bi = ImageGenerator.getModelAsImage(dc, width, height, renderMode, renderModeMan);
		BufferedImage bi = ic.createImage();

		int tileWidth = width / xTiles;
		int tileHeight = height / yTiles;
		BufferedImage[][] tiles = new BufferedImage[xTiles][yTiles];
		for (int i=0; i<xTiles; i++) {
			for (int j=0; j<yTiles; j++) {
				tiles[i][j] = bi.getSubimage(i*tileWidth, j*tileHeight, tileWidth, tileHeight);
			}
		}
		return tiles;
	}

	public static File getTileFile(File outputDir, String filePrefix, int tileIndexX, int tileIndexY, String imageFormat)
	{
		return new File(outputDir, filePrefix + "-x" + tileIndexX + 'y' + tileIndexY + '.' + imageFormat);
	}

	/**
	 * renders the given {@link DrawComponent} into a image of the given size and splits
	 * this images then into the given amount of tiles in x- and y-direction, the created
	 * image Tiles are encoded in the given imageFormat and stored in the given File directory
	 * in the following format (filePrefix + x-Index + y-Index + "." + imageFormat)
	 *
	 * @param xTiles the amount of tiles in x-direction
	 * @param yTiles the amount of tiles in y-direction
	 * @param imageFormat the imageFormat which determines the image encoding (eg. jpg, png, bmp)
	 * @param outputDir the output directory.
	 * @param filePrefix the prefix for the tileFiles
	 * @return an 2-dimensional array of Files which contain the tiled images.
	 * The first dimension determines the x index and the second the y index
	 * @throws IOException
	 */
	public File[][] createTileFiles(
			int xTiles, int yTiles, String imageFormat, File outputDir, String filePrefix
	)
	throws IOException
	{
		long start = System.currentTimeMillis();
//		BufferedImage[][] tiles = createTiles(dc, width, height, xTiles, yTiles, renderMode, renderModeMan);
		BufferedImage[][] tiles = createTiles(xTiles, yTiles);

		File[][] files = new File[xTiles][yTiles];
		for (int j=0; j<yTiles; j++) {
			for (int i=0; i<xTiles; i++) {
				BufferedImage bi = tiles[i][j];
				File file = getTileFile(outputDir, filePrefix, i, j, imageFormat);
				ImageIO.write(bi, imageFormat, file);
				files[i][j] = file;
			}
		}
		long duration = System.currentTimeMillis() - start;
		System.out.println("Rendering + Tiling + Saving to File took "+duration+" ms!");
		return files;
	}

	/**
	 * renders the given {@link DrawComponent} into a image of the given size and splits
	 * this images then into the needed amount of tiles in x- and y-direction, the created
	 * image Tiles are encoded in the given imageFormat and stored in the given File directory
	 * in the following format (filePrefix + x-Index + y-Index + "." + imageFormat)
	 *
	 * @param xTileWidth the size of tiles in x-direction
	 * @param yTileHeight the size of tiles in y-direction
	 * @param imageFormat the imageFormat which determines the image encoding (eg. jpg, png, bmp)
	 * @param outputDir the output directory
	 * @param filePrefix the prefix for the tileFiles
	 * @return an 2-dimensional array of Files which contain the tiled images.
	 * The first dimension determines the x index and the second the y index.
	 * @throws IOException if writing to files fails.
	 */
	public File[][] createFixedSizeTileFiles(
			int xTileWidth, int yTileHeight, String imageFormat, File outputDir, String filePrefix
	)
	throws IOException
	{
		if (drawComponent == null)
			throw new IllegalStateException("drawComponent is null! Call setDrawComponent(...) first!");

		int xTiles, yTiles;
		int xTileWidthTmp = xTileWidth;
		int yTileHeightTmp = yTileHeight;

		xTiles = width / xTileWidth;
		if(width % xTileWidth != 0)
			xTiles++;
		yTiles  = height / yTileHeight;
		if(height % yTileHeight != 0)
			yTiles++;

		ImageCreator ic = new ImageCreator(drawComponent, width, height);
		ic.setRenderMode(renderMode);
		ic.setRenderModeManager(renderModeManager);
		ic.setImageType(imageType);
//		BufferedImage bi = ImageGenerator.getModelAsImage(dc, width, height, renderMode, renderModeMan);
		BufferedImage bi = ic.createImage();

		BufferedImage[][] tiles = new BufferedImage[xTiles][yTiles];
		File[][] files = new File[xTiles][yTiles];

		for (int j=0; j<yTiles; j++) {
			for (int i=0; i<xTiles; i++) {
				if (width % xTileWidth != 0 && i == (xTiles-1))
					xTileWidthTmp = width - (xTileWidth*(xTiles-1));
				if (height % yTileHeight != 0 && j == (yTiles-1))
					yTileHeightTmp = height - (yTileHeight*(yTiles-1));
				tiles[i][j] = bi.getSubimage(i*xTileWidth, j*yTileHeight, xTileWidthTmp, yTileHeightTmp);
				xTileWidthTmp = xTileWidth;
				yTileHeightTmp = yTileHeight;

				File file = getTileFile(outputDir, filePrefix, i, j, imageFormat);
				if (!file.exists())
					file.createNewFile();
				ImageIO.write(tiles[i][j], imageFormat, file);
				files[i][j] = file;
				tiles[i][j].flush();
				tiles[i][j] = null;
			}
		}
		bi.flush();
		bi = null;

////		if (logger.isDebugEnabled())
////		{
//			long freeMemory = Runtime.getRuntime().freeMemory();
//			long totalMemory = Runtime.getRuntime().totalMemory();
//			long maxMemory = Runtime.getRuntime().maxMemory();
//			long usedMemory = totalMemory - freeMemory;
//
//			long divisorMB = 1024 * 1024;
////			logger.info("freeMemory = "+(freeMemory/divisorMB) +" MB!");
////			logger.info("usedMemory = "+(usedMemory/divisorMB) +" MB!");
////			logger.info("totalMemory = "+(totalMemory/divisorMB) +" MB!");
////			logger.info("maxMemory = "+(maxMemory/divisorMB) +" MB!");
//			System.out.println("freeMemory = "+(freeMemory/divisorMB) +" MB!");
//			System.out.println("usedMemory = "+(usedMemory/divisorMB) +" MB!");
//			System.out.println("totalMemory = "+(totalMemory/divisorMB) +" MB!");
//			System.out.println("maxMemory = "+(maxMemory/divisorMB) +" MB!");
//
//			long start = System.currentTimeMillis();
//			System.gc();
////			logger.info("GC took = "+(System.currentTimeMillis()-start)+" ms!");
//			System.out.println("GC took = "+(System.currentTimeMillis()-start)+" ms!");
////		}

		return files;
	}
}
