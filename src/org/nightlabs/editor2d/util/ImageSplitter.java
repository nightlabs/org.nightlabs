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
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Khaled [at] NightLabs [dot] de
 * @deprecated Use {@link ImageSplit} instead!
 */
@Deprecated
public class ImageSplitter
{
//	private static final Logger logger = Logger.getLogger(ImageSplitter.class);

	/**
	 * renders the given {@link DrawComponent} into a image of the given size and splits
	 * this images then into the given amount tiles in x- and y-direction
	 *
	 * @param dc the {@link DrawComponent} to render into different tiles
	 * @param width the width of the untiled output image
	 * @param height the height of the untiled output image
	 * @param xTiles the amount of tiles in x-direction
	 * @param yTiles the amount of tiles in y-direction
	 * @return an array of {@link BufferedImage}s where the first dimension determines the x index
	 * and the second the y index
	 */
	public static BufferedImage[][] createTiles(DrawComponent dc, int width, int height,
			int xTiles, int yTiles)
	{
		if (width <= 0)
			throw new IllegalArgumentException("Param width must be > 0");
		if (height <= 0)
			throw new IllegalArgumentException("Param height must be > 0");
		if (xTiles <= 0)
			throw new IllegalArgumentException("Param xTiles must be > 0");
		if (yTiles <= 0)
			throw new IllegalArgumentException("Param yTiles must be > 0");
		if ((width % xTiles) != 0)
			throw new IllegalArgumentException("width % xTiles != 0");
		if ((height % yTiles) != 0)
			throw new IllegalArgumentException("height % yTiles != 0");

		BufferedImage bi = ImageGenerator.getModelAsImage(dc, width, height);
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

	/**
	 * renders the given {@link DrawComponent} into a image of the given size and splits
	 * this images then into the given amount tiles in x- and y-direction
	 *
	 * @param dc the {@link DrawComponent} to render into different tiles
	 * @param width the width of the untiled output image
	 * @param height the height of the untiled output image
	 * @param xTiles the amount of tiles in x-direction
	 * @param yTiles the amount of tiles in y-direction
	 * @return an array of {@link BufferedImage}s where the first dimension determines the x index
	 * and the second the y index
	 */
	public static BufferedImage[][] createTiles(DrawComponent dc, int width, int height,
			int xTiles, int yTiles, String renderMode, RenderModeManager renderModeMan)
	{
		if (width <= 0)
			throw new IllegalArgumentException("Param width must be > 0");
		if (height <= 0)
			throw new IllegalArgumentException("Param height must be > 0");
		if (xTiles <= 0)
			throw new IllegalArgumentException("Param xTiles must be > 0");
		if (yTiles <= 0)
			throw new IllegalArgumentException("Param yTiles must be > 0");
		if ((width % xTiles) != 0)
			throw new IllegalArgumentException("width % xTiles != 0");
		if ((height % yTiles) != 0)
			throw new IllegalArgumentException("height % yTiles != 0");

		BufferedImage bi = ImageGenerator.getModelAsImage(dc, width, height, renderMode, renderModeMan);
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
	 * @param dc the {@link DrawComponent} to render into different tiles
	 * @param width the width of the untiled output image
	 * @param height the height of the untiled output image
	 * @param xTiles the amount of tiles in x-direction
	 * @param yTiles the amount of tiles in y-direction
	 * @param imageFormat the imageFormat which determines the image encoding (eg. jpg, png, bmp)
	 * @param output the output directory
	 * @param filePrefix the prefix for the tileFiles
	 * @return an 2-dimensional array of Files which contain the tiled images.
	 * The first dimension determines the x index and the second the y index
	 * @throws IOException
	 */
	public static File[][] createTileFiles(DrawComponent dc, int width, int height,
			int xTiles, int yTiles, String imageFormat, File output, String filePrefix)
	throws IOException
	{
		long start = System.currentTimeMillis();
		BufferedImage[][] tiles = createTiles(dc, width, height, xTiles, yTiles);
		File[][] files = new File[xTiles][yTiles];
		for (int i=0; i<xTiles; i++) {
			for (int j=0; j<yTiles; j++) {
				BufferedImage bi = tiles[i][j];
				File file = getTileFile(output, filePrefix, i, j, imageFormat);
//				if (!file.exists()) // TODO is that really necessary?!
//					file.createNewFile();
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
	 * this images then into the given amount of tiles in x- and y-direction, the created
	 * image Tiles are encoded in the given imageFormat and stored in the given File directory
	 * in the following format (filePrefix + x-Index + y-Index + "." + imageFormat)
	 *
	 * @param dc the {@link DrawComponent} to render into different tiles
	 * @param width the width of the untiled output image
	 * @param height the height of the untiled output image
	 * @param xTiles the amount of tiles in x-direction
	 * @param yTiles the amount of tiles in y-direction
	 * @param imageFormat the imageFormat which determines the image encoding (eg. jpg, png, bmp)
	 * @param output the output directory
	 * @param filePrefix the prefix for the tileFiles
	 * @return an 2-dimensional array of Files which contain the tiled images.
	 * The first dimension determines the x index and the second the y index
	 * @throws IOException
	 */
	public static File[][] createTileFiles(DrawComponent dc, int width, int height,
			int xTiles, int yTiles, String imageFormat, File output, String filePrefix,
			String renderMode, RenderModeManager renderModeMan)
	throws IOException
	{
		long start = System.currentTimeMillis();
		BufferedImage[][] tiles = createTiles(dc, width, height, xTiles, yTiles, renderMode, renderModeMan);
		File[][] files = new File[xTiles][yTiles];
		for (int j=0; j<yTiles; j++) {
			for (int i=0; i<xTiles; i++) {
				BufferedImage bi = tiles[i][j];
				File file = getTileFile(output, filePrefix, i, j, imageFormat);
//				if (!file.exists()) // TODO necessary?
//					file.createNewFile();
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
	 * @param dc the {@link DrawComponent} to render into different tiles
	 * @param width the width of the untiled output image
	 * @param height the height of the untiled output image
	 * @param xTileWidth the size of tiles in x-direction
	 * @param yTileHeight the size of tiles in y-direction
	 * @param imageFormat the imageFormat which determines the image encoding (eg. jpg, png, bmp)
	 * @param output the output directory
	 * @param filePrefix the prefix for the tileFiles
	 * @return an 2-dimensional array of Files which contain the tiled images.
	 * The first dimension determines the x index and the second the y index
	 * @throws IOException
	 */
	public static File[][] createFixedSizeTileFiles(DrawComponent dc, int width, int height,
			int xTileWidth, int yTileHeight, String imageFormat, File output, String filePrefix,
			String renderMode, RenderModeManager renderModeMan)
	throws IOException
	{
		int xTiles, yTiles = 0;
		int xTileWidthTmp = xTileWidth;
		int yTileHeightTmp = yTileHeight;

		xTiles = width / xTileWidth;
		if(width % xTileWidth != 0)
			xTiles++;
		yTiles  = height / yTileHeight;
		if(height % yTileHeight != 0)
			yTiles++;

		BufferedImage bi = ImageGenerator.getModelAsImage(dc, width, height, renderMode, renderModeMan);
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

				File file = getTileFile(output, filePrefix, i, j, imageFormat);
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

//		if (logger.isDebugEnabled())
//		{
			long freeMemory = Runtime.getRuntime().freeMemory();
			long totalMemory = Runtime.getRuntime().totalMemory();
			long maxMemory = Runtime.getRuntime().maxMemory();
			long usedMemory = totalMemory - freeMemory;

			long divisorMB = 1024 * 1024;
//			logger.info("freeMemory = "+(freeMemory/divisorMB) +" MB!");
//			logger.info("usedMemory = "+(usedMemory/divisorMB) +" MB!");
//			logger.info("totalMemory = "+(totalMemory/divisorMB) +" MB!");
//			logger.info("maxMemory = "+(maxMemory/divisorMB) +" MB!");
			System.out.println("freeMemory = "+(freeMemory/divisorMB) +" MB!");
			System.out.println("usedMemory = "+(usedMemory/divisorMB) +" MB!");
			System.out.println("totalMemory = "+(totalMemory/divisorMB) +" MB!");
			System.out.println("maxMemory = "+(maxMemory/divisorMB) +" MB!");

			long start = System.currentTimeMillis();
			System.gc();
//			logger.info("GC took = "+(System.currentTimeMillis()-start)+" ms!");
			System.out.println("GC took = "+(System.currentTimeMillis()-start)+" ms!");
//		}

		return files;
	}
}
