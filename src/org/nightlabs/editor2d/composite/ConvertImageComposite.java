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
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.composite.CComboComposite;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.form.XFormToolkit;
import org.nightlabs.base.form.XFormToolkit.TOOLKIT_MODE;
import org.nightlabs.base.util.RCPUtil;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.image.BWDitheringColorConvertDelegate;
import org.nightlabs.editor2d.image.BlackWhiteColorConvertDelegate;
import org.nightlabs.editor2d.image.GrayscaleColorConvertDelegate;
import org.nightlabs.editor2d.image.RGBColorConvertDelegate;
import org.nightlabs.editor2d.image.RenderModeConstants;
import org.nightlabs.editor2d.image.RenderModeMetaData;
import org.nightlabs.editor2d.render.RenderConstants;
import org.nightlabs.editor2d.util.ImageUtil;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class ConvertImageComposite 
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
	
	private long start = 0;
	protected void init(BufferedImage bi, TOOLKIT_MODE toolkitMode) 
	{ 
		start = System.currentTimeMillis();
		originalImage = bi;
		convertImage = ImageUtil.cloneImage(originalImage);
		initColorModels();		
		createComposite(this);		
		addDisposeListener(disposeListener);
	}
		
	private XFormToolkit toolkit = null;
	private TOOLKIT_MODE toolkitMode = TOOLKIT_MODE.COMPOSITE;
	
	private BufferedImage originalImage = null;
	public BufferedImage getOriginalImage() {
		return originalImage;
	}
	private BufferedImage convertImage = null;	
	public BufferedImage getConvertedImage() {
		return convertImage;
	}	
	
	private ImagePreviewComposite previewComp = null;
	private Button fitImageButton = null;
	private Button ditherButton = null;
	protected void createComposite(Composite parent) 
	{
		toolkit = new XFormToolkit(Display.getCurrent());
		toolkit.setCurrentMode(toolkitMode);
				
		previewComp = new ImagePreviewComposite(originalImage, parent, SWT.NONE, TOOLKIT_MODE.COMPOSITE);
			
		Composite optionsComp = toolkit.createXComposite(parent, SWT.NONE, 
				LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		optionsComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		optionsComp.setLayout(new GridLayout(2, false));
		
		Label colorModeLabel = toolkit.createLabel(optionsComp, EditorPlugin.getResourceString("convertImage.colorModel.label"));		
		colorModelCombo = new CComboComposite<ColorModel>(colorModels, colorModelLabelProvider, optionsComp, SWT.NONE);
		colorModelCombo.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_CENTER, GridData.VERTICAL_ALIGN_BEGINNING, true, false));
		colorModelCombo.addSelectionListener(colorModelListener);

		Label imageScaleLabel = toolkit.createLabel(optionsComp, EditorPlugin.getResourceString("convertImage.fitImage.label"));
		fitImageButton = toolkit.createButton(optionsComp, "", SWT.CHECK);
		fitImageButton.setSelection(fitImage);
		fitImageButton.addSelectionListener(fitImageListener);

		Label ditherLabel = toolkit.createLabel(optionsComp, 
				EditorPlugin.getResourceString("convertImage.ditherButton.label"));
		ditherButton = toolkit.createButton(optionsComp, "", SWT.CHECK);
		ditherButton.setSelection(dithering);
		ditherButton.addSelectionListener(ditherButtonListener);
		
		detailParent = toolkit.createXComposite(parent, SWT.NONE, 
				LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		detailParent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		selectColorMode(rgb);
		colorModelCombo.selectElement(rgb);

		previewComp.repaintCanvas();
		
		long end = System.currentTimeMillis() - start;
		logger.debug("init took "+end+" ms!");
	}
			
	private boolean dithering = true;
	private SelectionListener ditherButtonListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
			dithering = !dithering;
			setDithering(dithering, true);
		}	
	};
	
	private Composite detailComp = null;
	private Composite detailParent = null;
		
	public static String DITHER_MODE_QUALITY = "errordiffusion"; 
	public static String DITHER_MODE_SPEED = "ordereddither";
	private String ditherMode = DITHER_MODE_QUALITY;

//	private KernelJAI ditherAlgorithm = KernelJAI.ERROR_FILTER_FLOYD_STEINBERG;	
	private String ditherAlgorithm = BWDitheringColorConvertDelegate.DITHER_ALGORITHM_FLOYD_STEINBERG;	
	private Button buttonFilterFloydSteinberg = null;
	private Button buttonFilterJarvis = null;
	private Button buttonFilterStucki = null;
	private Button buttonDitherMask441 = null;
	protected Composite createDitherDetail(Composite parent) 
	{		
		ExpandableComposite ec = toolkit.createExpandableComposite(parent, SWT.NONE);
		ec.setText(EditorPlugin.getResourceString("convertImage.ditherOptions.label"));
		detailComp = ec;				
		detailComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
		detailComp.setLayout(new GridLayout());		
		
		Composite comp = toolkit.createGroup(detailComp, SWT.NONE, "");
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		buttonFilterFloydSteinberg = toolkit.createButton(comp,
				EditorPlugin.getResourceString("convertImage.ditherMode.floydSteinberg.label"), SWT.RADIO);
		buttonFilterJarvis = toolkit.createButton(comp,
				EditorPlugin.getResourceString("convertImage.ditherMode.jarvis.label"), SWT.RADIO);
		buttonFilterStucki = toolkit.createButton(comp,
				EditorPlugin.getResourceString("convertImage.ditherMode.stucki.label"), SWT.RADIO);
		buttonDitherMask441 = toolkit.createButton(comp, 
				EditorPlugin.getResourceString("convertImage.ditherMode.441.label"), SWT.RADIO);
		
		buttonFilterFloydSteinberg.setSelection(true);
		
		buttonFilterFloydSteinberg.addSelectionListener(ditherDetailButtonListener);
		buttonFilterJarvis.addSelectionListener(ditherDetailButtonListener);
		buttonFilterStucki.addSelectionListener(ditherDetailButtonListener);
		buttonDitherMask441.addSelectionListener(ditherDetailButtonListener);
		
		ec.setClient(comp);
		ec.setExpanded(true);
		ec.addExpansionListener(expansionListener);		
		detailParent.layout(true);
		
		return detailComp;
	}
		
	private ExpansionAdapter expansionListener = new ExpansionAdapter() 
	{
		public void expansionStateChanged(ExpansionEvent e) 
		{
			// TODO: dont layout detailParent but change size of composite			
//			ConvertImageComposite.this.layout();			
			detailParent.layout(true);
		}		
	};
	
	private SelectionListener ditherDetailButtonListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) 
		{
			Button b = (Button) e.getSource();
			if (b.equals(buttonFilterFloydSteinberg)) {
				ditherMode = DITHER_MODE_QUALITY;
				ditherAlgorithm = BWDitheringColorConvertDelegate.DITHER_ALGORITHM_FLOYD_STEINBERG;
			} else if (b.equals(buttonFilterJarvis)) {
				ditherMode = DITHER_MODE_QUALITY;
				ditherAlgorithm = BWDitheringColorConvertDelegate.DITHER_ALGORITHM_JARVIS;
			} else if (b.equals(buttonFilterStucki)) {
				ditherMode = DITHER_MODE_QUALITY;
				ditherAlgorithm = BWDitheringColorConvertDelegate.DITHER_ALGORITHM_STUCKI;
			} else if (b.equals(buttonDitherMask441)) {
				ditherMode = DITHER_MODE_SPEED; 
//				ditherAlgorithm = BWDitheringColorConvertDelegate.DITHER_ALGORITHM_441;				
			} 
			else {
				throw new IllegalArgumentException("e.getSource() is an unknown object");
			}
			refresh();
		}	
	};
	
	private ColorModel bw = null;
	private ColorModel grey = null;
	private ColorModel rgb = null;
	private CComboComposite<ColorModel> colorModelCombo = null;
	private List<ColorModel> colorModels = null;
	protected void initColorModels() 
	{		
		colorModels = new LinkedList<ColorModel>();
		
		bw = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY),
					new int[] {2}, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		colorModels.add(bw);
		
		grey = ImageUtil.getColorModel(BufferedImage.TYPE_BYTE_GRAY);
		colorModels.add(grey);
		
		rgb = ImageUtil.getColorModel(BufferedImage.TYPE_INT_RGB);
		colorModels.add(rgb);
		
		colorModel = rgb;		
		
	}
	
	private LabelProvider colorModelLabelProvider = new LabelProvider() 
	{
		@Override
		public String getText(Object element) 
		{
			if (element != null && element instanceof ColorModel) 
			{
				if (element.equals(bw))
					return EditorPlugin.getResourceString("convertImage.colorModel.blackwhite");
				if (element.equals(grey))
					return EditorPlugin.getResourceString("convertImage.colorModel.grayscale");
				if (element.equals(rgb))
					return EditorPlugin.getResourceString("convertImage.colorModel.rgb");				
			}
			return super.getText(element);
		}		
	};
		
//	private RenderingHints renderHints = null;		
	private ColorModel colorModel = null;
	private SelectionListener colorModelListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) 
		{
			selectColorMode(colorModelCombo.getSelectedElement());
		}	
	};

	protected void setDithering(boolean dithering, boolean refresh) 
	{				
		RCPUtil.disposeAllChildren(detailParent);
		if (dithering)
			detailComp = createDitherDetail(detailParent);
		
		// TODO: dont layout detailParent but change size of composite
		detailParent.layout(true);
		if (refresh)
			refresh();
	}
	
	protected void selectColorMode(ColorModel cm) 
	{
//		colorModelCombo.selectElement(cm);
		colorModel = cm;
		boolean blackwhite = colorModel.equals(bw);
		ditherButton.setEnabled(blackwhite);
		setDithering(blackwhite, false);		
		refresh();
	}
		
	private boolean fitImage = true;
	private SelectionListener fitImageListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) 
		{
			fitImage = !fitImage;
			previewComp.setFitImage(fitImage);
		}	
	};
			
	private List<RenderModeMetaData> renderModeMetaDatas = new LinkedList<RenderModeMetaData>();
	public List<RenderModeMetaData> getRenderModeMetaDatas() {
		return renderModeMetaDatas;
	}
	
	protected void refresh() 
	{		
		logger.debug("refresh!");
		long start = System.currentTimeMillis();
				
		if (colorModel == bw) {			
			convertBlackWhite();						
		}
		else {
			if (convertImage.getType() == BufferedImage.TYPE_BYTE_BINARY)
				convertImage = ImageUtil.cloneImage(originalImage);
			RenderedImage img = colorConvertJDK(originalImage, colorModel);
			if (img != null)
				convertImage = convertToBufferedImage(img);
		}						
		previewComp.setConvertImage(convertImage);		
		
		long end = System.currentTimeMillis() - start;
		logger.debug("refresh took "+end+" ms!");
	}		
				
	private RenderedImage colorConvertJDK(BufferedImage original, ColorModel colorModel) 
	{
		long startTime = System.currentTimeMillis();		
		String renderDelegateClassName = getImageRenderDelegateClassName(colorModel);
		Set<String> supportedRenderModes = new HashSet<String>();
		supportedRenderModes.add(getRenderMode(colorModel));
		String id = getRenderModeMetaDataID(colorModel);		
		RenderModeMetaData renderModeMetaData = new RenderModeMetaData(id, supportedRenderModes, 
				renderDelegateClassName, null, true);
		
		renderModeMetaDatas.clear();
		renderModeMetaDatas.add(renderModeMetaData);
		
		String renderMode = getRenderMode(colorModel); 
		try {
			RenderedImage img = renderModeMetaData.getRendererDelegate().render(renderMode, null, original, renderModeMetaData);
			long end = System.currentTimeMillis() - startTime;
			logger.debug("Color Convert took "+end+" ms!");
			return img;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}		
	
	protected String getRenderMode(ColorModel cm) 
	{
		if (cm.equals(bw)) {
			return RenderConstants.BLACK_WHITE_MODE;
		} else if (cm.equals(grey)) {
			return RenderConstants.GRAY_MODE;
		} else if (cm.equals(rgb)) {
			return RenderConstants.RGB_MODE;
		}
		return null;
	}

	protected String getRenderModeMetaDataID(ColorModel cm) 
	{
		if (cm.equals(bw)) {
			return RenderModeConstants.COLOR_CONVERT_BLACK_WHITE;
		} else if (cm.equals(grey)) {
			return RenderModeConstants.COLOR_CONVERT_GREYSCALE;
		} else if (cm.equals(rgb)) {
			return RenderModeConstants.COLOR_CONVERT_RGB;
		}
		return null;
	}
		
	protected String getImageRenderDelegateClassName(ColorModel cm) 
	{
		if (cm.equals(bw)) {
			return BlackWhiteColorConvertDelegate.class.getName();
		} else if (cm.equals(grey)) {
			return GrayscaleColorConvertDelegate.class.getName();
		} else if (cm.equals(rgb)) {
			return RGBColorConvertDelegate.class.getName();
		}
		return null;		
	}
								
	private void convertBlackWhite() 
	{						
		if (!dithering) {
			convertImage = convertBlackWhiteWithoutDithering(originalImage);			
			return;
		}			
		try {		
			RenderedImage img = convertBlackWhiteWithDithering(originalImage);
			if (img != null) {
				convertImage = convertToBufferedImage(img);
				logger.debug("Dithering worked!");				
			}
		} catch (Exception e) {
			convertImage = convertBlackWhiteWithoutDithering(originalImage);
		}
	}		
		
	private BufferedImage convertBlackWhiteWithoutDithering(BufferedImage img) 
	{
		logger.debug("Dithering skipped!");					
		return convertToBufferedImage(colorConvertJDK(img, bw));		
	}
		
	private RenderedImage convertBlackWhiteWithDithering(BufferedImage src) 
	{
		long startTime = System.currentTimeMillis();
		
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put(BWDitheringColorConvertDelegate.KEY_DITHER_MODE, ditherMode);
    parameters.put(BWDitheringColorConvertDelegate.KEY_DITHER_ALGORITHM, ditherAlgorithm);
		
		String renderDelegateClassName = BWDitheringColorConvertDelegate.class.getName();
		Set<String> supportedRenderModes = new HashSet<String>();
		supportedRenderModes.add(getRenderMode(bw));
		String id = getRenderModeMetaDataID(bw);		
		RenderModeMetaData renderModeMetaData = new RenderModeMetaData(id, supportedRenderModes, 
				renderDelegateClassName, parameters, false);		
		
		renderModeMetaDatas.clear();
		renderModeMetaDatas.add(renderModeMetaData);
		
		String renderMode = getRenderMode(bw); 
		try {
			RenderedImage img = renderModeMetaData.getRendererDelegate().render(renderMode, null, src, renderModeMetaData);
			long end = System.currentTimeMillis() - startTime;
			logger.debug("Dithering took "+end+" ms!");
			return img;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}	
		
	protected BufferedImage convertToBufferedImage(RenderedImage img) 
	{
		return ImageUtil.convertToBufferedImage(img);
	}
			
	private DisposeListener disposeListener = new DisposeListener()
	{	
		public void widgetDisposed(DisposeEvent e) 
		{
			colorModel = null;
			convertImage = null;
			grey = null;
			rgb = null;
		}	
	};
	
}