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
package org.nightlabs.editor2d.composite;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.holongate.j2d.IPaintable;
import org.holongate.j2d.J2DCanvas;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.form.XFormToolkit;
import org.nightlabs.base.form.XFormToolkit.TOOLKIT_MODE;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.j2dswt.ImagePaintable;
import org.nightlabs.editor2d.j2dswt.RenderedImagePaintable;
import org.nightlabs.editor2d.util.ImageUtil;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class ImagePreviewComposite 
extends XComposite 
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(ConvertImageComposite.class);

	/**
	 * @param parent
	 * @param style
	 */
	public ImagePreviewComposite(BufferedImage originalImage, Composite parent, int style, TOOLKIT_MODE toolkitMode) 
	{
		super(parent, style);
		this.originalImage = originalImage;
		this.toolkitMode = toolkitMode;
		createComposite(this);
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 * @param cols
	 */
	public ImagePreviewComposite(BufferedImage originalImage, Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode, TOOLKIT_MODE toolkitMode) 
	{
		super(parent, style, layoutMode, layoutDataMode);
		this.originalImage = originalImage;
		convertImage = ImageUtil.cloneImage(originalImage);		
		this.toolkitMode = toolkitMode;
		createComposite(this);
	}

	private TOOLKIT_MODE toolkitMode = TOOLKIT_MODE.COMPOSITE;	
	private XFormToolkit toolkit = null;
	private BufferedImage originalImage = null;
	public BufferedImage getOriginalImage() {
		return originalImage;
	}
	private BufferedImage convertImage = null;
	public BufferedImage getConvertImage() {
		return convertImage;
	}
	public void setConvertImage(BufferedImage convertImage) {
		this.convertImage = convertImage;
		convertPaintable.setImage(convertImage);
		rescaleImages();
	}
	
	private ScrolledComposite originalSC = null;
	private ScrolledComposite convertSC = null;
	private J2DCanvas originalCanvas = null;
	private J2DCanvas convertCanvas = null;	
	private RenderedImagePaintable originalPaintable = null;
	private RenderedImagePaintable convertPaintable = null;
//	private ImagePaintable originalPaintable = null;
//	private ImagePaintable convertPaintable = null;	
	
	protected void createComposite(Composite parent) 
	{
		toolkit = new XFormToolkit(Display.getCurrent());
		toolkit.setCurrentMode(toolkitMode);
		
		Composite comp = toolkit.createComposite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, true));
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Group originalGroup = toolkit.createGroup(comp, SWT.NONE, 
				EditorPlugin.getResourceString("convertImage.group.originalImage"));
		originalGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		originalGroup.setLayout(new GridLayout());
		originalSC = new ScrolledComposite(originalGroup, SWT.H_SCROLL | SWT.V_SCROLL);
		originalSC.setExpandHorizontal(true);		
		originalSC.setExpandVertical(true);
		originalSC.setLayoutData(new GridData(GridData.FILL_BOTH));
		originalSC.setLayout(new GridLayout());
		originalPaintable = new RenderedImagePaintable(originalImage);		
//		originalPaintable = new ImagePaintable(originalImage);
		originalCanvas = new J2DCanvas(originalSC, originalPaintable);
		originalCanvas.setLayoutData(new GridData(GridData.FILL_BOTH));
		originalSC.setContent(originalCanvas);
		originalSC.setMinSize(originalCanvas.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		Group convertGroup = toolkit.createGroup(comp, SWT.NONE, 
				EditorPlugin.getResourceString("convertImage.group.convertImage"));
		convertGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		convertGroup.setLayout(new GridLayout());
		convertSC = new ScrolledComposite(convertGroup, SWT.H_SCROLL | SWT.V_SCROLL);
		convertSC.setExpandHorizontal(true);		
		convertSC.setExpandVertical(true);		
		convertSC.setLayoutData(new GridData(GridData.FILL_BOTH));
		convertSC.setLayout(new GridLayout());		
		convertPaintable = new RenderedImagePaintable(convertImage);		
//		convertPaintable = new ImagePaintable(convertImage);
		convertCanvas = new J2DCanvas(convertSC, convertPaintable);
		convertCanvas.setLayoutData(new GridData(GridData.FILL_BOTH));
		convertSC.setContent(convertCanvas);
		convertSC.setMinSize(convertCanvas.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		convertCanvas.addControlListener(resizeListener);
		originalSC.getHorizontalBar().addSelectionListener(horizontalScrollListener);
		originalSC.getVerticalBar().addSelectionListener(verticalScrollListener);
		convertSC.getHorizontalBar().addSelectionListener(horizontalScrollListener);
		convertSC.getVerticalBar().addSelectionListener(verticalScrollListener);
		
		addDisposeListener(disposeListener);
	}
	
	private SelectionListener horizontalScrollListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
			repaintCanvas();
		}	
	};
	
	private SelectionListener verticalScrollListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
			repaintCanvas();
		}	
	};	
	
	public void repaintCanvas() 
	{
		originalCanvas.repaint();
		convertCanvas.repaint();		
	}	
	
	public void repaintOriginal() {
		originalCanvas.repaint();
	}

	public void repaintConvert() {
		convertCanvas.repaint();
	}
	
	private float scale = 1.0f;
	private ControlListener resizeListener = new ControlAdapter()
	{	
		public void controlResized(ControlEvent e) 
		{
			org.eclipse.swt.graphics.Rectangle canvasSize = originalCanvas.getClientArea();
			float originalWidth = originalImage.getWidth();
			float originalHeight = originalImage.getHeight();
			float newWidth = canvasSize.width;
			float newHeight = canvasSize.height;
			float scaleX = 1;
			float scaleY = 1;
			
			if (originalWidth != 0 && newWidth != 0)
				scaleX = newWidth / originalWidth;
			if (originalHeight != 0 && newWidth != 0)
				scaleY = newHeight / originalHeight;
			
			float tmpScale = Math.min(scaleX, scaleY);
			if (tmpScale != 1)
				scale = tmpScale; 				
			
			rescaleOp = null;
			logger.debug("Canvas resized!");
			
			rescaleImages();
		}	
	};	
	
//	private Image originalScale = null;
//	private Image convertScale = null;
	private BufferedImage originalScale = null;
	private BufferedImage convertScale = null;
	
	private boolean fitImage = true;
	public void setFitImage(boolean b) 
	{
		this.fitImage = b;		
		rescaleImages();	
		layoutScrollBars();		
	}
	public boolean isFitImage() {
		return fitImage;
	}
		
	protected void rescaleImages() 
	{
		long start = System.currentTimeMillis();
				
		if (fitImage) 		
		{	
			final int scaledWidth = (int) ((float)originalImage.getWidth() * scale);
			final int scaledHeight = (int) ((float)originalImage.getHeight() * scale);	
			if (logger.isDebugEnabled()) {
				logger.debug("scaledWidth = "+scaledWidth);
				logger.debug("scaledHeight = "+scaledHeight);
			}
			originalScale = scaleImage(originalImage, scaledWidth, scaledHeight);
			originalPaintable.setImage(originalScale);
			convertScale = scaleImage(convertImage, scaledWidth, scaledHeight);			
			convertPaintable.setImage(convertScale);			
		}
		else {
			originalPaintable.setImage(originalImage);
			convertPaintable.setImage(convertImage);			
		}
		
		if (logger.isDebugEnabled()) {
			long end = System.currentTimeMillis() - start;
			logger.debug("rescaleImages took "+end+" ms!");			
		}
		repaintCanvas();		
	}
	
//	private int scaleHint = Image.SCALE_DEFAULT;	
//	protected Image scaleImage(BufferedImage img, int width, int height) 
//	{
//		return img.getScaledInstance(width, height, scaleHint);
//	}
	
//	protected BufferedImage scaleImage(BufferedImage img, int width, int height) 
//	{
//		long start = System.currentTimeMillis();
//		int type = BufferedImage.TYPE_INT_ARGB; 
//		BufferedImage bi = new BufferedImage(width, height, type);		
//		Graphics2D g2d = bi.createGraphics();
//		g2d.drawImage(img, 0, 0, width, height, null);
//		g2d.dispose();
//		if (logger.isDebugEnabled()) {
//			long end = System.currentTimeMillis() - start;
//			logger.debug("scaleImage took "+end+" ms!");
//		}
//		return bi;
//	}

	protected BufferedImage scaleImage(BufferedImage img, int width, int height) 
	{
		logger.debug("scale = "+scale);
		if (rescaleOp == null)
			rescaleOp = createRescaleOp(scale);
		
		return rescaleOp.filter(img, null);
	}
		
	private int scaleInterpolationType = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;	
	private AffineTransformOp rescaleOp = null;		
	protected AffineTransformOp createRescaleOp(float scale) 
	{
		AffineTransform at = new AffineTransform();
		at.scale(scale, scale);
		logger.debug("image scale factor = "+scale);
		return new AffineTransformOp(at, scaleInterpolationType);		
	}
		
	protected void layoutScrollBars() 
	{
		Point size = null;
		if (!fitImage)
			size = new Point(originalImage.getWidth(), originalImage.getHeight());			
		else
			size = new Point(originalScale.getWidth(null), originalScale.getHeight(null));			
		
		originalCanvas.computeSize(size.x, size.y);
		convertCanvas.computeSize(size.x, size.y);		
		originalSC.setMinSize(size);		
		convertSC.setMinSize(size);				
		originalCanvas.layout(true);		
		convertCanvas.layout(true);		
	}		
	
	private DisposeListener disposeListener = new DisposeListener()
	{	
		public void widgetDisposed(DisposeEvent e) 
		{
			convertCanvas.removeControlListener(resizeListener);
			originalSC.getHorizontalBar().removeSelectionListener(horizontalScrollListener);
			originalSC.getVerticalBar().removeSelectionListener(verticalScrollListener);
			convertSC.getHorizontalBar().removeSelectionListener(horizontalScrollListener);
			convertSC.getVerticalBar().removeSelectionListener(verticalScrollListener);		
			
			originalImage = null;
			convertImage = null;
			originalScale = null;
			convertScale = null;
		}	
	};
	
}
