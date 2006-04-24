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

import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.RescaleOp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.holongate.j2d.J2DCanvas;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.form.XFormToolkit;
import org.nightlabs.base.form.XFormToolkit.TOOLKIT_MODE;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.j2dswt.BufferedImagePaintable;
import org.nightlabs.editor2d.util.ImageUtil;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class ConvertImageComposite 
extends XComposite 
{
	public static final Logger LOGGER = Logger.getLogger(ConvertImageComposite.class);
	
	/**
	 * @param parent
	 * @param style
	 */
	public ConvertImageComposite(Composite parent, int style, 
			BufferedImage bi, TOOLKIT_MODE toolkitMode) 
	{
		super(parent, style);
		init(bi, toolkitMode);
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public ConvertImageComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode, 
			BufferedImage bi, TOOLKIT_MODE toolkitMode) 
	{
		super(parent, style, layoutMode, layoutDataMode);
		init(bi, toolkitMode);		
	}
		
	protected void init(BufferedImage bi, TOOLKIT_MODE toolkitMode) 
	{
		originalImage = bi;
		convertImage = ImageUtil.cloneImage(originalImage);
		initICCProfiles();		
		initRenderHints();
		createComposite(this);		
	}
	
	protected void initRenderHints() 
	{
		renderHints = new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, 
				RenderingHints.VALUE_COLOR_RENDER_DEFAULT);
		renderHints.put(RenderingHints.KEY_DITHERING, 
				RenderingHints.VALUE_DITHER_DEFAULT);
	}
	
	protected XFormToolkit toolkit = null;
	protected TOOLKIT_MODE toolkitMode = TOOLKIT_MODE.COMPOSITE;
	protected BufferedImage originalImage = null;
	public BufferedImage getOriginalImage() {
		return originalImage;
	}
	protected BufferedImage convertImage = null;	
	public BufferedImage getConvertedImage() {
		return convertImage;
	}	
		
	protected J2DCanvas originalCanvas = null;
	protected J2DCanvas convertCanvas = null;
	protected BufferedImagePaintable originalPaintable = null;
	protected BufferedImagePaintable convertPaintable = null;
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
		originalPaintable = new BufferedImagePaintable(originalImage);
		originalCanvas = new J2DCanvas(originalGroup, originalPaintable);
		originalCanvas.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Group convertGroup = toolkit.createGroup(comp, SWT.NONE, 
				EditorPlugin.getResourceString("convertImage.group.convertImage"));
		convertGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		convertGroup.setLayout(new GridLayout());
		convertPaintable = new BufferedImagePaintable(convertImage);
		convertCanvas = new J2DCanvas(convertGroup, convertPaintable);
		convertCanvas.setLayoutData(new GridData(GridData.FILL_BOTH));
		// JVM crashes when used
		convertCanvas.addControlListener(resizeListener);
		
		Group convertDetails = toolkit.createGroup(parent, SWT.NONE, 
				EditorPlugin.getResourceString("convertImage.group.convertOptions"));
		convertDetails.setLayout(new GridLayout(3, false));
		convertDetails.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		iccProfileCombo = toolkit.createCombo(convertDetails, SWT.BORDER);
		iccProfileCombo.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_CENTER, GridData.VERTICAL_ALIGN_BEGINNING, true, false));
		iccProfileCombo.addSelectionListener(iccProfileListener);
		populateICCCombo(iccProfileCombo);
		
		Composite colorComp = toolkit.createXComposite(convertDetails, SWT.NONE);
		colorDefaultButton = toolkit.createButton(colorComp, 
				EditorPlugin.getResourceString("convertImage.colorButton.default"), SWT.RADIO);
		colorQualityButton = toolkit.createButton(colorComp, 
				EditorPlugin.getResourceString("convertImage.colorButton.quality"), SWT.RADIO);
		colorSpeedButton = toolkit.createButton(colorComp, 
				EditorPlugin.getResourceString("convertImage.colorButton.speed"), SWT.RADIO);
		colorDefaultButton.addSelectionListener(colorButtonListener);
		colorQualityButton.addSelectionListener(colorButtonListener);
		colorSpeedButton.addSelectionListener(colorButtonListener);
		colorDefaultButton.setSelection(true);

		Composite ditherComp = toolkit.createXComposite(convertDetails, SWT.NONE);
		ditherDefaultButton = toolkit.createButton(ditherComp, 
				EditorPlugin.getResourceString("convertImage.ditherButton.default"), SWT.RADIO);
		ditherEnableButton = toolkit.createButton(ditherComp, 
				EditorPlugin.getResourceString("convertImage.ditherButton.enabled"), SWT.RADIO);
		ditherDisableButton = toolkit.createButton(ditherComp, 
				EditorPlugin.getResourceString("convertImage.ditherButton.disabled"), SWT.RADIO);
		ditherDefaultButton.addSelectionListener(ditherButtonListener);
		ditherDisableButton.addSelectionListener(ditherButtonListener);
		ditherEnableButton.addSelectionListener(ditherButtonListener);
		ditherDefaultButton.setSelection(true);
		
		originalCanvas.repaint();
		convertCanvas.repaint();
	}
	
	protected Combo iccProfileCombo = null;
	protected Map<String, ICC_Profile> name2ICCProfile = new HashMap<String, ICC_Profile>();
//	protected void initICCProfiles() 
//	{
//		ICC_Profile bw = ICC_Profile.getInstance(ICC_Profile.icSigSpace2CLR); 
//		name2ICCProfile.put(EditorPlugin.getResourceString("convertImage.IICProfile.blackwhite"), bw);
//		profile = bw;
//		
//		ICC_Profile grey = ICC_Profile.getInstance(ICC_Profile.icSigGrayData);		
//		name2ICCProfile.put(EditorPlugin.getResourceString("convertImage.IICProfile.grayscale"), grey);
//		
//		ICC_Profile rgb = ICC_Profile.getInstance(ICC_Profile.icSigRgbData); 
//		name2ICCProfile.put(EditorPlugin.getResourceString("convertImage.IICProfile.rgb"), rgb);		
//		
//		ICC_Profile cmyk = ICC_Profile.getInstance(ICC_Profile.icSigCmykData);
//		name2ICCProfile.put(EditorPlugin.getResourceString("convertImage.IICProfile.cmyk"), cmyk);				
//	}
	protected void initICCProfiles() 
	{
		ICC_Profile bw = null;		
		name2ICCProfile.put(EditorPlugin.getResourceString("convertImage.IICProfile.blackwhite"), bw);
		profile = bw;
		
		ICC_Profile grey = ICC_Profile.getInstance(ColorSpace.CS_GRAY);		
		name2ICCProfile.put(EditorPlugin.getResourceString("convertImage.IICProfile.grayscale"), grey);
		
		ICC_Profile rgb = ICC_Profile.getInstance(ColorSpace.CS_sRGB); 
		name2ICCProfile.put(EditorPlugin.getResourceString("convertImage.IICProfile.rgb"), rgb);				
	}
	
	protected void populateICCCombo(Combo c) 
	{
		for (Iterator<String> it = name2ICCProfile.keySet().iterator(); it.hasNext(); ) {
			String profileName = it.next();
			iccProfileCombo.add(profileName);
		}
	}
	
	protected Button colorDefaultButton = null;
	protected Button colorQualityButton = null;
	protected Button colorSpeedButton = null;
	
	protected Button ditherDefaultButton = null;
	protected Button ditherEnableButton = null;
	protected Button ditherDisableButton = null;	
	
	protected RenderingHints renderHints = null;
	
	protected SelectionListener colorButtonListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
			Button b = (Button) e.getSource();
			if (b.equals(colorDefaultButton)) {
				renderHints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_DEFAULT);
			} else if (b.equals(colorQualityButton)) {
				renderHints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			} else if (b.equals(colorSpeedButton)) {
				renderHints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
			}
			refresh();
		}	
	};
	
	protected SelectionListener ditherButtonListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
			Button b = (Button) e.getSource();
			if (b.equals(ditherDefaultButton)) {
				renderHints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DEFAULT);
			} else if (b.equals(ditherEnableButton)) {
				renderHints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
			} else if (b.equals(ditherDisableButton)) {
				renderHints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
			}
			refresh();
		}	
	};	
	
	protected ICC_Profile profile = null;
	protected SelectionListener iccProfileListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
			String name = iccProfileCombo.getItem(iccProfileCombo.getSelectionIndex());
			profile = name2ICCProfile.get(name);	
			refresh();
		}	
	};
	
	protected ColorConvertOp colorConvertOp = null;
	public ColorConvertOp getColorConvertOp() {
		return colorConvertOp;
	}
	
	protected void refresh() 
	{
		if (profile != null) 
		{
			LOGGER.debug("profile != null!");
			colorConvertOp = new ColorConvertOp(new ICC_ColorSpace(profile), renderHints);
			if (convertImage.getType() == BufferedImage.TYPE_BYTE_BINARY)
				convertImage = ImageUtil.cloneImage(originalImage);
			convertImage = colorConvertOp.filter(originalImage, convertImage);	
		}
		// if profile == null, Black White is used where no ICC_Profile exists for
		else {
			LOGGER.debug("profile == null!");
			convertImage = ImageUtil.cloneImage(originalImage, BufferedImage.TYPE_BYTE_BINARY);
			colorConvertOp = new ColorConvertOp(convertImage.getColorModel().getColorSpace(), renderHints);
		}
		
		logColorSpace(convertImage.getColorModel().getColorSpace());		
		
		if (rescaleOp != null) {
			BufferedImage originalScale = rescaleOp.filter(originalImage, null);
			originalPaintable.setImage(originalScale);
			BufferedImage convertScale = rescaleOp.filter(convertImage, null);
			convertPaintable.setImage(convertScale);
			
			originalCanvas.repaint();
			convertCanvas.repaint();			
		}
		else {
			convertPaintable.setImage(convertImage);
			convertCanvas.repaint();			
		}		
	}
		
	protected int scaleInterpolationType = AffineTransformOp.TYPE_BILINEAR;
	protected AffineTransformOp rescaleOp = null;	
	protected ControlListener resizeListener = new ControlAdapter()
	{	
		public void controlResized(ControlEvent e) 
		{
			Rectangle canvasSize = originalCanvas.getClientArea();
			float originalWidth = originalImage.getWidth();
			float originalHeight = originalImage.getHeight();
			float newWidth = canvasSize.width;
			float newHeight = canvasSize.height;
			float scaleX = 1;
			float scaleY = 1;
			
			if (originalWidth > newWidth)
				scaleX = newWidth / originalWidth;
			if (originalHeight > newHeight)
				scaleY = newHeight / originalHeight;
			
			float scale = Math.min(scaleX, scaleY);
			AffineTransform at = new AffineTransform();
			at.scale(scale, scale);
			rescaleOp = new AffineTransformOp(at, scaleInterpolationType);
			
			refresh();
		}	
	};	
	
	protected void logColorSpace(ColorSpace cs) 
	{
		LOGGER.debug("ColorSpace Class = "+cs.getClass());
		LOGGER.debug("ColorSpace Type = "+cs.getType());
		LOGGER.debug("ColorSpace number of components = "+cs.getNumComponents());
		for (int i=0; i<cs.getNumComponents(); i++) {
			LOGGER.debug("Name of component "+i+" = "+cs.getName(i));
		}
	}
}