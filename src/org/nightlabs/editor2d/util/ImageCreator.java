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
import org.nightlabs.editor2d.render.RenderModeManager;
import org.nightlabs.editor2d.render.j2d.J2DRenderContext;

/**
 * Instances of this class create images from a {@link DrawComponent}. They can produce
 * instances of {@link BufferedImage} or write into an {@link OutputStream}.
 * <p>
 * To get a <code>BufferedImage</code> from a <code>DrawComponent</code>, you
 * create a throw-away-instance of <code>ImageCreator</code> and make use of
 * the {@link #createImage()} method like in this example:
 * <pre>
 * BufferedImage image = new ImageCreator(myDrawComponent, 800, 600).createImage();
 * </pre>
 * </p>
 * <p>
 * If you need to set additional properties for the rendering, you can configure
 * the <code>ImageCreator</code> before calling its <code>createImage()</code> method:
 * <pre>
 * ImageCreator ic = new ImageCreator(myDrawComponent, 800, 600);
 * ic.setOffsetX(300);
 * ic.setOffsetY(200);
 * BufferedImage image = ic.createImage();
 * </pre>
 * </p>
 * <p>
 * It is possible to reuse an instance of <code>ImageCreator</code> to produce multiple images.
 * Whenever the {@link #createImage()} or {@link #writeImage(String, OutputStream)} method is
 * called, it uses the properties that have been set before.
 * </p>
 * <p>
 * Instances of this class are <b>not thread-safe</b>!
 * </p>
 *
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author marco schulze - marco at nightlabs dot de
 */
public class ImageCreator
{
	private DrawComponent drawComponent;
	private int width = 1;
	private int height = 1;
	private int offsetX = 0;
	private int offsetY = 0;
	private int renderQuality = RenderingHintsManager.RENDER_MODE_DEFAULT;
	private String renderMode = null; // null causes the default renderer to be used.
	private RenderModeManager renderModeManager = null;
	private int imageType = BufferedImage.TYPE_INT_ARGB;
	private boolean centered = false;
	private boolean fitPage = false;

	/**
	 * Create an instance of <code>ImageCreator</code>. When using this constructor,
	 * you must call {@link #setDrawComponent(DrawComponent)} before being able to create
	 * an image.
	 */
	public ImageCreator() { }

	/**
	 * Create an instance of <code>ImageCreator</code>.
	 *
	 * @param in the stream to read data from. It is expected to be parsable by an {@link XStreamFilter}.
	 * @param width the width of the output image in pixels. Must be &gt; 0!
	 * @param height the height of the output image in pixels. Must be &gt; 0!
	 * @throws IOException if an error occurs while reading the data from the <code>InputStream</code>.
	 */
	public ImageCreator(InputStream in, int width, int height) throws IOException
	{
		XStreamFilter xStreamFilter = new XStreamFilter();
		this.drawComponent = xStreamFilter.read(in);
		this.setWidth(width);
		this.setHeight(height);
	}

	/**
	 * Create an instance of <code>ImageCreator</code>.
	 *
	 * @param drawComponent the {@link DrawComponent} to be rendered into an image.
	 * @param width the width of the output image in pixels. Must be &gt; 0!
	 * @param height the height of the output image in pixels. Must be &gt; 0!
	 */
	public ImageCreator(DrawComponent drawComponent, int width, int height) {
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
	 * Get the offset in x-direction in model coordinates.
	 * @return the offset in x-direction in model coordinates.
	 */
	public int getOffsetX() {
		return offsetX;
	}
	/**
	 * Set the offset in x-direction in model coordinates.
	 * @param offsetX the offset in x-direction in model coordinates.
	 */
	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}
	/**
	 * Get the offset in y-direction in model coordinates.
	 * @return the offset in y-direction in model coordinates.
	 */
	public int getOffsetY() {
		return offsetY;
	}
	/**
	 * Set the offset in y-direction in model coordinates.
	 * @param offsetY the offset in y-direction in model coordinates.
	 */
	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
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
	 * Get the 'centered' flag. It determines, if the model should be centered in the image.
	 * @return the 'centered' flag.
	 */
	public boolean isCentered() {
		return centered;
	}
	/**
	 * Set the 'centered' flag. It determines, if the model should be centered in the image.
	 * @param centered the 'centered' flag.
	 */
	public void setCentered(boolean centered) {
		this.centered = centered;
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
	 * Get the flag that determines, if the drawComponent should fit the page. If it is <code>false</code>, the complete {@link DrawComponent} is fitted (fit all).
	 * @return whether to fit to the page.
	 */
	public boolean isFitPage() {
		return fitPage;
	}
	/**
	 * Set the flag that determines, if the drawComponent should fit the page.
	 * @param fitPage whether to fit to the page.
	 * @see #isFitPage()
	 */
	public void setFitPage(boolean fitPage) {
		this.fitPage = fitPage;
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
	 * Renders the given {@link DrawComponent} into a {@link BufferedImage}.
	 *
	 * @return a {@link BufferedImage} which shows the rendered {@link DrawComponent}.
	 */
	public BufferedImage createImage()
	{
		if (drawComponent == null)
			throw new IllegalStateException("drawComponent is null! Call setDrawComponent(...) first!");

		double dcWidth;
		double dcHeight;
		// TODO determine the page where the drawComponent is contained in
		PageDrawComponent page = drawComponent.getRoot().getCurrentPage();
		if (!fitPage) {
			dcWidth = drawComponent.getWidth() + offsetX;
			dcHeight = drawComponent.getHeight() + offsetY;
		}
		else {
			int pageWidth = (int) page.getPageBounds().getWidth();
			int pageHeight = (int) page.getPageBounds().getHeight();
			dcWidth = pageWidth + offsetX;
			dcHeight = pageHeight + offsetY;
		}
		double scaleX = width / dcWidth;
		double scaleY = height / dcHeight;

//		int translateX = -drawComponent.getBounds().x;
//		int translateY = -drawComponent.getBounds().y;
		int translateX = 0;
		int translateY = 0;
		if (!fitPage) {
			translateX = -drawComponent.getBounds().x;
			translateY = -drawComponent.getBounds().y;
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

		BufferedImage bi = new BufferedImage(width, height, imageType);
		Graphics2D g2d = bi.createGraphics();
		// TODO set bgColor in Model
		g2d.setPaint(Color.WHITE);
		g2d.fillRect(0, 0, width, height);

		RenderingHints rh = g2d.getRenderingHints();
		RenderingHintsManager.setRenderMode(rh, renderQuality);
		g2d.setRenderingHints(rh);
		g2d.scale(scale, scale);
		g2d.translate(translateX, translateY);

		if (renderModeManager != null)
			drawComponent.setRenderModeManager(renderModeManager);
		drawComponent.setRenderMode(renderMode);
		J2DRenderContext rc = (J2DRenderContext) drawComponent.getRenderer().getRenderContext(J2DRenderContext.RENDER_CONTEXT_TYPE_JAVA2D);
		// if RootDrawComponent hide pageBounds
		Map<PageDrawComponent, Boolean> page2ShowBounds = new HashMap<PageDrawComponent, Boolean>();
		if (drawComponent instanceof RootDrawComponent)
		{
			RootDrawComponent root = (RootDrawComponent) drawComponent;
			for (Iterator<DrawComponent> iter = root.getDrawComponents().iterator(); iter.hasNext();) {
				DrawComponent drawComponent = iter.next();
				if (drawComponent instanceof PageDrawComponent) {
					PageDrawComponent page1 = (PageDrawComponent) drawComponent;
					page2ShowBounds.put(page1, page1.isShowPageBounds());
					page1.setShowPageBounds(false);
				}
			}
		}

		rc.paint(drawComponent, g2d);

		for (Map.Entry<PageDrawComponent, Boolean> entry : page2ShowBounds.entrySet()) {
			entry.getKey().setShowPageBounds(entry.getValue());
		}
		return bi;
	}

	/**
	 * Renders the given {@link DrawComponent} to an image of the given size and
	 * writes it (encoded according to the specified <code>format</code>) into the given {@link OutputStream}.
	 *
	 * @param formatName the file-extension of the image-format (e.g. "jpg", "png", ...).
	 * @param out the {@link OutputStream} to write the encoded image to.
	 * @throws IOException if writing to the {@link OutputStream} fails.
	 */
	public void writeImage(String formatName, OutputStream out)
	throws IOException
	{
		if (drawComponent == null)
			throw new IllegalStateException("drawComponent is null! Call setDrawComponent(...) first!");

		long start = System.currentTimeMillis();
		BufferedImage bi = createImage();
		ImageIO.write(bi, formatName, out);
		long duration = System.currentTimeMillis() - start;
		bi.flush();
		bi = null;
		System.out.println("writeModelAsImage took "+duration+" ms!");
	}

}
