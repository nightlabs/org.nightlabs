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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.RootDrawComponent;
import org.nightlabs.editor2d.iofilter.XStreamFilter;
import org.nightlabs.editor2d.render.RenderConstants;
import org.nightlabs.editor2d.render.RenderModeManager;
import org.nightlabs.editor2d.render.j2d.J2DRenderContext;

/**
 * This class has several utility Methods for creating images of an {@link DrawComponent}
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @deprecated Use {@link ImageCreator} instead.
 */
@Deprecated
public class ImageGenerator
{
	/**
	 * renders the given {@link DrawComponent} into a {@link BufferedImage}
	 * of the given size
	 *
	 * @param dc the {@link DrawComponent} to render to image
	 * @param width the width of the output image in pixels
	 * @param height the height of the output image in pixels
	 * @param offsetX the offset in x-direction in model coordinates
	 * @param offsetY the ofset in y-direction in model coordinates
	 * @param renderQuality determines the quality of Rendering, valid values are
	 * {@link RenderingHintsManager#RENDER_MODE_DEFAULT},
	 * {@link RenderingHintsManager#RENDER_MODE_QUALITY},
	 * {@link RenderingHintsManager#RENDER_MODE_SPEED}
	 * @param renderMode the renderMode of the {@link DrawComponent}
	 * @param centered determines if the model should be centered in the image
	 * @param renderModeMan the RenderModeManager which contains the registered renderModes
	 * @param fitPage determines if the drawComponent should fit the page or if false fitAll
	 * @return a {@link BufferedImage} which shows the rendered DrawComponent
	 *
	 * @see DrawComponent#getRenderMode()
	 */
	public static BufferedImage getModelAsImage(DrawComponent dc, int width, int height,
			int offsetX, int offsetY, int renderQuality, String renderMode, boolean centered,
			RenderModeManager renderModeMan, boolean fitPage)
	{
		if (dc == null)
			throw new IllegalArgumentException("Param dc must not be null!");
		if (width <= 0)
			throw new IllegalArgumentException("Param witdh must be > 0");
		if (height <= 0)
			throw new IllegalArgumentException("Param height must be > 0");

		double dcWidth = 0;
		double dcHeight = 0;
		// TODO determine the page where the dc is contained in
		PageDrawComponent page = dc.getRoot().getCurrentPage();
		if (!fitPage) {
			dcWidth = dc.getWidth() + offsetX;
			dcHeight = dc.getHeight() + offsetY;
		}
		else {
			int pageWidth = (int) page.getPageBounds().getWidth();
			int pageHeight = (int) page.getPageBounds().getHeight();
			dcWidth = pageWidth + offsetX;
			dcHeight = pageHeight + offsetY;
		}
		double scaleX = width / dcWidth;
		double scaleY = height / dcHeight;

//		int translateX = -dc.getBounds().x;
//		int translateY = -dc.getBounds().y;
		int translateX = 0;
		int translateY = 0;
		if (!fitPage) {
			translateX = -dc.getBounds().x;
			translateY = -dc.getBounds().y;
		}
		else {
			translateX = -page.getPageBounds().getBounds().x;
			translateY = -page.getPageBounds().getBounds().y;
		}

		translateX += (offsetX / 2);
		translateY += (offsetY / 2);
		double scale = Math.min(scaleX, scaleY);
		if (centered) {
			if (scaleX > scaleY) {
				double realWidth = (width) / scale;
				double dx = (realWidth - dcWidth) / 2;
				translateX += dx;
			}
			if (scaleY > scaleX) {
				double realHeight = (height / scale);
				double dy = (realHeight - dcHeight) / 2;
				translateY += dy;
			}
		}
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bi.createGraphics();
		// TODO set bgColor in Model
		g2d.setPaint(Color.WHITE);
		g2d.fillRect(0, 0, width, height);

		RenderingHints rh = g2d.getRenderingHints();
		RenderingHintsManager.setRenderMode(rh, renderQuality);
		g2d.setRenderingHints(rh);
		g2d.scale(scale, scale);
		g2d.translate(translateX, translateY);

		if (renderModeMan != null)
			dc.setRenderModeManager(renderModeMan);
		dc.setRenderMode(renderMode);
		J2DRenderContext rc = (J2DRenderContext) dc.getRenderer().getRenderContext(J2DRenderContext.RENDER_CONTEXT_TYPE_JAVA2D);
		// if RootDrawComponent hide pageBounds
		Map<PageDrawComponent, Boolean> page2ShowBounds = new HashMap<PageDrawComponent, Boolean>();
		if (dc instanceof RootDrawComponent)
		{
			RootDrawComponent root = (RootDrawComponent) dc;
			for (Iterator<DrawComponent> iter = root.getDrawComponents().iterator(); iter.hasNext();) {
				DrawComponent drawComponent = iter.next();
				if (drawComponent instanceof PageDrawComponent) {
					PageDrawComponent page1 = (PageDrawComponent) drawComponent;
					page2ShowBounds.put(page1, page1.isShowPageBounds());
					page1.setShowPageBounds(false);
				}
			}
		}

		rc.paint(dc, g2d);

		for (Map.Entry<PageDrawComponent, Boolean> entry : page2ShowBounds.entrySet()) {
			entry.getKey().setShowPageBounds(entry.getValue());
		}
		return bi;
	}

	/**
	 * renders the given {@link DrawComponent} into a {@link BufferedImage}
	 * of the given size
	 *
	 * @param dc the {@link DrawComponent} to render to image
	 * @param width the width of the output image in pixels
	 * @param height the height of the output image in pixels
	 * @param offsetX the offset in x-direction in model coordinates
	 * @param offsetY the ofset in y-direction in model coordinates
	 * @param renderQuality determines the quality of Rendering, valid values are
	 * {@link RenderingHintsManager#RENDER_MODE_DEFAULT},
	 * {@link RenderingHintsManager#RENDER_MODE_QUALITY},
	 * {@link RenderingHintsManager#RENDER_MODE_SPEED}
	 * @param renderMode the renderMode of the {@link DrawComponent}
	 * @param centered determines if the model should be centered in the image
	 * @param renderModeMan the RenderModeManager which contains the registered renderModes
	 * @return a {@link BufferedImage} which shows the rendered DrawComponent
	 *
	 * @see DrawComponent#getRenderMode()
	 */
	public static BufferedImage getModelAsImage(DrawComponent dc, int width, int height,
			int offsetX, int offsetY, int renderQuality, String renderMode, boolean centered,
			RenderModeManager renderModeMan)
	{
		return getModelAsImage(dc, width, height, offsetX, offsetY, renderQuality,
				renderMode, centered, renderModeMan, false);
	}

	/**
	 * renders the given {@link DrawComponent} into a {@link BufferedImage}
	 * of the given size
	 *
	 * @param dc the {@link DrawComponent} to render to image
	 * @param width the width of the output image in pixels
	 * @param height the height of the output image in pixels
	 * @param renderMode the renderMode of the {@link DrawComponent}
	 * @param renderModeMan the RenderModeManager which contains the registered renderModes
	 * @return a {@link BufferedImage} which shows the rendered DrawComponent
	 *
	 * @see DrawComponent#getRenderMode()
	 * @see RenderConstants
	 * @see RenderModeManager
	 */
	public static BufferedImage getModelAsImage(DrawComponent dc, int width, int height,
			String renderMode, RenderModeManager renderModeMan)
	{
		return getModelAsImage(dc, width, height, 0, 0,
				RenderingHintsManager.RENDER_MODE_QUALITY,
				renderMode, false, renderModeMan);
	}

//	/**
//	 * renders the given {@link DrawComponent} into a {@link BufferedImage}
//	 * of the given size
//	 *
//	 * @param dc the {@link DrawComponent} to render to image
//	 * @param width the width of the output image in pixels
//	 * @param height the height of the output image in pixels
//	 * @param renderMode the renderMode of the {@link DrawComponent}
//	 * @param centered determines if the model should be centered in the image
//	 * @return a {@link BufferedImage} which shows the rendered DrawComponent
//	 *
//	 * @see DrawComponent#getRenderMode()
//	 */
//	public static BufferedImage getModelAsImage(DrawComponent dc, int width, int height, String renderMode, boolean centered)
//	{
//		return getModelAsImage(dc, width, height, 0, 0,
//				RenderingHintsManager.RENDER_MODE_QUALITY,
//				centered, renderMode, RenderConstants.DEFAULT_MODE, null);
//	}

	/**
	 * renders the given {@link DrawComponent} into a {@link BufferedImage}
	 * of the given size
	 *
	 * @param dc the {@link DrawComponent} to render to image
	 * @param width the width of the output image in pixels
	 * @param height the height of the output image in pixels
	 * @param offsetX the offset in x-direction in model coordinates
	 * @param offsetY the ofset in y-direction in model coordinates
	 * @param centered determines if the model should be centered in the image
	 * @return a {@link BufferedImage} which shows the rendered DrawComponent
	 */
	public static BufferedImage getModelAsImage(DrawComponent dc, int width, int height,
			int offsetX, int offsetY, boolean centered)
	{
		return getModelAsImage(dc, width, height, offsetX, offsetY,
				RenderingHintsManager.RENDER_MODE_QUALITY,
				RenderConstants.DEFAULT_MODE, centered, null);
	}

	/**
	 * renders the given {@link DrawComponent} into a {@link BufferedImage}
	 * of the given size
	 *
	 * @param dc the {@link DrawComponent} to render to image
	 * @param width the width of the output image in pixels
	 * @param height the height of the output image in pixels
	 * @param centered determines if the model should be centered in the image
	 * @return a {@link BufferedImage} which shows the rendered DrawComponent
	 */
	public static BufferedImage getModelAsImage(DrawComponent dc, int width, int height,
			boolean centered)
	{
		return getModelAsImage(dc, width, height, 0, 0,
				RenderingHintsManager.RENDER_MODE_QUALITY,
				RenderConstants.DEFAULT_MODE, centered, null);
	}

	/**
	 * renders the given {@link DrawComponent} into a {@link BufferedImage}
	 * of the given size
	 *
	 * @param dc the {@link DrawComponent} to render to image
	 * @param width the width of the output image in pixels
	 * @param height the height of the output image in pixels
	 * @return a {@link BufferedImage} which shows the rendered DrawComponent
	 */
	public static BufferedImage getModelAsImage(DrawComponent dc, int width, int height)
	{
		return getModelAsImage(dc, width, height, 0, 0,
				RenderingHintsManager.RENDER_MODE_QUALITY,
				RenderConstants.DEFAULT_MODE, true, null);
	}

	/**
	 * renders the given {@link DrawComponent} to image of the given size and
	 * puts it into the given {@link OutputStream}
	 *
	 * @param dc the DrawComponent to render
	 * @param width the width of the output image
	 * @param height the height of the output image
	 * @param formatName the fileExtension of the imageFormat (e.g. jpg, png, ...)
	 * @param out the {@link OutputStream} to stream the image to
	 * @param renderMode the renderMode of the {@link DrawComponent}
	 * @param renderModeMan the RenderModeManager which contains the registered renderModes
	 * @throws IOException
	 *
	 * @see DrawComponent#getRenderMode()
	 * @see RenderConstants
	 * @see RenderModeManager
	 */
	public static void writeModelAsImage(DrawComponent dc, int width, int height,
			String formatName, OutputStream out, String renderMode, RenderModeManager renderModeMan)
	throws IOException
	{
		long start = System.currentTimeMillis();
		BufferedImage bi = getModelAsImage(dc, width, height, renderMode, renderModeMan);
		ImageIO.write(bi, formatName, out);
		long duration = System.currentTimeMillis() - start;
		bi.flush();
		bi = null;
		System.out.println("writeModelAsImage took "+duration+" ms!");
	}

	/**
	 * renders the given {@link DrawComponent} to image of the given size and
	 * save the image into the given File
	 *
	 * @param dc the DrawComponent to render
	 * @param width the width of the output image
	 * @param height the height of the output image
	 * @param formatName the fileExtension of the imageFormat (e.g. jpg, png, ...)
	 * @param output the output File where to save the image
	 * @param renderMode the renderMode of the {@link DrawComponent}
	 * @param renderModeMan the RenderModeManager which contains the registered renderModes
	 * @throws IOException
	 *
	 * @see DrawComponent#getRenderMode()
	 * @see RenderConstants
	 * @see RenderModeManager
	 */
	public static void writeModelAsImage(DrawComponent dc, int width, int height,
			String formatName, File output, String renderMode, RenderModeManager renderModeMan)
	throws IOException
	{
		BufferedImage bi = getModelAsImage(dc, width, height, renderMode, renderModeMan);
		ImageIO.write(bi, formatName, output);
		bi.flush();
		bi = null;
	}

	/**
	 * reads an Editor2D File and renders it to a Image with the given size and imageFormat
	 * into the given {@link OutputStream}
	 *
	 * @param in the {@link InputStream} to read the Editor2D file from
	 * @param width the width of the output image
	 * @param height the height of the output image
	 * @param formatName the fileExtension of the imageFormat (e.g. jpg, png, ...)
	 * @param out the {@link OutputStream} to stream the image to
	 * @param renderMode the renderMode of the {@link DrawComponent}
	 * @param renderModeMan the RenderModeManager which contains the registered renderModes

	 * @throws IOException
	 */
	public static void readEditorModelAndWriteAsImage(InputStream in, int width, int height,
			String formatName, OutputStream out, String renderMode, RenderModeManager renderModeMan)
	throws IOException
	{
		XStreamFilter xStreamFilter = new XStreamFilter();
		DrawComponent dc = xStreamFilter.read(in);
		writeModelAsImage(dc, width, height, formatName, out, renderMode, renderModeMan);
	}

	/**
	 * reads an Editor2D File and renders it to a Image with the given size and imageFormat
	 * into the given File
	 *
	 * @param in the {@link InputStream} to read the Editor2D file from
	 * @param width the width of the output image
	 * @param height the height of the output image
	 * @param formatName the fileExtension of the imageFormat (e.g. jpg, png, ...)
	 * @param output the File to save the image to
	 * @throws IOException
	 */
	public static void readEditorModelAndWriteAsImage(InputStream in, int width, int height,
			String formatName, File output, String renderMode, RenderModeManager renderModeMan)
	throws IOException
	{
		XStreamFilter xStreamFilter = new XStreamFilter();
		DrawComponent dc = xStreamFilter.read(in);
		writeModelAsImage(dc, width, height, formatName, output, renderMode, renderModeMan);
	}

	public static void main(String args[])
	{
		String filePath = "C:\\Dokumente und Einstellungen\\daniel\\Eigene Dateien\\Ipanema Sun\\Unbennant 1.e2d";
		String fileoutputpath = "C:\\Dokumente und Einstellungen\\daniel\\Eigene Dateien\\Ipanema Sun\\Unbennant 1.jpg";
		try {
			FileInputStream fis = new FileInputStream(filePath);
			File out = new File(fileoutputpath);
			if (!out.exists())
				out.createNewFile();
			FileOutputStream fos = new FileOutputStream(out);
			readEditorModelAndWriteAsImage(fis, 100, 100, "jpg", fos, RenderConstants.DEFAULT_MODE, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
